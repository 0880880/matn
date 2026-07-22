package com.github.zeroeighteightzero.matn._native.structs;

import com.badlogic.gdx.jnigen.runtime.CHandler;
import com.badlogic.gdx.jnigen.runtime.pointer.Struct;
import com.badlogic.gdx.jnigen.runtime.pointer.StackElementPointer;
import com.badlogic.gdx.jnigen.runtime.pointer.Pointing;
import com.badlogic.gdx.jnigen.runtime.pointer.VoidPointer;
import com.github.zeroeighteightzero.matn._native.FFITypes;
import com.badlogic.gdx.jnigen.runtime.pointer.integer.BytePointer;

public final class MatnGPU_Blob extends Struct {

    private final static int __size;

    private final static long __ffi_type;

    static {
        __ffi_type = FFITypes.getCTypeInfo(12).getFfiType();
        __size = CHandler.getSizeFromFFIType(__ffi_type);
    }

    public MatnGPU_Blob(long pointer, boolean freeOnGC) {
        super(pointer, freeOnGC);
    }

    public MatnGPU_Blob(long pointer, boolean freeOnGC, Pointing parent) {
        super(pointer, freeOnGC);
        setParent(parent);
    }

    public MatnGPU_Blob() {
        super(__size);
    }

    public long getSize() {
        return __size;
    }

    public long getFFIType() {
        return __ffi_type;
    }

    public MatnGPU_Blob.MatnGPU_BlobPointer asPointer() {
        return new MatnGPU_Blob.MatnGPU_BlobPointer(getPointer(), false, 1, this);
    }

    public void asPointer(MatnGPU_Blob.MatnGPU_BlobPointer ptr) {
        ptr.setPointer(this);
    }

    public BytePointer data() {
        return new BytePointer(getBufPtr().getNativePointer(0), false);
    }

    public void data(BytePointer data) {
        getBufPtr().setNativePointer(0, data.getPointer());
    }

    public void getData(BytePointer toSetPtr) {
        toSetPtr.setPointer(getBufPtr().getNativePointer(0));
    }

    public long length() {
        return getBufPtr().getUInt(CHandler.IS_64_BIT ? 8 : 4);
    }

    public void length(long length) {
        getBufPtr().setUInt(CHandler.IS_64_BIT ? 8 : 4, length);
    }

    public int min_x() {
        return getBufPtr().getInt(CHandler.IS_64_BIT ? 12 : 8);
    }

    public void min_x(int min_x) {
        getBufPtr().setInt(CHandler.IS_64_BIT ? 12 : 8, min_x);
    }

    public int min_y() {
        return getBufPtr().getInt(CHandler.IS_64_BIT ? 16 : 12);
    }

    public void min_y(int min_y) {
        getBufPtr().setInt(CHandler.IS_64_BIT ? 16 : 12, min_y);
    }

    public int max_x() {
        return getBufPtr().getInt(CHandler.IS_64_BIT ? 20 : 16);
    }

    public void max_x(int max_x) {
        getBufPtr().setInt(CHandler.IS_64_BIT ? 20 : 16, max_x);
    }

    public int max_y() {
        return getBufPtr().getInt(CHandler.IS_64_BIT ? 24 : 20);
    }

    public void max_y(int max_y) {
        getBufPtr().setInt(CHandler.IS_64_BIT ? 24 : 20, max_y);
    }

    public static final class MatnGPU_BlobPointer extends StackElementPointer<MatnGPU_Blob> {

        public MatnGPU_BlobPointer(VoidPointer pointer) {
            super(pointer);
        }

        public MatnGPU_BlobPointer(long pointer, boolean freeOnGC) {
            super(pointer, freeOnGC);
        }

        public MatnGPU_BlobPointer(long pointer, boolean freeOnGC, int capacity) {
            super(pointer, freeOnGC, capacity * __size);
        }

        public MatnGPU_BlobPointer(long pointer, boolean freeOnGC, Pointing parent) {
            super(pointer, freeOnGC);
            setParent(parent);
        }

        public MatnGPU_BlobPointer(long pointer, boolean freeOnGC, int capacity, Pointing parent) {
            super(pointer, freeOnGC, capacity * __size);
            setParent(parent);
        }

        public MatnGPU_BlobPointer() {
            this(1, true);
        }

        public MatnGPU_BlobPointer(int count, boolean freeOnGC) {
            super(__size, count, freeOnGC);
        }

        public int getSize() {
            return __size;
        }

        protected MatnGPU_Blob createStackElement(long ptr, boolean freeOnGC) {
            return new MatnGPU_Blob(ptr, freeOnGC);
        }
    }
}
