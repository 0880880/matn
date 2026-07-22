package com.github.zeroeighteightzero.matn._native.structs;

import com.badlogic.gdx.jnigen.runtime.pointer.VoidPointer;
import com.badlogic.gdx.jnigen.runtime.pointer.Pointing;

public final class MatnBlob {

    private MatnBlob() {
    }

    public static final class MatnBlobPointer extends VoidPointer {

        public MatnBlobPointer(VoidPointer pointer) {
            super(pointer);
        }

        public MatnBlobPointer(long pointer, boolean freeOnGC) {
            super(pointer, freeOnGC);
        }

        public MatnBlobPointer(long pointer, boolean freeOnGC, Pointing parent) {
            super(pointer, freeOnGC);
            setParent(parent);
        }
    }
}
