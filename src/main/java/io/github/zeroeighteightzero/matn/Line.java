package io.github.zeroeighteightzero.matn;

import com.badlogic.gdx.utils.LongArray;

/**
 * A single laid-out line of glyphs.
 *
 * <p>Collects the glyph identifiers and their source clusters produced by shaping and
 * wrapping, together with the accumulated width of the line.</p>
 */
public class Line {

    /** The font-specific glyph identifiers in this line, in drawing order. */
    public final LongArray glyphs;
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
        clusters = new LongArray(length);
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
