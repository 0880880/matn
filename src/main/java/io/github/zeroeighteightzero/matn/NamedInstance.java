package io.github.zeroeighteightzero.matn;

/**
 * A named variation instance of a variable font.
 *
 * <p>Associates a user-visible name with a fixed set of variation-axis coordinates.
 * Instances are created by {@link Typeface} when a font is loaded and applied to a
 * {@link Font} via {@link Font#setNamedInstance(NamedInstance)}.</p>
 */
public class NamedInstance {

    /** The user-visible name of this variation instance. */
    public final String name;
    /** The variation-axis coordinates of this instance. */
    public final float[] coords;

    /**
     * Creates a named variation instance.
     *
     * @param name the user-visible name
     * @param coords the variation-axis coordinates
     */
    public NamedInstance(String name, float[] coords) {
        this.name = name;
        this.coords = coords;
    }

}
