package com.github.zeroeighteightzero.matn;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.jnigen.runtime.CHandler;
import com.badlogic.gdx.jnigen.runtime.pointer.FloatPointer;
import com.badlogic.gdx.jnigen.runtime.pointer.PointerPointer;
import com.badlogic.gdx.jnigen.runtime.pointer.integer.BytePointer;
import com.badlogic.gdx.jnigen.runtime.pointer.integer.SIntPointer;
import com.badlogic.gdx.jnigen.runtime.pointer.integer.UBytePointer;
import com.badlogic.gdx.jnigen.runtime.pointer.integer.UShortPointer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.BufferUtils;
import com.badlogic.gdx.utils.Disposable;
import com.github.zeroeighteightzero.matn._native.Matn;
import com.github.zeroeighteightzero.matn._native.enums.MatnGPU_LANGUAGE;
import com.github.zeroeighteightzero.matn._native.enums.MatnPixelFormat;
import com.github.zeroeighteightzero.matn._native.structs.*;

import java.nio.ByteBuffer;

public class Font implements Disposable {

    public String name = "Unnamed Font";

    final Typeface face;
    final MatnFont.MatnFontPointer mtFont;

    private float ascender, descender, lineGap, lineHeight;

    protected final float[] varCoords;
    protected float boldX, boldY, slant;
    protected boolean boldInPlace;

    private final Layout layout = new Layout(this, 1);

    public static class GlyphMetrics {
        public final float width, height;
        public final float bearingX, bearingY;

        GlyphMetrics(float width, float height, float bearingX, float bearingY) {
            this.width = width;
            this.height = height;
            this.bearingX = bearingX;
            this.bearingY = bearingY;
        }
    }

    public static class ShapeResult {
        public final Vector2[] advances;
        public final Vector2[] offsets;
        public final long[] glyphIDs;
        public final long[] clusters;
        public final boolean rtl;

        public ShapeResult(Vector2[] advances, Vector2[] offsets, long[] glyphIDs, long[] clusters, boolean rtl) {
            this.advances = advances;
            this.offsets = offsets;
            this.glyphIDs = glyphIDs;
            this.clusters = clusters;
            this.rtl = rtl;
        }
    }

    Font(Typeface face) {
        this.face = face;

        PointerPointer<MatnFont.MatnFontPointer> ptr = new PointerPointer<>(MatnFont.MatnFontPointer::new);
        Matn.matn_typeface_create_font(face.mtFace, ptr);
        this.mtFont = ptr.getValue();

        varCoords = new float[this.face.varAxes.length];
        for (int i = 0; i < varCoords.length; i++) {
            varCoords[i] = this.face.varAxes[i].def;
        }

        ascender = Matn.matn_font_get_ascender(mtFont);
        descender = Matn.matn_font_get_descender(mtFont);
        lineGap = Matn.matn_font_get_line_gap(mtFont);
        lineHeight = ascender - descender + lineGap;

        FloatPointer bxPtr = new FloatPointer(1, true);
        FloatPointer byPtr = new FloatPointer(1, true);
        SIntPointer inPlacePtr = new SIntPointer(1, true);
        Matn.matn_font_get_synthetic_bold(mtFont, bxPtr, byPtr, inPlacePtr);

        this.boldX = bxPtr.getFloat(0);
        this.boldY = byPtr.getFloat(0);
        this.boldInPlace = inPlacePtr.getInt(0) != 0;

        this.slant = Matn.matn_font_get_synthetic_slant(mtFont);

    }

    public void setVariableAxis(String tag, float value) {
        if (tag.length() != 4) {
            return;
        }
        for (int i = 0; i < face.varAxes.length; i++) {
            VarAxis v = this.face.varAxes[i];
            if (v.tag.equals(tag)) {
                float clamped = MathUtils.clamp(value, v.min, v.max);
                this.varCoords[i] = clamped;
                return;
            }
        }
        throw new RuntimeException("Typeface does not have '" + tag + "' tag");
    }

    public void italic(float value) {
        setVariableAxis("ital", value);
    }

    public void opticalSize(float value) {
        setVariableAxis("opsz", value);
    }

    public void weight(float value) {
        setVariableAxis("wght", value);
    }

    public void width(float value) {
        setVariableAxis("wdth", value);
    }

    public void slant(float value) {
        setVariableAxis("slnt", value);
    }

    public void applyVariation() {
        FloatPointer coords = new FloatPointer(this.varCoords.length);
        for (int i = 0; i < this.varCoords.length; ++i) {
            coords.setFloat(this.varCoords[i], i);
        }
        Matn.matn_font_set_var_coords(mtFont, coords, this.varCoords.length);

        ascender = Matn.matn_font_get_ascender(mtFont);
        descender = Matn.matn_font_get_descender(mtFont);
        lineGap = Matn.matn_font_get_line_gap(mtFont);
        lineHeight = ascender - descender + lineGap;
    }

    public void setNamedInstance(NamedInstance instance) {
        if (instance.coords.length != this.varCoords.length) {
            throw new RuntimeException("Coordinates size doesn't match.");
        }
        System.arraycopy(instance.coords, 0, this.varCoords, 0, this.varCoords.length);
        applyVariation();
    }

    public void setNamedInstance(String name) {
        for (int i = 0; i < face.namedInstances.length; ++i) {
            if (face.namedInstances[i].name.equals(name)) {
                setNamedInstance(face.namedInstances[i]);
                return;
            }
        }
        throw new RuntimeException("Named instance not found");
    }

    public long getGlyphID(int codepoint) {
        return Matn.matn_font_get_glyph_id(mtFont, codepoint);
    }

    public long getGlyphID(char codepoint) {
        return getGlyphID((int) codepoint);
    }

    public GlyphMetrics getGlyphMetrics(long glyphID) {
        MatnGlyphMetrics.MatnGlyphMetricsPointer ptr = new MatnGlyphMetrics.MatnGlyphMetricsPointer();
        Matn.matn_font_get_glyph_metrics(mtFont, glyphID, ptr);
        MatnGlyphMetrics gm = ptr.get();
        return new GlyphMetrics(gm.width(), gm.height(), gm.bearing_x(), gm.bearing_y());
    }

    public static class Paragraph {
        protected final UShortPointer ptr;
        public final int length;

        public Paragraph(String text) {
            if (text == null || text.isEmpty()) {
                ptr = null;
                length = 0;
                return;
            }
            ptr = new UShortPointer(text.length(), false);
            length = text.length();
            for (int i = 0; i < length; ++i) {
                ptr.setUShort(text.charAt(i), i);
            }
        }
    }

    public ShapeResult shape(Paragraph paragraph) {
        return shape(paragraph, 0, paragraph.length);
    }

    public ShapeResult shape(Paragraph paragraph, int offset, int length) {
        if (paragraph.length == 0) {
            return new ShapeResult(new Vector2[0], new Vector2[0], new long[0], new long[0], false);
        }
        Matn.matn_shape_set_utf16(mtFont, paragraph.ptr, paragraph.length, offset, length);
        Matn.matn_shape(mtFont);

        MatnBufferView bufferView = Matn.matn_shape_view_buffer(mtFont).get();

        int shapeLength = (int) bufferView.length();
        ShapeResult res = new ShapeResult(new Vector2[shapeLength], new Vector2[shapeLength], new long[shapeLength], new long[shapeLength], Matn.matn_shape_is_rtl(mtFont) != 0);

        for (int i = 0; i < shapeLength; ++i) {
            res.advances[i] = new Vector2(bufferView.x_advances().getFloat(i), bufferView.y_advances().getFloat(i));
            res.offsets[i] = new Vector2(bufferView.x_offsets().getFloat(i), bufferView.y_offsets().getFloat(i));
            res.glyphIDs[i] = bufferView.glyph_ids().getUInt(i);
            res.clusters[i] = bufferView.clusters().getUInt(i);
        }

        return res;
    }

    public Pixmap rasterize(long glyphID, int size) {
        PointerPointer<MatnBlob.MatnBlobPointer> ptr = new PointerPointer<>(MatnBlob.MatnBlobPointer::new);
        Matn.matn_rasterize_glyph(mtFont, glyphID, size, ptr);
        MatnBlob.MatnBlobPointer blob = ptr.getValue();
        UBytePointer data = Matn.matn_blob_get_data(blob);
        int stride = Matn.matn_blob_get_stride(blob);
        int width = Matn.matn_blob_get_width(blob);
        int height = Matn.matn_blob_get_height(blob);
        MatnPixelFormat format = Matn.matn_blob_get_format(blob);
        Pixmap pixmap;
        if (format == MatnPixelFormat.MATN_PIXEL_FORMAT_A8) {
            pixmap = new Pixmap(width, height, Pixmap.Format.Alpha);
            if (width == stride) {
                int numBytes = width * height;
                ByteBuffer buffer = BufferUtils.newUnsafeByteBuffer(numBytes);
                CHandler.memcpy(BufferUtils.getUnsafeBufferAddress(buffer), data.getPointer(), numBytes);
                pixmap.setPixels(buffer);
                BufferUtils.disposeUnsafeByteBuffer(buffer);
            } else {
                pixmap = new Pixmap(width, height, Pixmap.Format.Alpha);
                ByteBuffer pixels = pixmap.getPixels();
                long pixelsPtr = BufferUtils.getUnsafeBufferAddress(pixels);
                for (int i = 0; i < height; i++) {
                    CHandler.memcpy(pixelsPtr + (long) width * i, data.getPointer() + (long) stride * i, width);
                }
                pixels.flip();
            }
        } else {
            // MATN_PIXEL_FORMAT_BGRA32
            pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);
            for (int i = 0; i < height; ++i) {
                for (int j = 0; j < width; ++j) {
                    int index = i * stride + j;
                    int pixel = index * 4;
                    int b = data.getUByte(pixel) & 0xFF;
                    int g = data.getUByte(pixel + 1) & 0xFF;
                    int r = data.getUByte(pixel + 2) & 0xFF;
                    int a = data.getUByte(pixel + 3) & 0xFF;
                    pixmap.drawPixel(j, height - i - 1, (r << 24) | (g << 16) | (b << 8) | a);
                }
            }
        }

        Matn.matn_blob_destroy(blob);

        return pixmap;
    }

    public GPUGlyph encodeGPU(long glyphID, GPUGlyphAtlas atlas) {
        PointerPointer<MatnGPU_Blob.MatnGPU_BlobPointer> ptr = new PointerPointer<>(MatnGPU_Blob.MatnGPU_BlobPointer::new);
        Matn.matn_gpu_draw_glyph(mtFont, glyphID, ptr);
        MatnGPU_Blob.MatnGPU_BlobPointer gpuBlob = ptr.getValue();
        BytePointer data = gpuBlob.get().data();
        long length = gpuBlob.get().length();

        ByteBuffer dataBuffer = BufferUtils.newByteBuffer((int) length);
        CHandler.memcpy(BufferUtils.getUnsafeBufferAddress(dataBuffer), data.getPointer(), length);

        return atlas.createGlyph(face, gpuBlob.get(), dataBuffer, (int) length);
    }

    public static String getVertexShader() {
        return Matn.matn_gpu_get_vertex(MatnGPU_LANGUAGE.MATN_GPU_LANGUAGE_GLSL).getString();
    }

    public static String getFragmentShader() {
        return Matn.matn_gpu_get_fragment(MatnGPU_LANGUAGE.MATN_GPU_LANGUAGE_GLSL).getString();
    }

    public float[] getVarCoords() {
        return varCoords;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getAscender(float size) {
        return ascender * size;
    }

    public float getDescender(float size) {
        return descender * size;
    }

    public float getLineGap(float size) {
        return lineGap * size;
    }

    public float getLineHeight(float size) {
        return lineHeight * size;
    }

    public float getUnscaledAscender() {
        return ascender;
    }

    public float getUnscaledDescender() {
        return descender;
    }

    public float getUnscaledLineGap() {
        return lineGap;
    }

    public float getUnscaledLineHeight() {
        return lineHeight;
    }

    public float getSyntheticBoldX() {
        return boldX;
    }

    public float getSyntheticBoldY() {
        return boldY;
    }

    public boolean getSyntheticBoldInPlace() {
        return boldInPlace;
    }

    public void setSyntheticBold(float boldX, float boldY, boolean inPlace) {
        this.boldX = boldX;
        this.boldY = boldY;
        this.boldInPlace = inPlace;
        Matn.matn_font_set_synthetic_bold(mtFont, boldX, boldY, inPlace ? 1 : 0);
    }

    public float getSyntheticSlant() {
        return slant;
    }

    public void setSyntheticSlant(float slant) {
        this.slant = slant;
        Matn.matn_font_set_synthetic_slant(mtFont, slant);
    }

    private Matrix4 mat = new Matrix4();

    public void drawGlyph(Batch batch, GlyphAtlas atlas, long glyphID, float fontSize, float x, float y, float sx, float sy, float rot) {
        Glyph glyph = atlas.getGlyph(this, glyphID, (int) fontSize);
        float scale = fontSize / glyph.size;
        float width = glyph.width * scale;
        float height = glyph.height * scale;
        float cx = width * .5f;
        float cy = height * .5f;
        mat.idt();
        mat.translate(x + cx, y + cy, 0);
        mat.rotateRad(0, 0, 1, rot);
        mat.scale(sx, sy, 1);
        mat.translate(-cx, -cy, 0);
        batch.setTransformMatrix(mat);
        batch.draw(atlas.pages.get(glyph.page).texture, glyph.bearingX * fontSize, -glyph.height * scale + glyph.bearingY * fontSize, width, height, glyph.u, glyph.v, glyph.u2, glyph.v2);
    }

    public void drawText(Batch batch, GlyphAtlas atlas, Layout layout, float x, float y) {
        int idx = 0;
        float penX = x;
        float penY = y;
        for (int i = 0; i < layout.lines.size; ++i) {
            Line line = layout.lines.get(i);
            for (int j = 0; j < line.glyphs.size; ++j) {
                drawGlyph(batch, atlas, line.glyphs.get(j), layout.fontSize, penX + layout.offsets.get(idx * 2), penY + layout.offsets.get(idx * 2 + 1), layout.sizing.get(idx * 2), layout.sizing.get(idx * 2 + 1), layout.rotation.get(idx));
                penX += layout.advances.get(idx);
                ++idx;
            }
            penX = x;
            penY -= layout.lineHeight;
        }
    }

    public void drawGPUGlyph(GPUTextBatch batch, GPUGlyphAtlas atlas, long glyphID, float fontSize, float x, float y) {
        batch.drawGlyph(atlas.getGlyph(this, glyphID), fontSize, x, y);
    }

    public void drawGPUText(GPUTextBatch batch, GPUGlyphAtlas atlas, Layout layout, float x, float y) {
        int idx = 0;
        for (int i = 0; i < layout.lines.size; ++i) {
            Line line = layout.lines.get(i);
            float penX = 0;
            for (int j = 0; j < line.glyphs.size; ++j) {
                drawGPUGlyph(batch, atlas, line.glyphs.get(j), layout.fontSize, x + penX + layout.offsets.get(idx * 2), y - i * layout.lineHeight + layout.offsets.get(idx * 2 + 1));
                penX += layout.advances.get(idx);
                ++idx;
            }
        }
    }

    public void drawText(Batch batch, GlyphAtlas atlas, String text, float fontSize, float x, float y) {
        layout.setText(text);
        layout.fontSize(fontSize);
        drawText(batch, atlas, layout, x, y);
    }

    @Override
    public void dispose() {
        Matn.matn_font_destroy(mtFont);
    }
}
