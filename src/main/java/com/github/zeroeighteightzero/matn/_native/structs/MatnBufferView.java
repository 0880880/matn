package com.github.zeroeighteightzero.matn._native.structs;

import com.badlogic.gdx.jnigen.runtime.CHandler;
import com.badlogic.gdx.jnigen.runtime.pointer.Struct;
import com.badlogic.gdx.jnigen.runtime.pointer.StackElementPointer;
import com.badlogic.gdx.jnigen.runtime.pointer.Pointing;
import com.badlogic.gdx.jnigen.runtime.pointer.VoidPointer;
import com.github.zeroeighteightzero.matn._native.FFITypes;
import com.badlogic.gdx.jnigen.runtime.pointer.integer.UIntPointer;
import com.badlogic.gdx.jnigen.runtime.pointer.FloatPointer;

public final class MatnBufferView extends Struct {

    private final static int __size;

    private final static long __ffi_type;

    static {
        __ffi_type = FFITypes.getCTypeInfo(10).getFfiType();
        __size = CHandler.getSizeFromFFIType(__ffi_type);
    }

    public MatnBufferView(long pointer, boolean freeOnGC) {
        super(pointer, freeOnGC);
    }

    public MatnBufferView(long pointer, boolean freeOnGC, Pointing parent) {
        super(pointer, freeOnGC);
        setParent(parent);
    }

    public MatnBufferView() {
        super(__size);
    }

    public long getSize() {
        return __size;
    }

    public long getFFIType() {
        return __ffi_type;
    }

    public MatnBufferView.MatnBufferViewPointer asPointer() {
        return new MatnBufferView.MatnBufferViewPointer(getPointer(), false, 1, this);
    }

    public void asPointer(MatnBufferView.MatnBufferViewPointer ptr) {
        ptr.setPointer(this);
    }

    public UIntPointer glyph_ids() {
        return new UIntPointer(getBufPtr().getNativePointer(0), false);
    }

    public void glyph_ids(UIntPointer glyph_ids) {
        getBufPtr().setNativePointer(0, glyph_ids.getPointer());
    }

    public void getGlyph_ids(UIntPointer toSetPtr) {
        toSetPtr.setPointer(getBufPtr().getNativePointer(0));
    }

    public FloatPointer x_advances() {
        return new FloatPointer(getBufPtr().getNativePointer(CHandler.IS_64_BIT ? 8 : 4), false);
    }

    public void x_advances(FloatPointer x_advances) {
        getBufPtr().setNativePointer(CHandler.IS_64_BIT ? 8 : 4, x_advances.getPointer());
    }

    public void getX_advances(FloatPointer toSetPtr) {
        toSetPtr.setPointer(getBufPtr().getNativePointer(CHandler.IS_64_BIT ? 8 : 4));
    }

    public FloatPointer y_advances() {
        return new FloatPointer(getBufPtr().getNativePointer(CHandler.IS_64_BIT ? 16 : 8), false);
    }

    public void y_advances(FloatPointer y_advances) {
        getBufPtr().setNativePointer(CHandler.IS_64_BIT ? 16 : 8, y_advances.getPointer());
    }

    public void getY_advances(FloatPointer toSetPtr) {
        toSetPtr.setPointer(getBufPtr().getNativePointer(CHandler.IS_64_BIT ? 16 : 8));
    }

    public FloatPointer x_offsets() {
        return new FloatPointer(getBufPtr().getNativePointer(CHandler.IS_64_BIT ? 24 : 12), false);
    }

    public void x_offsets(FloatPointer x_offsets) {
        getBufPtr().setNativePointer(CHandler.IS_64_BIT ? 24 : 12, x_offsets.getPointer());
    }

    public void getX_offsets(FloatPointer toSetPtr) {
        toSetPtr.setPointer(getBufPtr().getNativePointer(CHandler.IS_64_BIT ? 24 : 12));
    }

    public FloatPointer y_offsets() {
        return new FloatPointer(getBufPtr().getNativePointer(CHandler.IS_64_BIT ? 32 : 16), false);
    }

    public void y_offsets(FloatPointer y_offsets) {
        getBufPtr().setNativePointer(CHandler.IS_64_BIT ? 32 : 16, y_offsets.getPointer());
    }

    public void getY_offsets(FloatPointer toSetPtr) {
        toSetPtr.setPointer(getBufPtr().getNativePointer(CHandler.IS_64_BIT ? 32 : 16));
    }

    public UIntPointer clusters() {
        return new UIntPointer(getBufPtr().getNativePointer(CHandler.IS_64_BIT ? 40 : 20), false);
    }

    public void clusters(UIntPointer clusters) {
        getBufPtr().setNativePointer(CHandler.IS_64_BIT ? 40 : 20, clusters.getPointer());
    }

    public void getClusters(UIntPointer toSetPtr) {
        toSetPtr.setPointer(getBufPtr().getNativePointer(CHandler.IS_64_BIT ? 40 : 20));
    }

    public long length() {
        return getBufPtr().getUInt(CHandler.IS_64_BIT ? 48 : 24);
    }

    public void length(long length) {
        getBufPtr().setUInt(CHandler.IS_64_BIT ? 48 : 24, length);
    }

    public static final class MatnBufferViewPointer extends StackElementPointer<MatnBufferView> {

        public MatnBufferViewPointer(VoidPointer pointer) {
            super(pointer);
        }

        public MatnBufferViewPointer(long pointer, boolean freeOnGC) {
            super(pointer, freeOnGC);
        }

        public MatnBufferViewPointer(long pointer, boolean freeOnGC, int capacity) {
            super(pointer, freeOnGC, capacity * __size);
        }

        public MatnBufferViewPointer(long pointer, boolean freeOnGC, Pointing parent) {
            super(pointer, freeOnGC);
            setParent(parent);
        }

        public MatnBufferViewPointer(long pointer, boolean freeOnGC, int capacity, Pointing parent) {
            super(pointer, freeOnGC, capacity * __size);
            setParent(parent);
        }

        public MatnBufferViewPointer() {
            this(1, true);
        }

        public MatnBufferViewPointer(int count, boolean freeOnGC) {
            super(__size, count, freeOnGC);
        }

        public int getSize() {
            return __size;
        }

        protected MatnBufferView createStackElement(long ptr, boolean freeOnGC) {
            return new MatnBufferView(ptr, freeOnGC);
        }
    }
}
