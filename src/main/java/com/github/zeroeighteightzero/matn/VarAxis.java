package com.github.zeroeighteightzero.matn;

/**
 * A single variable-font (OpenType variation) axis.
 *
 * <p>Describes the tag, minimum, default, and maximum values of one axis of a variable
 * font. Instances are created by {@link Typeface} when a font is loaded and should not be
 * constructed by callers.</p>
 */
public class VarAxis {

    /** The four-character OpenType variation-axis tag. */
    public final String tag;
    /** The minimum, default, and maximum axis values. */
    public final float min, def, max;

    VarAxis(String tag, float min, float def, float max) {
        this.tag = tag;
        this.min = min;
        this.def = def;
        this.max = max;
    }

}
