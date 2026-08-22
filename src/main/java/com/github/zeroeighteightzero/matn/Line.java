package com.github.zeroeighteightzero.matn;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.LongArray;
import com.badlogic.gdx.utils.NumberUtils;
import com.badlogic.gdx.utils.ShortArray;

/**
 * A single laid-out line of glyphs.
 *
 * <p>Collects the glyph identifiers and their source clusters produced by shaping and
 * wrapping, together with the accumulated width of the line.</p>
 */
public class Line {

    /** The font-specific glyph identifiers in this line, in drawing order. */
    public final LongArray glyphs;
    public final ShortArray flags;
    /** The source UTF-16 cluster index for each glyph in {@link #glyphs}. */
    public final LongArray clusters;
    /** The horizontal width of this line. */
    public float width;

    /**
     * Creates an empty line with the given initial capacity.
     *
     * @param length the initial capacity in glyphs
     */
    public Line(int length) {
        glyphs = new LongArray(length);
        flags = new ShortArray(length);
        clusters = new LongArray(length);
    }

    public void add(int glyph, float color, short flags) {
        glyphs.add(glyph | (((long) NumberUtils.floatToIntColor(color)) << 32));
        ///glyphs.add(((long) glyph << 32) | NumberUtils.floatToIntColor(color));
        this.flags.add(flags);
    }

    public void add(int glyph, Color color, short flags) {
        glyphs.add(glyph | (((long) color.toIntBits()) << 32));
        this.flags.add(flags);
    }

    public int getGlyph(int index) {
        return (int) glyphs.get(index);
    }

    public float getColor(int index) {
        return NumberUtils.intToFloatColor((int) (glyphs.get(index) >> 32));
    }

    public short getFlags(int index) {
        return flags.get(index);
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
