package com.github.zeroeighteightzero.matn;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.*;

/**
 * A single laid-out line of glyphs.
 *
 * <p>Collects the glyph identifiers and their source clusters produced by shaping and
 * wrapping, together with the accumulated width of the line.</p>
 */
public class Run {

    public float x, y;
    public Font font;
    /** The font-specific glyph identifiers in this line, in drawing order. */
    public final Array<Glyph> glyphs;
    public final float color;
    public final short flags;
    public final float scaleX;
    public final float scaleY;
    /** The source UTF-16 cluster index for each glyph in {@link #glyphs}. */
    public final LongArray clusters;
    /** The horizontal width of this line. */
    public float width;

    /**
     * Creates an empty line with the given initial capacity.
     *
     * @param length the initial capacity in glyphs
     */
    public Run(int length, short flags, float color, float scaleX, float scaleY) {
        glyphs = new Array<>(length);
        clusters = new LongArray(length);
        this.flags = flags;
        this.color = color;
        this.scaleX = scaleX;
        this.scaleY = scaleY;
    }

    public void add(Glyph glyph) {
        glyphs.add(glyph);
    }

    public Glyph getGlyph(int index) {
        return glyphs.get(index);
    }

    /**
     * Returns whether this line contains at least one glyph.
     *
     * @return {@code true} if the line is not empty
     */
    public boolean notEmpty() {
        return glyphs.notEmpty();
    }

}
