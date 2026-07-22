package com.github.zeroeighteightzero.matn._native.structs;

import com.badlogic.gdx.jnigen.runtime.pointer.VoidPointer;
import com.badlogic.gdx.jnigen.runtime.pointer.Pointing;

public final class MatnFont {

    private MatnFont() {
    }

    public static final class MatnFontPointer extends VoidPointer {

        public MatnFontPointer(VoidPointer pointer) {
            super(pointer);
        }

        public MatnFontPointer(long pointer, boolean freeOnGC) {
            super(pointer, freeOnGC);
        }

        public MatnFontPointer(long pointer, boolean freeOnGC, Pointing parent) {
            super(pointer, freeOnGC);
            setParent(parent);
        }
    }
}
