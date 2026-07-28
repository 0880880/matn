package io.github.zeroeighteightzero.matn;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.jnigen.runtime.pointer.PointerPointer;
import com.badlogic.gdx.jnigen.runtime.pointer.integer.BytePointer;
import com.badlogic.gdx.utils.BufferUtils;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.StreamUtils;
import io.github.zeroeighteightzero.matn._native.Matn;
import io.github.zeroeighteightzero.matn._native.structs.MatnTypeface;
import io.github.zeroeighteightzero.matn._native.structs.MatnVarAxis;
import io.github.zeroeighteightzero.matn._native.structs.MatnVarInstance;

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

    private Typeface(MatnTypeface.MatnTypefacePointer mtFace, boolean isScalable, boolean hasColor, boolean hasVariations, VarAxis[] varAxes, NamedInstance[] namedInstances, int upem) {
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
    /**
     * Loads a typeface from a font file and selects a face from the file.
     *
     * <p>The face index is useful for font collections containing multiple
     * typefaces. A value of {@code 0} selects the first face.</p>
     *
     * @param file the font file to load
     * @param index the zero-based face index within the font file or collection
     * @return a newly loaded typeface
     * @throws GdxRuntimeException if the file cannot be read
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

    /**
     * Loads the first typeface from a font file.
     *
     * @param file the font file to load
     * @return a newly loaded typeface
     * @throws GdxRuntimeException if the file cannot be read
     */
    public static Typeface fromFile(FileHandle file) {
        return fromFile(file, 0);
    }

    /**
     * Creates a font instance from this typeface.
     *
     * <p>The returned font has its own variation and synthetic-style settings,
     * while sharing this typeface's underlying font data.</p>
     *
     * @param atlas the glyph atlas used for rasterized and GPU glyph storage
     * @return a new font instance
     */
    public Font createFont(GlyphAtlas atlas) {
        return new Font(this, atlas);
    }

    /**
     * Gets a variable-font axis by its OpenType tag.
     *
     * @param tag the four-character OpenType variation-axis tag
     * @return the matching variable-font axis, or {@code null} if {@code tag} is not four characters long
     * @throws RuntimeException if this typeface does not contain the specified axis
     */
    public VarAxis getVariableAxis(String tag) {
        if (tag.length() != 4) {
            return null;
        }
        for (VarAxis v : varAxes) {
            if (v.tag.equals(tag)) {
                return v;
            }
        }
        throw new RuntimeException("Typeface does not have '" + tag + "' tag");
    }

    /**
     * Gets the {@code ital} variable-font axis.
     *
     * @return the italic axis
     */
    public VarAxis italic() {
        return getVariableAxis("ital");
    }

    /**
     * Gets the {@code opsz} variable-font axis.
     *
     * @return the optical-size axis
     */
    public VarAxis opticalSize() {
        return getVariableAxis("opsz");
    }

    /**
     * Gets the {@code wght} variable-font axis.
     *
     * @return the weight axis
     */
    public VarAxis weight() {
        return getVariableAxis("wgth");
    }

    /**
     * Gets the {@code wdth} variable-font axis.
     *
     * @return the width axis
     */
    public VarAxis width() {
        return getVariableAxis("wdth");
    }

    /**
     * Gets the {@code slnt} variable-font axis.
     *
     * @return the slant axis
     */
    public VarAxis slant() {
        return getVariableAxis("slnt");
    }

    @Override
    public void dispose() {
        Matn.matn_typeface_destroy(mtFace);
    }
}
