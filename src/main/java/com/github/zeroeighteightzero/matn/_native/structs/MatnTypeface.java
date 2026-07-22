package com.github.zeroeighteightzero.matn._native.structs;

import com.badlogic.gdx.jnigen.runtime.pointer.VoidPointer;
import com.badlogic.gdx.jnigen.runtime.pointer.Pointing;

public final class MatnTypeface {

    private MatnTypeface() {
    }

    public static final class MatnTypefacePointer extends VoidPointer {

        public MatnTypefacePointer(VoidPointer pointer) {
            super(pointer);
        }

        public MatnTypefacePointer(long pointer, boolean freeOnGC) {
            super(pointer, freeOnGC);
        }

        public MatnTypefacePointer(long pointer, boolean freeOnGC, Pointing parent) {
            super(pointer, freeOnGC);
            setParent(parent);
        }
    }
}
