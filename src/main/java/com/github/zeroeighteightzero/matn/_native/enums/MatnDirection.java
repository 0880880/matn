package com.github.zeroeighteightzero.matn._native.enums;

import com.badlogic.gdx.jnigen.runtime.pointer.EnumPointer;
import com.badlogic.gdx.jnigen.runtime.c.CEnum;
import com.badlogic.gdx.jnigen.runtime.CHandler;
import com.badlogic.gdx.jnigen.runtime.pointer.VoidPointer;

public enum MatnDirection implements CEnum {

    MATN_DIRECTION_INVALID(0), MATN_DIRECTION_LTR(4), MATN_DIRECTION_RTL(5), MATN_DIRECTION_TTB(6), MATN_DIRECTION_BTT(7);

    private static final int __size = 4;

    private final int index;

    MatnDirection(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    public int getSize() {
        return __size;
    }

    public static MatnDirection getByIndex(int index) {
        switch(index) {
            case 0:
                return MATN_DIRECTION_INVALID;
            case 4:
                return MATN_DIRECTION_LTR;
            case 5:
                return MATN_DIRECTION_RTL;
            case 6:
                return MATN_DIRECTION_TTB;
            case 7:
                return MATN_DIRECTION_BTT;
            default:
                throw new IllegalArgumentException("Index " + index + " does not exist.");
        }
    }

    public static final class MatnDirectionPointer extends EnumPointer<MatnDirection> {

        public MatnDirectionPointer(VoidPointer pointer) {
            super(pointer);
        }

        public MatnDirectionPointer(long pointer, boolean freeOnGC) {
            super(pointer, freeOnGC);
        }

        public MatnDirectionPointer(long pointer, boolean freeOnGC, int capacity) {
            super(pointer, freeOnGC, capacity * __size);
        }

        public MatnDirectionPointer() {
            this(1, true);
        }

        public MatnDirectionPointer(int count, boolean freeOnGC) {
            super(count * __size, freeOnGC);
        }

        public MatnDirection getEnumValue(int index) {
            return getByIndex((int) getBufPtr().getInt(index * __size));
        }

        public void setEnumValue(MatnDirection value, int index) {
            getBufPtr().setInt(index * __size, value.getIndex());
        }
    }
}
