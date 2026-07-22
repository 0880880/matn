package com.github.zeroeighteightzero.matn._native.structs;

import com.badlogic.gdx.jnigen.runtime.CHandler;
import com.badlogic.gdx.jnigen.runtime.pointer.Struct;
import com.badlogic.gdx.jnigen.runtime.pointer.StackElementPointer;
import com.badlogic.gdx.jnigen.runtime.pointer.Pointing;
import com.badlogic.gdx.jnigen.runtime.pointer.VoidPointer;
import com.github.zeroeighteightzero.matn._native.FFITypes;
import com.badlogic.gdx.jnigen.runtime.pointer.integer.BytePointer;

public final class MatnVarAxis extends Struct {

    private final static int __size;

    private final static long __ffi_type;

    static {
        __ffi_type = FFITypes.getCTypeInfo(15).getFfiType();
        __size = CHandler.getSizeFromFFIType(__ffi_type);
    }

    public MatnVarAxis(long pointer, boolean freeOnGC) {
        super(pointer, freeOnGC);
    }

    public MatnVarAxis(long pointer, boolean freeOnGC, Pointing parent) {
        super(pointer, freeOnGC);
        setParent(parent);
    }

    public MatnVarAxis() {
        super(__size);
    }

    public long getSize() {
        return __size;
    }

    public long getFFIType() {
        return __ffi_type;
    }

    public MatnVarAxis.MatnVarAxisPointer asPointer() {
        return new MatnVarAxis.MatnVarAxisPointer(getPointer(), false, 1, this);
    }

    public void asPointer(MatnVarAxis.MatnVarAxisPointer ptr) {
        ptr.setPointer(this);
    }

    public BytePointer tag() {
        return new BytePointer(getPointer(), false, 4);
    }

    public void tag(BytePointer toSetPtr) {
        toSetPtr.setPointer(getPointer(), 4, this);
    }

    public BytePointer getTag() {
        return new BytePointer(getBufPtr().duplicate(0, 4), false, 4);
    }

    public void getTag(BytePointer toCopyTo) {
        toCopyTo.getBufPtr().copyFrom(0, getBufPtr(), 0, 4);
    }

    public void setTag(BytePointer toCopyFrom) {
        getBufPtr().copyFrom(0, toCopyFrom.getBufPtr(), 0, 4);
    }

    public float min_value() {
        return getBufPtr().getFloat(4);
    }

    public void min_value(float min_value) {
        getBufPtr().setFloat(4, min_value);
    }

    public float default_value() {
        return getBufPtr().getFloat(8);
    }

    public void default_value(float default_value) {
        getBufPtr().setFloat(8, default_value);
    }

    public float max_value() {
        return getBufPtr().getFloat(12);
    }

    public void max_value(float max_value) {
        getBufPtr().setFloat(12, max_value);
    }

    public static final class MatnVarAxisPointer extends StackElementPointer<MatnVarAxis> {

        public MatnVarAxisPointer(VoidPointer pointer) {
            super(pointer);
        }

        public MatnVarAxisPointer(long pointer, boolean freeOnGC) {
            super(pointer, freeOnGC);
        }

        public MatnVarAxisPointer(long pointer, boolean freeOnGC, int capacity) {
            super(pointer, freeOnGC, capacity * __size);
        }

        public MatnVarAxisPointer(long pointer, boolean freeOnGC, Pointing parent) {
            super(pointer, freeOnGC);
            setParent(parent);
        }

        public MatnVarAxisPointer(long pointer, boolean freeOnGC, int capacity, Pointing parent) {
            super(pointer, freeOnGC, capacity * __size);
            setParent(parent);
        }

        public MatnVarAxisPointer() {
            this(1, true);
        }

        public MatnVarAxisPointer(int count, boolean freeOnGC) {
            super(__size, count, freeOnGC);
        }

        public int getSize() {
            return __size;
        }

        protected MatnVarAxis createStackElement(long ptr, boolean freeOnGC) {
            return new MatnVarAxis(ptr, freeOnGC);
        }
    }
}
