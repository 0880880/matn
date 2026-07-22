package com.github.zeroeighteightzero.matn;

public class Glyph {

    public final long glyphID;
    public final int size;
    public final int page;
    public final int x, y, width, height;
    public final float u, v, u2, v2;
    public final float bearingX, bearingY;

    Glyph(GlyphAtlas atlas, long glyphID, int size, int page, int x, int y, int width, int height, float bearingX, float bearingY) {
        this.glyphID = glyphID;
        this.size = size;
        this.page = page;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        float scale = 1 / ((float) atlas.pageSize);
        this.u = x * scale;
        this.v = y * scale;
        this.u2 = this.u + width * scale;
        this.v2 = this.v + height * scale;
        this.bearingX = bearingX;
        this.bearingY = bearingY;
    }
}
