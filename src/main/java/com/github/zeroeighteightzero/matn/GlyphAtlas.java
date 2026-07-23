package com.github.zeroeighteightzero.matn;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
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

/**
 * Texture atlas for font glyphs. Glyphs are packed with true Bottom-Left placement
 * (Baker–Coffman–Rivest): each glyph is placed at the lexicographically minimal
 * feasible (y, x). That is the placement rule used by FQW-ordered Bottom-Left
 * (Hougardy & Zondervan, STACS 2026). Because {@link #getGlyph} is online, the
 * offline FQW global ordering cannot be applied; placement is irrevocable BL.
 */
public class GlyphAtlas implements Disposable {

    public static class Page {

        /**
         * Axis-aligned rectangle already packed into this page.
         */
        public static class Rect {
            public final int x, y, width, height;

            public Rect(int x, int y, int width, int height) {
                this.x = x;
                this.y = y;
                this.width = width;
                this.height = height;
            }

            /**
             * True if this rect overlaps the open interval of the proposed placement.
             */
            boolean overlaps(int ox, int oy, int ow, int oh) {
                return ox < x + width && ox + ow > x && oy < y + height && oy + oh > y;
            }
        }

        public final Texture texture;
        private final Pixmap.Format format;

        final Array<Rect> placed = new Array<>();

        public Page(int pageSize, Pixmap.Format format) {
            this.format = format;
            texture = new Texture(pageSize, pageSize, format);
        }

        void place(Pixmap pixmap, int x, int y) {
            Pixmap correctPixmap = pixmap;
            if (pixmap.getFormat() != this.format) {
                correctPixmap = new Pixmap(pixmap.getWidth(), pixmap.getHeight(), format);
                correctPixmap.drawPixmap(pixmap, 0, 0);
            }
            texture.draw(correctPixmap, x, y);
        }

    }

    public static class GPUPage {
        protected final IntArray buf = new IntArray(1);
        protected final int tex;
        protected int index;

        protected GPUPage(int buf, int tex) {
            this.buf.add(buf);
            this.tex = tex;
        }

        protected GPUPage(int tex) {
            this.tex = tex;
        }
    }

    public Array<Page> pages = new Array<>(1);
    public Array<GPUPage> gpuPages = new Array<>(1);
    private final LongMap<GPUGlyph> gpuGlyphMap = new LongMap<>();
    public LongMap<Glyph> glyphMap = new LongMap<>();
    public final int pageSize;
    public final Pixmap.Format format;

    private static final int GPU_TEXTURE_WIDTH = 512;
    private static final int TEXEL_SIZE = 8;

    private final int gpuPageCapacity;
    private final int gpuPageHeight;
    private final boolean hasTexBuf;

    private void newGPUPage() {
        if (hasTexBuf) {
            int buf = Gdx.gl.glGenBuffer();
            int tex = Gdx.gl.glGenBuffer();

            Gdx.gl.glBindBuffer(GL_TEXTURE_BUFFER, buf);
            Gdx.gl.glBufferData(GL_TEXTURE_BUFFER, gpuPageCapacity * 8, null, GL_STATIC_DRAW);

            Gdx.gl.glBindTexture(GL_TEXTURE_BUFFER, tex);
            Gdx.gl32.glTexBuffer(GL_TEXTURE_BUFFER, GL_RGBA16I, buf);

            gpuPages.add(new GPUPage(buf, tex));
        } else {
            int tex = Gdx.gl.glGenTexture();

            Gdx.gl.glBindTexture(GL_TEXTURE_2D, tex);
            Gdx.gl.glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA16I, GPU_TEXTURE_WIDTH, gpuPageHeight, 0, GL_RGBA_INTEGER, GL_SHORT, null);
            Gdx.gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
            Gdx.gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

            gpuPages.add(new GPUPage(tex));
        }
    }

    public GlyphAtlas(int pageSize, Pixmap.Format format) {
        this.pageSize = pageSize;
        this.format = format;
        this.gpuPageCapacity = pageSize * pageSize;
        pages.add(new Page(pageSize, format));

        if (Gdx.gl32 != null) {
            hasTexBuf = true;
            gpuPageHeight = 0;
        } else {
            hasTexBuf = false;
            gpuPageHeight = MathUtils.ceil((gpuPageCapacity + (GPU_TEXTURE_WIDTH - 1)) / ((float) GPU_TEXTURE_WIDTH));
        }
    }

    public GlyphAtlas(int pageSize) {
        this(pageSize, Pixmap.Format.RGBA8888);
    }

    public void bindGPU(GPUGlyph glyph) {
        if (hasTexBuf) {
            Gdx.gl.glBindBuffer(GL_TEXTURE_BUFFER, gpuPages.get(glyph.page).buf.get(0));
        } else {
            Gdx.gl.glBindTexture(GL_TEXTURE_2D, gpuPages.get(glyph.page).tex);
        }
    }

    GPUGlyph createGPUGlyph(Typeface face, MatnGPU_Blob blob, ByteBuffer data, int length) {

        for (int i = 0; i <= gpuPages.size; i++) {

            if (i == gpuPages.size) {
                newGPUPage();
            }

            GPUPage page = gpuPages.get(i);

            if (page.index * TEXEL_SIZE + length > gpuPageCapacity * 8) {
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
                    int x = dstOffset % GPU_TEXTURE_WIDTH;
                    int y = dstOffset / GPU_TEXTURE_WIDTH;
                    int rowCount = GPU_TEXTURE_WIDTH - x;
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

    public GPUGlyph getGPUGlyph(Font font, long glyphID) {
        long hash = Utils.glyphHash(font, glyphID);

        if (gpuGlyphMap.containsKey(hash)) {
            return gpuGlyphMap.get(hash);
        }

        GPUGlyph glyph = font.encodeGPU(glyphID);
        gpuGlyphMap.put(hash, glyph);

        return glyph;
    }

    public Glyph getGlyph(Font font, long glyphID, int size) {
        int steppedSize = Utils.getFontSize(size);
        long hash = Utils.glyphHashWithSize(font, glyphID, steppedSize);
        if (glyphMap.containsKey(hash)) {
            return glyphMap.get(hash);
        }
        Font.GlyphMetrics metrics = font.getGlyphMetrics(glyphID);
        Pixmap pixmap = font.rasterize(glyphID, steppedSize);
        int width = pixmap.getWidth(), height = pixmap.getHeight();

        // Glyphs with zero area (e.g., space) don't consume atlas space.
        if (width == 0 || height == 0) {
            pixmap.dispose();
            Glyph glyph = new Glyph(this, glyphID, steppedSize, 0, 0, 0, 0, 0, metrics.bearingX, metrics.bearingY);
            glyphMap.put(hash, glyph);
            return glyph;
        }

        // Glyph larger than a whole page cannot be packed.
        if (width > pageSize || height > pageSize) {
            pixmap.dispose();
            throw new RuntimeException("Glyph too large for page size: "
                    + width + "x" + height + " > " + pageSize);
        }

        // Try Bottom-Left on every existing page.
        for (Page page : pages) {
            int[] pos = findBottomLeft(page, width, height);
            if (pos != null) {
                int x = pos[0], y = pos[1];
                page.placed.add(new Page.Rect(x, y, width, height));
                page.place(pixmap, x, y);

                Glyph glyph = new Glyph(this, glyphID, steppedSize, pages.indexOf(page, true),
                        x, y, width, height, metrics.bearingX, metrics.bearingY);
                glyphMap.put(hash, glyph);
                pixmap.dispose();
                return glyph;
            }
        }

        // No page could accommodate the glyph → create a new page; place at (0, 0).
        Page newPage = new Page(pageSize, format);
        pages.add(newPage);
        newPage.placed.add(new Page.Rect(0, 0, width, height));
        newPage.place(pixmap, 0, 0);

        Glyph glyph = new Glyph(this, glyphID, steppedSize, pages.size - 1, 0, 0, width, height, metrics.bearingX, metrics.bearingY);
        glyphMap.put(hash, glyph);
        pixmap.dispose();
        return glyph;
    }

    /**
     * True Bottom-Left placement (Baker et al.; used by FQW-ordered BL, STACS 2026).
     * <p>
     * By the supporter property of BL, a feasible lower-left corner is always at an
     * intersection of a vertical line through a previous right edge (or {@code x = 0})
     * and a horizontal line through a previous top edge (or {@code y = 0}). Candidates
     * are therefore complete: we enumerate those points and return the lexicographically
     * minimal feasible {@code (y, x)}.
     *
     * @return {@code {x, y}} or {@code null} if the page is full
     */
    private int[] findBottomLeft(Page page, int width, int height) {
        IntArray candX = new IntArray(page.placed.size + 1);
        IntArray candY = new IntArray(page.placed.size + 1);
        candX.add(0);
        candY.add(0);
        for (Page.Rect r : page.placed) {
            candX.add(r.x + r.width);
            candY.add(r.y + r.height);
        }
        sortUnique(candX);
        sortUnique(candY);

        int bestX = -1;
        int bestY = Integer.MAX_VALUE;

        for (int yi = 0; yi < candY.size; yi++) {
            int y = candY.get(yi);
            if (y + height > pageSize) {
                continue;
            }
            // ys are sorted ascending; once y exceeds bestY nothing better remains
            if (y > bestY) {
                break;
            }

            for (int xi = 0; xi < candX.size; xi++) {
                int x = candX.get(xi);
                if (x + width > pageSize) {
                    continue;
                }
                if (y == bestY && bestX >= 0 && x >= bestX) {
                    continue;
                }

                if (fits(page, x, y, width, height)) {
                    if (y < bestY || (y == bestY && (bestX < 0 || x < bestX))) {
                        bestX = x;
                        bestY = y;
                    }
                }
            }
        }

        if (bestX < 0) {
            return null;
        }
        return new int[]{bestX, bestY};
    }

    /**
     * Whether {@code [x, x+w) × [y, y+h)} lies entirely free inside the page.
     */
    private boolean fits(Page page, int x, int y, int w, int h) {
        if (x < 0 || y < 0 || x + w > pageSize || y + h > pageSize) {
            return false;
        }
        for (Page.Rect r : page.placed) {
            if (r.overlaps(x, y, w, h)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Sort ascending and drop duplicates in place.
     */
    private static void sortUnique(IntArray a) {
        a.sort();
        int write = 0;
        for (int read = 0; read < a.size; read++) {
            int v = a.get(read);
            if (write == 0 || v != a.get(write - 1)) {
                a.set(write++, v);
            }
        }
        a.size = write;
    }

    @Override
    public void dispose() {
        for (Page page : pages) {
            page.texture.dispose();
        }
        for (int i = 0; i < gpuPages.size; ++i) {
            GPUPage page = gpuPages.get(i);
            if (hasTexBuf) {
                Gdx.gl.glDeleteBuffer(page.buf.get(0));
            }
            Gdx.gl.glDeleteTexture(page.tex);
        }
    }
}