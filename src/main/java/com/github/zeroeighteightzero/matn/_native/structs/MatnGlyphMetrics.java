package com.github.zeroeighteightzero.matn._native.structs;

import com.badlogic.gdx.jnigen.runtime.CHandler;
import com.badlogic.gdx.jnigen.runtime.pointer.Struct;
import com.badlogic.gdx.jnigen.runtime.pointer.StackElementPointer;
import com.badlogic.gdx.jnigen.runtime.pointer.Pointing;
import com.badlogic.gdx.jnigen.runtime.pointer.VoidPointer;
import com.github.zeroeighteightzero.matn._native.FFITypes;

public final class MatnGlyphMetrics extends Struct {

    private final static int __size;

    private final static long __ffi_type;

    static {
        __ffi_type = FFITypes.getCTypeInfo(13).getFfiType();
        __size = CHandler.getSizeFromFFIType(__ffi_type);
    }

    public MatnGlyphMetrics(long pointer, boolean freeOnGC) {
        super(pointer, freeOnGC);
    }

    public MatnGlyphMetrics(long pointer, boolean freeOnGC, Pointing parent) {
        super(pointer, freeOnGC);
        setParent(parent);
    }

    public MatnGlyphMetrics() {
        super(__size);
    }

    public long getSize() {
        return __size;
    }

    public long getFFIType() {
        return __ffi_type;
    }

    public MatnGlyphMetrics.MatnGlyphMetricsPointer asPointer() {
        return new MatnGlyphMetrics.MatnGlyphMetricsPointer(getPointer(), false, 1, this);
    }

    public void asPointer(MatnGlyphMetrics.MatnGlyphMetricsPointer ptr) {
        ptr.setPointer(this);
    }

    public float bearing_x() {
        return getBufPtr().getFloat(0);
    }

    public void bearing_x(float bearing_x) {
        getBufPtr().setFloat(0, bearing_x);
    }

    public float bearing_y() {
        return getBufPtr().getFloat(4);
    }

    public void bearing_y(float bearing_y) {
        getBufPtr().setFloat(4, bearing_y);
    }

    public float width() {
        return getBufPtr().getFloat(8);
    }

    public void width(float width) {
        getBufPtr().setFloat(8, width);
    }

    public float height() {
        return getBufPtr().getFloat(12);
    }

    public void height(float height) {
        getBufPtr().setFloat(12, height);
    }

    public static final class MatnGlyphMetricsPointer extends StackElementPointer<MatnGlyphMetrics> {

        public MatnGlyphMetricsPointer(VoidPointer pointer) {
            super(pointer);
        }

        public MatnGlyphMetricsPointer(long pointer, boolean freeOnGC) {
            super(pointer, freeOnGC);
        }

        public MatnGlyphMetricsPointer(long pointer, boolean freeOnGC, int capacity) {
            super(pointer, freeOnGC, capacity * __size);
        }

        public MatnGlyphMetricsPointer(long pointer, boolean freeOnGC, Pointing parent) {
            super(pointer, freeOnGC);
            setParent(parent);
        }

        public MatnGlyphMetricsPointer(long pointer, boolean freeOnGC, int capacity, Pointing parent) {
            super(pointer, freeOnGC, capacity * __size);
            setParent(parent);
        }

        public MatnGlyphMetricsPointer() {
            this(1, true);
        }

        public MatnGlyphMetricsPointer(int count, boolean freeOnGC) {
            super(__size, count, freeOnGC);
        }

        public int getSize() {
            return __size;
        }

        protected MatnGlyphMetrics createStackElement(long ptr, boolean freeOnGC) {
            return new MatnGlyphMetrics(ptr, freeOnGC);
        }
    }
}
