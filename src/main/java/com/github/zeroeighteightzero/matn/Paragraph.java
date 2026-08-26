package com.github.zeroeighteightzero.matn;

import com.badlogic.gdx.jnigen.runtime.pointer.integer.UShortPointer;
import com.badlogic.gdx.utils.Disposable;

/**
 * A UTF-16 text paragraph shaped as a single unit.
 *
 * <p>Constructing a paragraph copies the text into a native-side buffer, making it
 * suitable for passing to the shaping functions in {@link Font}.</p>
 */
public class Paragraph implements Disposable {
    protected final UShortPointer ptr;
    public final int length;

    /**
     * Creates a paragraph from the given text.
     *
     * @param text the paragraph text; {@code null} or empty yields an empty paragraph
     */
    public Paragraph(String text) {
        if (text == null || text.isEmpty()) {
            ptr = null;
            length = 0;
            return;
        }
        ptr = new UShortPointer(text.length(), false);
        length = text.length();
        for (int i = 0; i < length; ++i) {
            ptr.setUShort(text.charAt(i), i);
        }
    }

    @Override
    public void dispose() {
        ptr.free();
    }
}
