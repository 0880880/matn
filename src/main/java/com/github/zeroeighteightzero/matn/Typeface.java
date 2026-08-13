package com.github.zeroeighteightzero.matn;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.jnigen.runtime.pointer.PointerPointer;
import com.badlogic.gdx.jnigen.runtime.pointer.integer.BytePointer;
import com.badlogic.gdx.utils.*;
import com.github.zeroeighteightzero.matn._native.Matn;
import com.github.zeroeighteightzero.matn._native.structs.MatnTypeface;
import com.github.zeroeighteightzero.matn._native.structs.MatnVarAxis;
import com.github.zeroeighteightzero.matn._native.structs.MatnVarInstance;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

/**
 * A loaded font face with its metadata and variation information.
 *
 * <p>Wraps a native text-engine typeface and exposes its scalability, color and variation
 * flags, its units-per-em, and its variable-font axes and named instances. Load via the
 * public constructors from a {@link FileHandle}, an internal path, or a file plus a font
 * index. Obtain {@link Font} instances by passing this typeface to {@link Font}'s
 * constructor.</p>
 */
public class Typeface implements Disposable {

    final MatnTypeface.MatnTypefacePointer mtFace;

    /** Whether the typeface is scalable (vector) rather than bitmap. */
    public final boolean isScalable;
    /** Whether the typeface contains color glyphs. */
    public final boolean hasColor;
    /** Whether the typeface has variable-font axes. */
    public final boolean hasVariations;
    /** The units-per-em of the typeface. */
    public final int upem;

    public final float underlineThickness, underlinePosition;
    public final float strikoutThickness, strikoutPosition;

    /** The variable-font axes of this typeface, if any. */
    public final VarAxis[] varAxes;
    /** The named variation instances of this typeface, if any. */
    public final NamedInstance[] namedInstances;

    protected final Array<Font> managedFonts = new Array<>();

    private Typeface(MatnTypeface.MatnTypefacePointer mtFace) {
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

        this.mtFace = mtFace;
        this.isScalable = isScalable;
        this.hasColor = hasColor;
        this.hasVariations = hasVariations;
        this.varAxes = varAxes;
        this.namedInstances = namedInstances;
        this.upem = (int) Matn.matn_typeface_get_upem(mtFace);

        this.underlinePosition = Matn.matn_typeface_get_underline_position(mtFace);
        this.underlineThickness = Matn.matn_typeface_get_underline_position(mtFace);
        this.strikoutPosition = Matn.matn_typeface_get_strikeout_position(mtFace);
        this.strikoutThickness = Matn.matn_typeface_get_strikeout_thickness(mtFace);
    }

    private static MatnTypeface.MatnTypefacePointer loadMemory(ByteBuffer data, int index) {
        PointerPointer<MatnTypeface.MatnTypefacePointer> ptr = new PointerPointer<>(MatnTypeface.MatnTypefacePointer::new);
        Matn.matn_typeface_from_memory(new BytePointer(BufferUtils.getUnsafeBufferAddress(data), false), data.remaining(), index, ptr);
        return ptr.getValue();
    }

    /*
    Copied from gdx-freetype font loader
    https://github.com/libgdx/libgdx/blob/master/extensions/gdx-freetype/src/com/badlogic/gdx/graphics/g2d/freetype/FreeType.java
     */
    private static ByteBuffer fileToBuffer(FileHandle file) {
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
        return buffer;
    }

    /**
     * Loads a typeface from a file handle, selecting the given font face.
     *
     * @param file the file containing the font data
     * @param index the zero-based index of the font face within the collection
     */
    public Typeface(FileHandle file, int index) {
        this(loadMemory(fileToBuffer(file), index));
    }

    /**
     * Loads the first font face from a file handle.
     *
     * @param file the file containing the font data
     */
    public Typeface(FileHandle file) {
        this(file, 0);
    }

    /**
     * Loads a typeface from an internal file path, selecting the given font face.
     *
     * @param internalPath an internal (classpath) path to the font file
     * @param index the zero-based index of the font face within the collection
     */
    public Typeface(String internalPath, int index) {
        this(Gdx.files.internal(internalPath), index);
    }

    /**
     * Loads the first font face from an internal file path.
     *
     * @param internalPath an internal (classpath) path to the font file
     */
    public Typeface(String internalPath) {
        this(internalPath, 0);
    }

    /**
     * Gets a variable-font axis by its OpenType tag.
     *
     * @param tag the four-character OpenType variation-axis tag
     * @return the matching variable-font axis, or {@code null} if {@code tag} is
     *         not four characters long or this typeface does not contain the axis
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
        return null;
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
        return getVariableAxis("wght");
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

    /**
     * Releases the native typeface resource owned by this typeface.
     *
     * <p>All fonts created from this typeface must be disposed first, otherwise a
     * {@link RuntimeException} is thrown.</p>
     *
     * @throws RuntimeException if fonts created from this typeface are still alive
     */
    @Override
    public void dispose() {
        if (managedFonts.notEmpty()) {
            throw new RuntimeException("All fonts must be disposed before disposing typeface.");
        }
        Matn.matn_typeface_destroy(mtFace);
    }
}
