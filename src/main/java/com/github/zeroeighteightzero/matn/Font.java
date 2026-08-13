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
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.BufferUtils;
import com.badlogic.gdx.utils.Disposable;
import com.github.zeroeighteightzero.matn._native.Matn;
import com.github.zeroeighteightzero.matn._native.enums.MatnGPU_LANGUAGE;
import com.github.zeroeighteightzero.matn._native.enums.MatnPixelFormat;
import com.github.zeroeighteightzero.matn._native.structs.*;
import com.github.zeroeighteightzero.matn._native.structs.*;

import java.nio.ByteBuffer;

/**
 * A text font built from a {@link Typeface} and rendered through a {@link GlyphAtlas}.
 *
 * <p>A font wraps a native text-engine instance and holds variable-font coordinates,
 * synthetic-bold and synthetic-slant settings, and cached vertical metrics. Use it to
 * look up and rasterize glyphs, shape paragraphs, and draw text with either a standard
 * LibGDX {@link Batch} or a {@link GPUTextBatch}.</p>
 */
public class Font implements Disposable {

    public String name = "Unnamed Font";

    protected final Typeface face;
    protected final GlyphAtlas atlas;
    protected final MatnFont.MatnFontPointer mtFont;

    private float ascender, descender, lineGap, lineHeight;

    protected final float[] varCoords;
    protected float boldX, boldY, slant;
    protected boolean boldInPlace;

    private final Layout layout = new Layout(this, 1);

    /**
     * Layout metrics describing the size and bearings of a glyph.
     */
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

    /**
     * The result of shaping a paragraph of text.
     *
     * <p>For each shaped glyph the arrays {@link #advances}, {@link #offsets},
     * {@link #glyphIDs}, and {@link #clusters} contain one corresponding entry.
     * {@link #rtl} indicates whether the buffer was shaped right-to-left.</p>
     */
    public static class ShapeResult {
        public final Vector2[] advances;
        public final Vector2[] offsets;
        public final long[] glyphIDs;
        public final long[] clusters;
        public final boolean rtl;

        /**
         * Creates a shaping result with the given per-glyph data.
         *
         * @param advances the horizontal and vertical advance of each glyph
         * @param offsets the horizontal and vertical offset of each glyph
         * @param glyphIDs the font-specific glyph identifier of each glyph
         * @param clusters the source UTF-16 cluster index of each glyph
         * @param rtl whether the shaped buffer is right-to-left
         */
        public ShapeResult(Vector2[] advances, Vector2[] offsets, long[] glyphIDs, long[] clusters, boolean rtl) {
            this.advances = advances;
            this.offsets = offsets;
            this.glyphIDs = glyphIDs;
            this.clusters = clusters;
            this.rtl = rtl;
        }
    }

    public Font(Typeface face, GlyphAtlas atlas) {
        this.face = face;
        this.atlas = atlas;

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

    /**
     * Sets the value of a variable font axis.
     *
     * <p>The value is clamped to the axis's supported minimum and maximum range.
     * Call {@link #applyVariation()} after changing one or more axes to apply
     * the pending variation coordinates to the native font.</p>
     *
     * @param tag the four-character OpenType variation-axis tag
     * @param value the requested axis value
     * @throws RuntimeException if this typeface does not contain the specified axis
     */
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

    /**
     * Gets the pending value of a variable font axis.
     *
     * <p>The returned value is the locally configured coordinate. It may not be
     * reflected by the native font until {@link #applyVariation()} is called.</p>
     *
     * @param tag the four-character OpenType variation-axis tag
     * @return the configured axis value, or {@code 0} if {@code tag} is not four characters long
     * @throws RuntimeException if this typeface does not contain the specified axis
     */
    public float getVariableAxis(String tag) {
        if (tag.length() != 4) {
            return 0;
        }
        for (int i = 0; i < face.varAxes.length; i++) {
            VarAxis v = this.face.varAxes[i];
            if (v.tag.equals(tag)) {
                return this.varCoords[i];
            }
        }
        throw new RuntimeException("Typeface does not have '" + tag + "' tag");
    }

    /**
     * Sets the {@code ital} variable-font axis.
     *
     * @param value the italic-axis value
     */
    public void italic(float value) {
        setVariableAxis("ital", value);
    }

    /**
     * Sets the {@code opsz} variable-font axis.
     *
     * @param value the optical-size axis value
     */
    public void opticalSize(float value) {
        setVariableAxis("opsz", value);
    }

    /**
     * Sets the {@code wght} variable-font axis.
     *
     * @param value the weight-axis value
     */
    public void weight(float value) {
        setVariableAxis("wght", value);
    }

    /**
     * Sets the {@code wdth} variable-font axis.
     *
     * @param value the width-axis value
     */
    public void width(float value) {
        setVariableAxis("wdth", value);
    }

    /**
     * Sets the {@code slnt} variable-font axis.
     *
     * @param value the slant-axis value
     */
    public void slant(float value) {
        setVariableAxis("slnt", value);
    }

    /**
     * Gets the pending value of the {@code ital} variable-font axis.
     *
     * @return the configured italic-axis value
     */
    public float italic() {
        return getVariableAxis("ital");
    }

    /**
     * Gets the pending value of the {@code opsz} variable-font axis.
     *
     * @return the configured optical-size axis value
     */
    public float opticalSize() {
        return getVariableAxis("opsz");
    }

    /**
     * Gets the pending value of the {@code wght} variable-font axis.
     *
     * @return the configured weight-axis value
     */
    public float weight() {
        return getVariableAxis("wght");
    }

    /**
     * Gets the pending value of the {@code wdth} variable-font axis.
     *
     * @return the configured width-axis value
     */
    public float width() {
        return getVariableAxis("wdth");
    }

    /**
     * Gets the pending value of the {@code slnt} variable-font axis.
     *
     * @return the configured slant-axis value
     */
    public float slant() {
        return getVariableAxis("slnt");
    }

    /**
     * Applies the currently configured variable-font coordinates to this font.
     *
     * <p>This also refreshes the ascender, descender, line gap, and line-height
     * metrics to reflect the selected variation.</p>
     */
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

    /**
     * Applies a named variation instance to this font.
     *
     * @param instance the named instance whose coordinates should be applied
     * @throws RuntimeException if the instance coordinate count does not match this font
     */
    public void setNamedInstance(NamedInstance instance) {
        if (instance.coords.length != this.varCoords.length) {
            throw new RuntimeException("Coordinates size doesn't match.");
        }
        System.arraycopy(instance.coords, 0, this.varCoords, 0, this.varCoords.length);
        applyVariation();
    }

    /**
     * Applies the named variation instance with the specified name.
     *
     * @param name the name of the variation instance
     * @throws RuntimeException if no named instance with the specified name exists
     */
    public void setNamedInstance(String name) {
        for (int i = 0; i < face.namedInstances.length; ++i) {
            if (face.namedInstances[i].name.equals(name)) {
                setNamedInstance(face.namedInstances[i]);
                return;
            }
        }
        throw new RuntimeException("Named instance not found");
    }

    /**
     * Gets the glyph identifier associated with a Unicode code point.
     *
     * @param codepoint the Unicode code point
     * @return the font-specific glyph identifier, or the missing-glyph identifier when unavailable
     */
    public long getGlyphID(int codepoint) {
        return Matn.matn_font_get_glyph_id(mtFont, codepoint);
    }

    /**
     * Gets the glyph identifier associated with a UTF-16 character.
     *
     * @param codepoint the character to look up
     * @return the font-specific glyph identifier, or the missing-glyph identifier when unavailable
     */
    public long getGlyphID(char codepoint) {
        return getGlyphID((int) codepoint);
    }

    /**
     * Gets layout metrics for a glyph.
     *
     * @param glyphID the font-specific glyph identifier
     * @return the glyph's width, height, horizontal bearing, and vertical bearing
     */
    public GlyphMetrics getGlyphMetrics(long glyphID) {
        MatnGlyphMetrics.MatnGlyphMetricsPointer ptr = new MatnGlyphMetrics.MatnGlyphMetricsPointer();
        Matn.matn_font_get_glyph_metrics(mtFont, glyphID, ptr);
        MatnGlyphMetrics gm = ptr.get();
        return new GlyphMetrics(gm.width(), gm.height(), gm.bearing_x(), gm.bearing_y());
    }

    /**
     * A UTF-16 text paragraph shaped as a single unit.
     *
     * <p>Constructing a paragraph copies the text into a native-side buffer, making it
     * suitable for passing to the shaping functions in {@link Font}.</p>
     */
    public static class Paragraph {
        protected final UShortPointer ptr;
        public final int length;

        /**
         * Creates a paragraph from the given text.
         *
         * @param text the paragraph text; {@code null} or empty yields an empty paragraph
         */
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

    /**
     * Shapes the entire paragraph using this font.
     *
     * @param paragraph the UTF-16 paragraph to shape
     * @return the shaping result containing glyph IDs, advances, offsets, clusters, and direction
     */
    public ShapeResult shape(Paragraph paragraph) {
        return shape(paragraph, 0, paragraph.length);
    }

    /**
     * Shapes a range of UTF-16 code units within a paragraph.
     *
     * @param paragraph the UTF-16 paragraph to shape
     * @param offset the starting UTF-16 code-unit offset
     * @param length the number of UTF-16 code units to shape
     * @return the shaping result containing glyph IDs, advances, offsets, clusters, and direction
     */
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

    public static class GlyphRasterData {
        public final Pixmap pixmap;
        public final int top, left;

        public GlyphRasterData(Pixmap pixmap, int top, int left) {
            this.pixmap = pixmap;
            this.top = top;
            this.left = left;
        }
    }

    /**
     * Rasterizes a glyph into a pixmap at the requested size.
     *
     * @param glyphID the font-specific glyph identifier
     * @param size the rasterization size
     * @return a newly created pixmap containing the rasterized glyph
     */
    public GlyphRasterData rasterize(long glyphID, int size) {
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

        GlyphRasterData rasterData = new GlyphRasterData(pixmap, Matn.matn_blob_get_top(blob), Matn.matn_blob_get_left(blob));

        Matn.matn_blob_destroy(blob);

        return rasterData;
    }

    /**
     * Encodes a glyph into the GPU representation used by this font's glyph atlas.
     *
     * @param glyphID the font-specific glyph identifier
     * @return the encoded GPU glyph
     */
    public GPUGlyph encodeGPU(long glyphID) {
        PointerPointer<MatnGPU_Blob.MatnGPU_BlobPointer> ptr = new PointerPointer<>(MatnGPU_Blob.MatnGPU_BlobPointer::new);
        Matn.matn_gpu_draw_glyph(mtFont, glyphID, ptr);
        MatnGPU_Blob.MatnGPU_BlobPointer gpuBlob = ptr.getValue();
        BytePointer data = gpuBlob.get().data();
        long length = gpuBlob.get().length();

        ByteBuffer dataBuffer = BufferUtils.newByteBuffer((int) length);
        CHandler.memcpy(BufferUtils.getUnsafeBufferAddress(dataBuffer), data.getPointer(), length);

        return atlas.createGPUGlyph(face, gpuBlob.get(), dataBuffer, (int) length);
    }

    /**
     * Gets the GLSL vertex shader source required for GPU glyph rendering.
     *
     * @return the GLSL vertex shader source code
     */
    public static String getVertexShader() {
        return Matn.matn_gpu_get_vertex(MatnGPU_LANGUAGE.MATN_GPU_LANGUAGE_GLSL).getString();
    }

    /**
     * Gets the GLSL fragment shader source required for GPU glyph rendering.
     *
     * @return the GLSL fragment shader source code
     */
    public static String getFragmentShader() {
        return Matn.matn_gpu_get_fragment(MatnGPU_LANGUAGE.MATN_GPU_LANGUAGE_GLSL).getString();
    }

    /**
     * Gets the current variable-font coordinates.
     *
     * @return the mutable array of variation-axis coordinates
     */
    public float[] getVarCoords() {
        return varCoords;
    }

    /**
     * Gets this font's display name.
     *
     * @return the font name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets this font's display name.
     *
     * @param name the new font name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the ascender scaled to the specified font size.
     *
     * @param size the font size
     * @return the scaled ascender
     */
    public float getAscender(float size) {
        return ascender * size;
    }

    /**
     * Gets the descender scaled to the specified font size.
     *
     * @param size the font size
     * @return the scaled descender
     */
    public float getDescender(float size) {
        return descender * size;
    }

    /**
     * Gets the line gap scaled to the specified font size.
     *
     * @param size the font size
     * @return the scaled line gap
     */
    public float getLineGap(float size) {
        return lineGap * size;
    }

    /**
     * Gets the total line height scaled to the specified font size.
     *
     * @param size the font size
     * @return the scaled line height
     */
    public float getLineHeight(float size) {
        return lineHeight * size;
    }

    /**
     * Gets the font's unscaled ascender.
     *
     * @return the unscaled ascender
     */
    public float getUnscaledAscender() {
        return ascender;
    }

    /**
     * Gets the font's unscaled descender.
     *
     * @return the unscaled descender
     */
    public float getUnscaledDescender() {
        return descender;
    }

    /**
     * Gets the font's unscaled line gap.
     *
     * @return the unscaled line gap
     */
    public float getUnscaledLineGap() {
        return lineGap;
    }

    /**
     * Gets the font's unscaled total line height.
     *
     * @return the unscaled line height
     */
    public float getUnscaledLineHeight() {
        return lineHeight;
    }

    /**
     * Gets the horizontal synthetic-bold amount.
     *
     * @return the horizontal synthetic-bold amount
     */
    public float getSyntheticBoldX() {
        return boldX;
    }

    /**
     * Gets the vertical synthetic-bold amount.
     *
     * @return the vertical synthetic-bold amount
     */
    public float getSyntheticBoldY() {
        return boldY;
    }

    /**
     * Determines whether synthetic bold is applied in place.
     *
     * @return {@code true} if synthetic bold is applied in place
     */
    public boolean getSyntheticBoldInPlace() {
        return boldInPlace;
    }

    /**
     * Configures synthetic bold for this font.
     *
     * @param boldX the horizontal bold amount
     * @param boldY the vertical bold amount
     * @param inPlace whether bolding should be applied in place
     */
    public void setSyntheticBold(float boldX, float boldY, boolean inPlace) {
        this.boldX = boldX;
        this.boldY = boldY;
        this.boldInPlace = inPlace;
        Matn.matn_font_set_synthetic_bold(mtFont, boldX, boldY, inPlace ? 1 : 0);
    }

    /**
     * Sets synthetic bold using the same amount for both axes.
     *
     * @param bold the horizontal and vertical bold amount
     * @param inPlace whether bolding should be applied in place
     */
    public void setSyntheticBold(float bold, boolean inPlace) {
        setSyntheticBold(bold, bold, inPlace);
    }

    /**
     * Sets synthetic bold using the specified amount and applies it out of place.
     *
     * @param bold the horizontal and vertical bold amount
     */
    public void setSyntheticBold(float bold) {
        setSyntheticBold(bold, bold, false);
    }

    /**
     * Gets the synthetic slant value.
     *
     * @return the synthetic slant value
     */
    public float getSyntheticSlant() {
        return slant;
    }

    /**
     * Sets the synthetic slant value for this font.
     *
     * @param slant the synthetic slant value
     */
    public void setSyntheticSlant(float slant) {
        this.slant = slant;
        Matn.matn_font_set_synthetic_slant(mtFont, slant);
    }

    /**
     * Draws a single glyph using a standard LibGDX batch.
     *
     * @param batch the batch used to draw the glyph
     * @param glyphID the font-specific glyph identifier
     * @param fontSize the desired font size
     * @param x the glyph origin x-coordinate
     * @param y the glyph origin y-coordinate
     * @param sx the horizontal scale factor
     * @param sy the vertical scale factor
     * @param rot the rotation in radians
     */
    public void drawGlyph(Batch batch, long glyphID, float fontSize, float x, float y, float sx, float sy, float rot) {
        Glyph glyph = atlas.getGlyph(this, glyphID, (int) fontSize);
        float scale = fontSize / glyph.size;
        float width = glyph.width * scale;
        float height = glyph.height * scale;
        float cx = width * .5f;
        float cy = height * .5f;
        float drawX = glyph.left * scale + x;
        float drawY = glyph.top * scale + y;

        batch.draw(
                atlas.pages.get(glyph.page).texture,
                drawX,
                drawY,
                cx,
                cy,
                width,
                height,
                sx,
                sy,
                rot * MathUtils.radiansToDegrees,
                glyph.x,
                glyph.y,
                glyph.width,
                glyph.height,
                false,
                true
        );
    }

    /**
     * Draws a prepared text layout using a standard LibGDX batch.
     *
     * @param batch the batch used to draw the text
     * @param layout the prepared layout to render
     * @param x the x-coordinate of the layout origin
     * @param y the y-coordinate of the first line baseline
     */
    public void drawText(Batch batch, Layout layout, float x, float y) {
        int idx = 0;
        float penX = x;
        float penY = y;
        for (int i = 0; i < layout.lines.size; ++i) {
            Line line = layout.lines.get(i);
            for (int j = 0; j < line.glyphs.size; ++j) {
                drawGlyph(batch, line.glyphs.get(j), layout.fontSize, penX + layout.offsets.get(idx * 2), penY + layout.offsets.get(idx * 2 + 1), layout.sizing.get(idx * 2), layout.sizing.get(idx * 2 + 1), layout.rotation.get(idx));
                penX += layout.advances.get(idx);
                ++idx;
            }
            penX = x;
            penY -= layout.lineHeight;
        }
    }

    /**
     * Draws a single glyph using a GPU text batch.
     *
     * @param batch the GPU text batch used to draw the glyph
     * @param glyphID the font-specific glyph identifier
     * @param fontSize the desired font size
     * @param x the glyph origin x-coordinate
     * @param y the glyph origin y-coordinate
     */
    public void drawGPUGlyph(GPUTextBatch batch, long glyphID, float fontSize, float x, float y) {
        batch.drawGlyph(atlas.getGPUGlyph(this, glyphID), fontSize, x, y);
    }

    /**
     * Draws a prepared text layout using a GPU text batch.
     *
     * @param batch the GPU text batch used to draw the text
     * @param layout the prepared layout to render
     * @param x the x-coordinate of the layout origin
     * @param y the y-coordinate of the first line baseline
     */
    public void drawGPUText(GPUTextBatch batch, Layout layout, float x, float y) {
        int idx = 0;
        for (int i = 0; i < layout.lines.size; ++i) {
            Line line = layout.lines.get(i);
            float penX = 0;
            for (int j = 0; j < line.glyphs.size; ++j) {
                drawGPUGlyph(batch, line.glyphs.get(j), layout.fontSize, x + penX + layout.offsets.get(idx * 2), y - i * layout.lineHeight + layout.offsets.get(idx * 2 + 1));
                penX += layout.advances.get(idx);
                ++idx;
            }
        }
    }

    /**
     * Lays out and draws text using a standard LibGDX batch.
     *
     * <p>This method reuses an internal layout instance and is therefore not
     * suitable for concurrent use from multiple threads.</p>
     *
     * @param batch the batch used to draw the text
     * @param text the text to lay out and render
     * @param fontSize the desired font size
     * @param x the x-coordinate of the text origin
     * @param y the y-coordinate of the first line baseline
     */
    public void drawText(Batch batch, String text, float fontSize, float x, float y) {
        layout.setText(text);
        layout.fontSize(fontSize);
        drawText(batch, layout, x, y);
    }

    /**
     * Releases the native font resource owned by this font.
     *
     * <p>Any glyphs cached in the associated {@link GlyphAtlas} remain valid as long as
     * the underlying typeface is alive. The font cannot be used after disposal.</p>
     */
    @Override
    public void dispose() {
        Matn.matn_font_destroy(mtFont);
    }
}
