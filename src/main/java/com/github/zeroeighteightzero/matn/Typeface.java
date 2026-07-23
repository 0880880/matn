package com.github.zeroeighteightzero.matn;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.jnigen.runtime.pointer.PointerPointer;
import com.badlogic.gdx.jnigen.runtime.pointer.integer.BytePointer;
import com.badlogic.gdx.utils.BufferUtils;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.StreamUtils;
import com.github.zeroeighteightzero.matn._native.Matn;
import com.github.zeroeighteightzero.matn._native.structs.MatnTypeface;
import com.github.zeroeighteightzero.matn._native.structs.MatnVarAxis;
import com.github.zeroeighteightzero.matn._native.structs.MatnVarInstance;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class Typeface implements Disposable {

    final MatnTypeface.MatnTypefacePointer mtFace;

    public final boolean isScalable, hasColor, hasVariations;
    public final int upem;

    public final VarAxis[] varAxes;
    public final NamedInstance[] namedInstances;

    public Typeface(MatnTypeface.MatnTypefacePointer mtFace, boolean isScalable, boolean hasColor, boolean hasVariations, VarAxis[] varAxes, NamedInstance[] namedInstances, int upem) {
        this.mtFace = mtFace;
        this.isScalable = isScalable;
        this.hasColor = hasColor;
        this.hasVariations = hasVariations;
        this.varAxes = varAxes;
        this.namedInstances = namedInstances;
        this.upem = upem;
    }

    private static Typeface loadMemory(ByteBuffer data, int index) {
        PointerPointer<MatnTypeface.MatnTypefacePointer> ptr = new PointerPointer<>(MatnTypeface.MatnTypefacePointer::new);
        Matn.matn_typeface_from_memory(new BytePointer(BufferUtils.getUnsafeBufferAddress(data), false), data.remaining(), index, ptr);
        MatnTypeface.MatnTypefacePointer mtFace = ptr.getValue();

        boolean isScalable = Matn.matn_typeface_is_scalable(mtFace) != 0;
        boolean hasColor = Matn.matn_typeface_has_color(mtFace) != 0;
        boolean hasVariations = Matn.matn_typeface_has_variations(mtFace) != 0;

        VarAxis[] varAxes = new VarAxis[Matn.matn_typeface_get_var_axis_count(mtFace)];
        MatnVarAxis.MatnVarAxisPointer varAxesPtr = new MatnVarAxis.MatnVarAxisPointer(varAxes.length, true);
        Matn.matn_typeface_get_var_axes(mtFace, varAxesPtr, varAxes.length);
        for (int i = 0; i < varAxes.length; ++i) {
            MatnVarAxis v = varAxesPtr.get(i);
            BytePointer tagBuf = v.tag();
            varAxes[i] = new VarAxis(
                    new String(new byte[]{tagBuf.getByte(0), tagBuf.getByte(1), tagBuf.getByte(2), tagBuf.getByte(3)}, StandardCharsets.US_ASCII),
                    v.min_value(),
                    v.default_value(),
                    v.max_value()
            );
        }

        NamedInstance[] namedInstances = new NamedInstance[Matn.matn_typeface_get_var_axis_count(mtFace)];
        for (int i = 0; i < varAxes.length; ++i) {
            MatnVarInstance.MatnVarInstancePointer namedInstancePtr = new MatnVarInstance.MatnVarInstancePointer(1, true);
            Matn.matn_typeface_get_named_instance(mtFace, i, namedInstancePtr);
            MatnVarInstance inst = namedInstancePtr.get();
            float[] coords = new float[inst.coord_count()];
            for (int j = 0; j < inst.coord_count(); ++j) {
                coords[j] = inst.coords().getFloat(j);
            }
            namedInstances[i] = new NamedInstance(inst.name().isNull() ? "" : inst.name().getString(), coords);
        }

        return new Typeface(mtFace, isScalable, hasColor, hasVariations, varAxes, namedInstances, (int) Matn.matn_typeface_get_upem(mtFace));
    }

    /*
    Copied from gdx-freetype font loader
    https://github.com/libgdx/libgdx/blob/master/extensions/gdx-freetype/src/com/badlogic/gdx/graphics/g2d/freetype/FreeType.java
     */
    public static Typeface fromFile(FileHandle file, int index) {
        ByteBuffer buffer = null;
        try {
            buffer = file.map();
        } catch (GdxRuntimeException ignored) {
            // OK to ignore, some platforms do not support file mapping.
        }
        if (buffer == null) {
            InputStream input = file.read();
            try {
                int fileSize = (int) file.length();
                if (fileSize == 0) {
                    // Copy to a byte[] to get the size, then copy to the buffer.
                    byte[] data = StreamUtils.copyStreamToByteArray(input, 1024 * 16);
                    buffer = BufferUtils.newUnsafeByteBuffer(data.length);
                    BufferUtils.copy(data, 0, buffer, data.length);
                } else {
                    // Trust the specified file size.
                    buffer = BufferUtils.newUnsafeByteBuffer(fileSize);
                    StreamUtils.copyStream(input, buffer);
                }
            } catch (IOException ex) {
                throw new GdxRuntimeException(ex);
            } finally {
                StreamUtils.closeQuietly(input);
            }
        }
        return loadMemory(buffer, index);
    }

    public static Typeface fromFile(FileHandle file) {
        return fromFile(file, 0);
    }

    public Font createFont(GlyphAtlas atlas) {
        return new Font(this, atlas);
    }

    @Override
    public void dispose() {
        Matn.matn_typeface_destroy(mtFace);
    }
}
