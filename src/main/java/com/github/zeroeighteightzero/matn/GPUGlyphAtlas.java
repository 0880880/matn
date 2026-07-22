package com.github.zeroeighteightzero.matn;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.LongMap;
import com.github.zeroeighteightzero.matn._native.structs.MatnGPU_Blob;

import java.nio.ByteBuffer;

import static com.badlogic.gdx.graphics.GL20.*;
import static com.badlogic.gdx.graphics.GL30.GL_RGBA16I;
import static com.badlogic.gdx.graphics.GL30.GL_RGBA_INTEGER;
import static com.badlogic.gdx.graphics.GL32.GL_TEXTURE_BUFFER;

public class GPUGlyphAtlas implements Disposable {

    public static class Page {
        protected final IntArray buf = new IntArray(1);
        protected final int tex;
        protected int index;

        protected Page(int buf, int tex) {
            this.buf.add(buf);
            this.tex = tex;
        }

        protected Page(int tex) {
            this.tex = tex;
        }
    }

    private static final int TEXTURE_WIDTH = 512;
    private static final int TEXEL_SIZE = 8;

    private final Array<Page> pages = new Array<>();
    private final int capacity;
    private final int height;
    private final boolean hasTexBuf;

    private final LongMap<GPUGlyph> glyphMap = new LongMap<>();

    private void newPage() {
        if (hasTexBuf) {
            int buf = Gdx.gl.glGenBuffer();
            int tex = Gdx.gl.glGenBuffer();

            Gdx.gl.glBindBuffer(GL_TEXTURE_BUFFER, buf);
            Gdx.gl.glBufferData(GL_TEXTURE_BUFFER, capacity * 8, null, GL_STATIC_DRAW);

            Gdx.gl.glBindTexture(GL_TEXTURE_BUFFER, tex);
            Gdx.gl32.glTexBuffer(GL_TEXTURE_BUFFER, GL_RGBA16I, buf);

            pages.add(new Page(buf, tex));
        } else {
            int tex = Gdx.gl.glGenTexture();

            Gdx.gl.glBindTexture(GL_TEXTURE_2D, tex);
            Gdx.gl.glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA16I, TEXTURE_WIDTH, height, 0, GL_RGBA_INTEGER, GL_SHORT, null);
            Gdx.gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
            Gdx.gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

            pages.add(new Page(tex));
        }
    }

    public GPUGlyphAtlas() {
        this(512 * 512);
    }

    public GPUGlyphAtlas(int texels) {

        capacity = texels;

        if (Gdx.gl32 != null) {
            hasTexBuf = true;
            height = 0;
        } else {
            hasTexBuf = false;
            height = MathUtils.ceil((texels + (TEXTURE_WIDTH - 1)) / ((float) TEXTURE_WIDTH));
        }

        newPage();

    }

    GPUGlyph createGlyph(Typeface face, MatnGPU_Blob blob, ByteBuffer data, int length) {

        for (int i = 0; i <= pages.size; i++) {

            if (i == pages.size) {
                newPage();
            }

            Page page = pages.get(i);

            if (page.index * TEXEL_SIZE + length > capacity * 8) {
                continue;
            }

            GPUGlyph glyph = new GPUGlyph(
                    this,
                    (float) face.upem,
                    page.index,
                    blob.min_x(),
                    blob.min_y(),
                    blob.max_x(),
                    blob.max_y(),
                    length / TEXEL_SIZE,
                    i
            );

            if (hasTexBuf) {
                data.position(0);
                Gdx.gl.glBufferSubData(GL_TEXTURE_BUFFER, page.index * TEXEL_SIZE, 0, data); // size ignored
                page.index += glyph.length;
            } else {
                Gdx.gl.glBindTexture(GL_TEXTURE_2D, page.tex);
                data.position(0);
                int remaining = glyph.length;
                int srcOffset = 0;
                int dstOffset = page.index;
                while (remaining > 0) {
                    int x = dstOffset % TEXTURE_WIDTH;
                    int y = dstOffset / TEXTURE_WIDTH;
                    int rowCount = TEXTURE_WIDTH - x;
                    if (rowCount > remaining)
                        rowCount = remaining;
                    data.position(srcOffset * TEXEL_SIZE);
                    Gdx.gl.glTexSubImage2D(GL_TEXTURE_2D, 0,
                            x, y, rowCount, 1,
                            GL_RGBA_INTEGER, GL_SHORT,
                            data);
                    srcOffset += rowCount;
                    dstOffset += rowCount;
                    remaining -= rowCount;
                }
                page.index = dstOffset;
            }

            return glyph;
        }

        throw new RuntimeException("No more space available for glyph");

    }

    public void bind(GPUGlyph glyph) {
        if (hasTexBuf) {
            Gdx.gl.glBindBuffer(GL_TEXTURE_BUFFER, pages.get(glyph.page).buf.get(0));
        } else {
            Gdx.gl.glBindTexture(GL_TEXTURE_2D, pages.get(glyph.page).tex);
        }
    }

    @Override
    public void dispose() {
        for (int i = 0; i < pages.size; ++i) {
            Page page = pages.get(i);
            if (hasTexBuf) {
                Gdx.gl.glDeleteBuffer(page.buf.get(0));
            }
            Gdx.gl.glDeleteTexture(page.tex);
        }
    }

    public GPUGlyph getGlyph(Font font, long glyphID) {
        long hash = Utils.glyphHash(font, glyphID);

        if (glyphMap.containsKey(hash)) {
            return glyphMap.get(hash);
        }

        GPUGlyph glyph = font.encodeGPU(glyphID, this);
        glyphMap.put(hash, glyph);

        return glyph;
    }

}
