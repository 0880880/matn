package com.github.zeroeighteightzero.matn._native.structs;

import com.badlogic.gdx.jnigen.runtime.CHandler;
import com.badlogic.gdx.jnigen.runtime.pointer.Struct;
import com.badlogic.gdx.jnigen.runtime.pointer.StackElementPointer;
import com.badlogic.gdx.jnigen.runtime.pointer.Pointing;
import com.badlogic.gdx.jnigen.runtime.pointer.VoidPointer;
import com.github.zeroeighteightzero.matn._native.FFITypes;
import com.badlogic.gdx.jnigen.runtime.pointer.integer.BytePointer;
import com.badlogic.gdx.jnigen.runtime.pointer.FloatPointer;

public final class MatnVarInstance extends Struct {

    private final static int __size;

    private final static long __ffi_type;

    static {
        __ffi_type = FFITypes.getCTypeInfo(16).getFfiType();
        __size = CHandler.getSizeFromFFIType(__ffi_type);
    }

    public MatnVarInstance(long pointer, boolean freeOnGC) {
        super(pointer, freeOnGC);
    }

    public MatnVarInstance(long pointer, boolean freeOnGC, Pointing parent) {
        super(pointer, freeOnGC);
        setParent(parent);
    }

    public MatnVarInstance() {
        super(__size);
    }

    public long getSize() {
        return __size;
    }

    public long getFFIType() {
        return __ffi_type;
    }

    public MatnVarInstance.MatnVarInstancePointer asPointer() {
        return new MatnVarInstance.MatnVarInstancePointer(getPointer(), false, 1, this);
    }

    public void asPointer(MatnVarInstance.MatnVarInstancePointer ptr) {
        ptr.setPointer(this);
    }

    public BytePointer name() {
        return new BytePointer(getBufPtr().getNativePointer(0), false);
    }

    public void name(BytePointer name) {
        getBufPtr().setNativePointer(0, name.getPointer());
    }

    public void getName(BytePointer toSetPtr) {
        toSetPtr.setPointer(getBufPtr().getNativePointer(0));
    }

    public FloatPointer coords() {
        return new FloatPointer(getBufPtr().getNativePointer(CHandler.IS_64_BIT ? 8 : 4), false);
    }

    public void coords(FloatPointer coords) {
        getBufPtr().setNativePointer(CHandler.IS_64_BIT ? 8 : 4, coords.getPointer());
    }

    public void getCoords(FloatPointer toSetPtr) {
        toSetPtr.setPointer(getBufPtr().getNativePointer(CHandler.IS_64_BIT ? 8 : 4));
    }

    public int coord_count() {
        return getBufPtr().getInt(CHandler.IS_64_BIT ? 16 : 8);
    }

    public void coord_count(int coord_count) {
        getBufPtr().setInt(CHandler.IS_64_BIT ? 16 : 8, coord_count);
    }

    public static final class MatnVarInstancePointer extends StackElementPointer<MatnVarInstance> {

        public MatnVarInstancePointer(VoidPointer pointer) {
            super(pointer);
        }

        public MatnVarInstancePointer(long pointer, boolean freeOnGC) {
            super(pointer, freeOnGC);
        }

        public MatnVarInstancePointer(long pointer, boolean freeOnGC, int capacity) {
            super(pointer, freeOnGC, capacity * __size);
        }

        public MatnVarInstancePointer(long pointer, boolean freeOnGC, Pointing parent) {
            super(pointer, freeOnGC);
            setParent(parent);
        }

        public MatnVarInstancePointer(long pointer, boolean freeOnGC, int capacity, Pointing parent) {
            super(pointer, freeOnGC, capacity * __size);
            setParent(parent);
        }

        public MatnVarInstancePointer() {
            this(1, true);
        }

        public MatnVarInstancePointer(int count, boolean freeOnGC) {
            super(__size, count, freeOnGC);
        }

        public int getSize() {
            return __size;
        }

        protected MatnVarInstance createStackElement(long ptr, boolean freeOnGC) {
            return new MatnVarInstance(ptr, freeOnGC);
        }
    }
}
