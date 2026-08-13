package com.github.zeroeighteightzero.matn._native.structs;

import com.badlogic.gdx.jnigen.runtime.pointer.VoidPointer;
import com.badlogic.gdx.jnigen.runtime.pointer.Pointing;

public final class MatnTextRuns {

    private MatnTextRuns() {
    }

    public static final class MatnTextRunsPointer extends VoidPointer {

        public MatnTextRunsPointer(VoidPointer pointer) {
            super(pointer);
        }

        public MatnTextRunsPointer(long pointer, boolean freeOnGC) {
            super(pointer, freeOnGC);
        }

        public MatnTextRunsPointer(long pointer, boolean freeOnGC, Pointing parent) {
            super(pointer, freeOnGC);
            setParent(parent);
        }
    }
}
