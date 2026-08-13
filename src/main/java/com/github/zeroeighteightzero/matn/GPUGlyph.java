package com.github.zeroeighteightzero.matn;

/**
 * A glyph encoded for GPU rendering and stored in a {@link GlyphAtlas} GPU page.
 *
 * <p>Holds the glyph's location and texel count within a GPU page, its upem-scaled
 * bounding box, and a reference to the owning atlas. Instances are created and cached by
 * a {@link GlyphAtlas} and should not be constructed directly by callers.</p>
 */
public class GPUGlyph {

    /** The atlas that owns this GPU glyph. */
    public final GlyphAtlas atlas;
    /** The units-per-em of the typeface the glyph belongs to. */
    public final float upem;
    /** The starting texel offset of this glyph within its GPU page. */
    public final int location;
    /** The minimum x of the glyph bounding box. */
    public final int minX;
    /** The minimum y of the glyph bounding box. */
    public final int minY;
    /** The maximum x of the glyph bounding box. */
    public final int maxX;
    /** The maximum y of the glyph bounding box. */
    public final int maxY;
    protected final int length;
    protected final int page;

    /**
     * Creates a GPU glyph with the given placement and bounds.
     */
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
