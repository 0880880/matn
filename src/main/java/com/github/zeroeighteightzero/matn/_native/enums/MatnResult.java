package com.github.zeroeighteightzero.matn._native.enums;

import com.badlogic.gdx.jnigen.runtime.pointer.EnumPointer;
import com.badlogic.gdx.jnigen.runtime.c.CEnum;
import com.badlogic.gdx.jnigen.runtime.CHandler;
import com.badlogic.gdx.jnigen.runtime.pointer.VoidPointer;

public enum MatnResult implements CEnum {

    MATN_ERR_DATA_NOT_FOUND(-7),
    MATN_ERR_RASTERIZATION_FAILED(-6),
    MATN_ERR_SHAPING_FAILED(-5),
    MATN_ERR_FONT_LOAD_FAILED(-4),
    MATN_ERR_FILE_NOT_FOUND(-3),
    MATN_ERR_OUT_OF_MEMORY(-2),
    MATN_ERR_INVALID_ARGUMENT(-1),
    MATN_SUCCESS(0);

    private static final int __size = 4;

    private final int index;

    MatnResult(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    public int getSize() {
        return __size;
    }

    public static MatnResult getByIndex(int index) {
        switch(index) {
            case 0:
                return MATN_SUCCESS;
            case -1:
                return MATN_ERR_INVALID_ARGUMENT;
            case -2:
                return MATN_ERR_OUT_OF_MEMORY;
            case -3:
                return MATN_ERR_FILE_NOT_FOUND;
            case -4:
                return MATN_ERR_FONT_LOAD_FAILED;
            case -5:
                return MATN_ERR_SHAPING_FAILED;
            case -6:
                return MATN_ERR_RASTERIZATION_FAILED;
            case -7:
                return MATN_ERR_DATA_NOT_FOUND;
            default:
                throw new IllegalArgumentException("Index " + index + " does not exist.");
        }
    }

    public static final class MatnResultPointer extends EnumPointer<MatnResult> {

        public MatnResultPointer(VoidPointer pointer) {
            super(pointer);
        }

        public MatnResultPointer(long pointer, boolean freeOnGC) {
            super(pointer, freeOnGC);
        }

        public MatnResultPointer(long pointer, boolean freeOnGC, int capacity) {
            super(pointer, freeOnGC, capacity * __size);
        }

        public MatnResultPointer() {
            this(1, true);
        }

        public MatnResultPointer(int count, boolean freeOnGC) {
            super(count * __size, freeOnGC);
        }

        public MatnResult getEnumValue(int index) {
            return getByIndex((int) getBufPtr().getInt(index * __size));
        }

        public void setEnumValue(MatnResult value, int index) {
            getBufPtr().setInt(index * __size, value.getIndex());
        }
    }
}
