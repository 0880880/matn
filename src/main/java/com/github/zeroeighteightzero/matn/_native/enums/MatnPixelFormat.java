package com.github.zeroeighteightzero.matn._native.enums;

import com.badlogic.gdx.jnigen.runtime.pointer.EnumPointer;
import com.badlogic.gdx.jnigen.runtime.c.CEnum;
import com.badlogic.gdx.jnigen.runtime.CHandler;
import com.badlogic.gdx.jnigen.runtime.pointer.VoidPointer;

public enum MatnPixelFormat implements CEnum {

    MATN_PIXEL_FORMAT_A8(0), MATN_PIXEL_FORMAT_BGRA32(1);

    private static final int __size = 4;

    private final int index;

    MatnPixelFormat(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    public int getSize() {
        return __size;
    }

    public static MatnPixelFormat getByIndex(int index) {
        switch(index) {
            case 0:
                return MATN_PIXEL_FORMAT_A8;
            case 1:
                return MATN_PIXEL_FORMAT_BGRA32;
            default:
                throw new IllegalArgumentException("Index " + index + " does not exist.");
        }
    }

    public static final class MatnPixelFormatPointer extends EnumPointer<MatnPixelFormat> {

        public MatnPixelFormatPointer(VoidPointer pointer) {
            super(pointer);
        }

        public MatnPixelFormatPointer(long pointer, boolean freeOnGC) {
            super(pointer, freeOnGC);
        }

        public MatnPixelFormatPointer(long pointer, boolean freeOnGC, int capacity) {
            super(pointer, freeOnGC, capacity * __size);
        }

        public MatnPixelFormatPointer() {
            this(1, true);
        }

        public MatnPixelFormatPointer(int count, boolean freeOnGC) {
            super(count * __size, freeOnGC);
        }

        public MatnPixelFormat getEnumValue(int index) {
            return getByIndex((int) getBufPtr().getInt(index * __size));
        }

        public void setEnumValue(MatnPixelFormat value, int index) {
            getBufPtr().setInt(index * __size, value.getIndex());
        }
    }
}
