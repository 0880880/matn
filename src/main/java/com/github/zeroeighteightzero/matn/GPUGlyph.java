package com.github.zeroeighteightzero.matn;

public class GPUGlyph {

    public final GlyphAtlas atlas;
    public final float upem;
    public final int location;
    public final int minX, minY;
    public final int maxX, maxY;
    protected final int length;
    protected final int page;

    public GPUGlyph(GlyphAtlas atlas, float upem, int location, int minX, int minY, int maxX, int maxY, int length, int page) {
        this.atlas = atlas;
        this.upem = upem;
        this.location = location;
        this.minX = minX;
        this.minY = minY;
        this.maxX = maxX;
        this.maxY = maxY;
        this.length = length;
        this.page = page;
    }

}
