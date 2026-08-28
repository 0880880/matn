package com.github.zeroeighteightzero.matn;

public interface GPUGlyphBatch {

    void begin();

    void end();

    void flush();

    void setPackedColor(float color);

    void drawGlyph(GPUGlyph glyph, float size, int minX, int minY, int maxX, int maxY, float x, float y);

    public default void drawGlyph(GPUGlyph glyph, float size, float x, float y) {
        drawGlyph(glyph, size, glyph.minX, glyph.minY, glyph.maxX, glyph.maxY, x, y);
    }

}
