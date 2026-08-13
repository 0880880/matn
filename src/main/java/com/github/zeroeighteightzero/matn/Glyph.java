package com.github.zeroeighteightzero.matn;

/**
 * A rasterized glyph cached inside a {@link GlyphAtlas} page.
 *
 * <p>Describes where in an atlas page the glyph's pixels live, plus its texture
 * coordinates, pixel dimensions, and bearing. Instances are created and cached by a
 * {@link GlyphAtlas} and should not be constructed directly by callers.</p>
 */
public class Glyph {

    public final long glyphID;
    public final int size;
    public final int page;
    public final int x, y, width, height;
    public final int top, left;

    Glyph(GlyphAtlas atlas, long glyphID, int size, int page, int x, int y, int width, int height, int top, int left) {
        this.glyphID = glyphID;
        this.size = size;
        this.page = page;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.top = top;
        this.left = left;
    }
}
