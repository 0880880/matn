package com.github.zeroeighteightzero.matn._native.enums;

import com.badlogic.gdx.jnigen.runtime.pointer.EnumPointer;
import com.badlogic.gdx.jnigen.runtime.c.CEnum;
import com.badlogic.gdx.jnigen.runtime.CHandler;
import com.badlogic.gdx.jnigen.runtime.pointer.VoidPointer;

public enum MatnGPU_LANGUAGE implements CEnum {

    MATN_GPU_LANGUAGE_GLSL(0), MATN_GPU_LANGUAGE_WGSL(1), MATN_GPU_LANGUAGE_HLSL(2), MATN_GPU_LANGUAGE_MSL(3);

    private static final int __size = 4;

    private final int index;

    MatnGPU_LANGUAGE(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    public int getSize() {
        return __size;
    }

    public static MatnGPU_LANGUAGE getByIndex(int index) {
        switch(index) {
            case 0:
                return MATN_GPU_LANGUAGE_GLSL;
            case 1:
                return MATN_GPU_LANGUAGE_WGSL;
            case 2:
                return MATN_GPU_LANGUAGE_HLSL;
            case 3:
                return MATN_GPU_LANGUAGE_MSL;
            default:
                throw new IllegalArgumentException("Index " + index + " does not exist.");
        }
    }

    public static final class MatnGPU_LANGUAGEPointer extends EnumPointer<MatnGPU_LANGUAGE> {

        public MatnGPU_LANGUAGEPointer(VoidPointer pointer) {
            super(pointer);
        }

        public MatnGPU_LANGUAGEPointer(long pointer, boolean freeOnGC) {
            super(pointer, freeOnGC);
        }

        public MatnGPU_LANGUAGEPointer(long pointer, boolean freeOnGC, int capacity) {
            super(pointer, freeOnGC, capacity * __size);
        }

        public MatnGPU_LANGUAGEPointer() {
            this(1, true);
        }

        public MatnGPU_LANGUAGEPointer(int count, boolean freeOnGC) {
            super(count * __size, freeOnGC);
        }

        public MatnGPU_LANGUAGE getEnumValue(int index) {
            return getByIndex((int) getBufPtr().getInt(index * __size));
        }

        public void setEnumValue(MatnGPU_LANGUAGE value, int index) {
            getBufPtr().setInt(index * __size, value.getIndex());
        }
    }
}
