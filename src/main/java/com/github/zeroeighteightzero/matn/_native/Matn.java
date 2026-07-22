package com.github.zeroeighteightzero.matn._native;

import com.badlogic.gdx.jnigen.runtime.CHandler;
import com.badlogic.gdx.jnigen.runtime.c.CXXException;
import com.badlogic.gdx.jnigen.runtime.pointer.FloatPointer;
import com.badlogic.gdx.jnigen.runtime.pointer.PointerPointer;
import com.badlogic.gdx.jnigen.runtime.pointer.integer.*;
import com.badlogic.gdx.utils.SharedLibraryLoader;
import com.github.zeroeighteightzero.matn._native.enums.MatnGPU_LANGUAGE;
import com.github.zeroeighteightzero.matn._native.enums.MatnPixelFormat;
import com.github.zeroeighteightzero.matn._native.enums.MatnResult;
import com.github.zeroeighteightzero.matn._native.structs.*;

public final class Matn {

    static {
        new SharedLibraryLoader().load("matn");
        CHandler.init();
        FFITypes.init();
        init(IllegalArgumentException.class, CXXException.class);
    }

    public static void initialize() {
    }

    /*JNI
#include <jnigen.h>
#include <matn.h>

static jclass illegalArgumentExceptionClass = NULL;
static jclass cxxExceptionClass = NULL;
*/
    private static native void init(Class<?> illegalArgumentException, Class<?> cxxException);/*
    	illegalArgumentExceptionClass = (jclass)env->NewGlobalRef(illegalArgumentException);
    	cxxExceptionClass = (jclass)env->NewGlobalRef(cxxException);
    */

    public static MatnResult matn_typeface_from_file(BytePointer path, int index, PointerPointer<MatnTypeface.MatnTypefacePointer> out_typeface) {
        return MatnResult.getByIndex((int) matn_typeface_from_file_internal(path.getPointer(), index, out_typeface.getPointer()));
    }

    public static native int matn_typeface_from_file_internal(long path, int index, long out_typeface);/*
    	HANDLE_JAVA_EXCEPTION_START()
    	CHECK_AND_THROW_C_TYPE(env, int, index, 1, return 0);
    	return (jint)matn_typeface_from_file((const char *)path, (int)index, (MatnTypeface **)out_typeface);
    	HANDLE_JAVA_EXCEPTION_END()
    	return 0;
    */

    public static MatnResult matn_typeface_from_memory(BytePointer data, long size, int index, PointerPointer<MatnTypeface.MatnTypefacePointer> out_typeface) {
        return MatnResult.getByIndex((int) matn_typeface_from_memory_internal(data.getPointer(), size, index, out_typeface.getPointer()));
    }

    public static native int matn_typeface_from_memory_internal(long data, long size, int index, long out_typeface);/*
    	HANDLE_JAVA_EXCEPTION_START()
    	CHECK_AND_THROW_C_TYPE(env, int, index, 2, return 0);
    	CHECK_AND_THROW_C_TYPE(env, uint32_t, size, 1, return 0);
    	return (jint)matn_typeface_from_memory((const char *)data, (uint32_t)size, (int)index, (MatnTypeface **)out_typeface);
    	HANDLE_JAVA_EXCEPTION_END()
    	return 0;
    */

    public static void matn_typeface_destroy(MatnTypeface.MatnTypefacePointer face) {
        matn_typeface_destroy_internal(face.getPointer());
    }

    public static native void matn_typeface_destroy_internal(long face);/*
    	HANDLE_JAVA_EXCEPTION_START()
    	matn_typeface_destroy((MatnTypeface *)face);
    	HANDLE_JAVA_EXCEPTION_END()
    */

    public static int matn_typeface_has_color(MatnTypeface.MatnTypefacePointer face) {
        return matn_typeface_has_color_internal(face.getPointer());
    }

    public static native int matn_typeface_has_color_internal(long face);/*
    	HANDLE_JAVA_EXCEPTION_START()
    	return (jint)matn_typeface_has_color((MatnTypeface *)face);
    	HANDLE_JAVA_EXCEPTION_END()
    	return 0;
    */

    public static int matn_typeface_is_scalable(MatnTypeface.MatnTypefacePointer face) {
        return matn_typeface_is_scalable_internal(face.getPointer());
    }

    public static native int matn_typeface_is_scalable_internal(long face);/*
    	HANDLE_JAVA_EXCEPTION_START()
    	return (jint)matn_typeface_is_scalable((MatnTypeface *)face);
    	HANDLE_JAVA_EXCEPTION_END()
    	return 0;
    */

    public static int matn_typeface_has_variations(MatnTypeface.MatnTypefacePointer face) {
        return matn_typeface_has_variations_internal(face.getPointer());
    }

    public static native int matn_typeface_has_variations_internal(long face);/*
    	HANDLE_JAVA_EXCEPTION_START()
    	return (jint)matn_typeface_has_variations((MatnTypeface *)face);
    	HANDLE_JAVA_EXCEPTION_END()
    	return 0;
    */

    public static long matn_typeface_get_upem(MatnTypeface.MatnTypefacePointer face) {
        return matn_typeface_get_upem_internal(face.getPointer());
    }

    public static native long matn_typeface_get_upem_internal(long face);/*
    	HANDLE_JAVA_EXCEPTION_START()
    	return (jlong)matn_typeface_get_upem((MatnTypeface *)face);
    	HANDLE_JAVA_EXCEPTION_END()
    	return 0;
    */

    public static int matn_typeface_get_var_axis_count(MatnTypeface.MatnTypefacePointer face) {
        return matn_typeface_get_var_axis_count_internal(face.getPointer());
    }

    public static native int matn_typeface_get_var_axis_count_internal(long face);/*
    	HANDLE_JAVA_EXCEPTION_START()
    	return (jint)matn_typeface_get_var_axis_count((const MatnTypeface *)face);
    	HANDLE_JAVA_EXCEPTION_END()
    	return 0;
    */

    public static int matn_typeface_get_var_axes(MatnTypeface.MatnTypefacePointer face, MatnVarAxis.MatnVarAxisPointer out_axes, int max_axes) {
        return matn_typeface_get_var_axes_internal(face.getPointer(), out_axes.getPointer(), max_axes);
    }

    public static native int matn_typeface_get_var_axes_internal(long face, long out_axes, int max_axes);/*
    	HANDLE_JAVA_EXCEPTION_START()
    	CHECK_AND_THROW_C_TYPE(env, int, max_axes, 2, return 0);
    	return (jint)matn_typeface_get_var_axes((const MatnTypeface *)face, (MatnVarAxis *)out_axes, (int)max_axes);
    	HANDLE_JAVA_EXCEPTION_END()
    	return 0;
    */

    public static int matn_typeface_get_named_instance_count(MatnTypeface.MatnTypefacePointer face) {
        return matn_typeface_get_named_instance_count_internal(face.getPointer());
    }

    public static native int matn_typeface_get_named_instance_count_internal(long face);/*
    	HANDLE_JAVA_EXCEPTION_START()
    	return (jint)matn_typeface_get_named_instance_count((const MatnTypeface *)face);
    	HANDLE_JAVA_EXCEPTION_END()
    	return 0;
    */

    public static MatnResult matn_typeface_get_named_instance(MatnTypeface.MatnTypefacePointer face, int index, MatnVarInstance.MatnVarInstancePointer out_instance) {
        return MatnResult.getByIndex((int) matn_typeface_get_named_instance_internal(face.getPointer(), index, out_instance.getPointer()));
    }

    public static native int matn_typeface_get_named_instance_internal(long face, int index, long out_instance);/*
    	HANDLE_JAVA_EXCEPTION_START()
    	CHECK_AND_THROW_C_TYPE(env, int, index, 1, return 0);
    	return (jint)matn_typeface_get_named_instance((const MatnTypeface *)face, (int)index, (MatnVarInstance *)out_instance);
    	HANDLE_JAVA_EXCEPTION_END()
    	return 0;
    */

    public static MatnResult matn_typeface_create_font(MatnTypeface.MatnTypefacePointer face, PointerPointer<MatnFont.MatnFontPointer> out_font) {
        return MatnResult.getByIndex((int) matn_typeface_create_font_internal(face.getPointer(), out_font.getPointer()));
    }

    public static native int matn_typeface_create_font_internal(long face, long out_font);/*
    	HANDLE_JAVA_EXCEPTION_START()
    	return (jint)matn_typeface_create_font((MatnTypeface *)face, (MatnFont **)out_font);
    	HANDLE_JAVA_EXCEPTION_END()
    	return 0;
    */

    public static void matn_font_destroy(MatnFont.MatnFontPointer font) {
        matn_font_destroy_internal(font.getPointer());
    }

    public static native void matn_font_destroy_internal(long font);/*
    	HANDLE_JAVA_EXCEPTION_START()
    	matn_font_destroy((MatnFont *)font);
    	HANDLE_JAVA_EXCEPTION_END()
    */

    public static MatnResult matn_font_set_var_coords(MatnFont.MatnFontPointer font, FloatPointer coords, long coords_count) {
        return MatnResult.getByIndex((int) matn_font_set_var_coords_internal(font.getPointer(), coords.getPointer(), coords_count));
    }

    public static native int matn_font_set_var_coords_internal(long font, long coords, long coords_count);/*
    	HANDLE_JAVA_EXCEPTION_START()
    	CHECK_AND_THROW_C_TYPE(env, uint32_t, coords_count, 2, return 0);
    	return (jint)matn_font_set_var_coords((MatnFont *)font, (const float *)coords, (uint32_t)coords_count);
    	HANDLE_JAVA_EXCEPTION_END()
    	return 0;
    */

    public static void matn_font_get_synthetic_bold(MatnFont.MatnFontPointer font, FloatPointer x, FloatPointer y, SIntPointer in_place) {
        matn_font_get_synthetic_bold_internal(font.getPointer(), x.getPointer(), y.getPointer(), in_place.getPointer());
    }

    public static native void matn_font_get_synthetic_bold_internal(long font, long x, long y, long in_place);/*
    	HANDLE_JAVA_EXCEPTION_START()
    	matn_font_get_synthetic_bold((const MatnFont *)font, (float *)x, (float *)y, (int *)in_place);
    	HANDLE_JAVA_EXCEPTION_END()
    */

    public static void matn_font_set_synthetic_bold(MatnFont.MatnFontPointer font, float x, float y, int in_place) {
        matn_font_set_synthetic_bold_internal(font.getPointer(), x, y, in_place);
    }

    public static native void matn_font_set_synthetic_bold_internal(long font, float x, float y, int in_place);/*
    	HANDLE_JAVA_EXCEPTION_START()
    	CHECK_AND_THROW_C_TYPE(env, int, in_place, 3, return);
    	matn_font_set_synthetic_bold((const MatnFont *)font, (float)x, (float)y, (int)in_place);
    	HANDLE_JAVA_EXCEPTION_END()
    */

    public static float matn_font_get_synthetic_slant(MatnFont.MatnFontPointer font) {
        return matn_font_get_synthetic_slant_internal(font.getPointer());
    }

    public static native float matn_font_get_synthetic_slant_internal(long font);/*
    	HANDLE_JAVA_EXCEPTION_START()
    	return (jfloat)matn_font_get_synthetic_slant((const MatnFont *)font);
    	HANDLE_JAVA_EXCEPTION_END()
    	return 0;
    */

    public static void matn_font_set_synthetic_slant(MatnFont.MatnFontPointer font, float slant) {
        matn_font_set_synthetic_slant_internal(font.getPointer(), slant);
    }

    public static native void matn_font_set_synthetic_slant_internal(long font, float slant);/*
    	HANDLE_JAVA_EXCEPTION_START()
    	matn_font_set_synthetic_slant((const MatnFont *)font, (float)slant);
    	HANDLE_JAVA_EXCEPTION_END()
    */

    public static float matn_font_get_ascender(MatnFont.MatnFontPointer font) {
        return matn_font_get_ascender_internal(font.getPointer());
    }

    public static native float matn_font_get_ascender_internal(long font);/*
    	HANDLE_JAVA_EXCEPTION_START()
    	return (jfloat)matn_font_get_ascender((const MatnFont *)font);
    	HANDLE_JAVA_EXCEPTION_END()
    	return 0;
    */

    public static float matn_font_get_descender(MatnFont.MatnFontPointer font) {
        return matn_font_get_descender_internal(font.getPointer());
    }

    public static native float matn_font_get_descender_internal(long font);/*
    	HANDLE_JAVA_EXCEPTION_START()
    	return (jfloat)matn_font_get_descender((const MatnFont *)font);
    	HANDLE_JAVA_EXCEPTION_END()
    	return 0;
    */

    public static float matn_font_get_line_gap(MatnFont.MatnFontPointer font) {
        return matn_font_get_line_gap_internal(font.getPointer());
    }

    public static native float matn_font_get_line_gap_internal(long font);/*
    	HANDLE_JAVA_EXCEPTION_START()
    	return (jfloat)matn_font_get_line_gap((const MatnFont *)font);
    	HANDLE_JAVA_EXCEPTION_END()
    	return 0;
    */

    public static long matn_font_get_size_px(MatnFont.MatnFontPointer font) {
        return matn_font_get_size_px_internal(font.getPointer());
    }

    public static native long matn_font_get_size_px_internal(long font);/*
    	HANDLE_JAVA_EXCEPTION_START()
    	return (jlong)matn_font_get_size_px((const MatnFont *)font);
    	HANDLE_JAVA_EXCEPTION_END()
    	return 0;
    */

    public static long matn_font_get_glyph_id(MatnFont.MatnFontPointer font, long codepoint) {
        return matn_font_get_glyph_id_internal(font.getPointer(), codepoint);
    }

    public static native long matn_font_get_glyph_id_internal(long font, long codepoint);/*
    	HANDLE_JAVA_EXCEPTION_START()
    	CHECK_AND_THROW_C_TYPE(env, uint32_t, codepoint, 1, return 0);
    	return (jlong)matn_font_get_glyph_id((const MatnFont *)font, (uint32_t)codepoint);
    	HANDLE_JAVA_EXCEPTION_END()
    	return 0;
    */

    public static MatnResult matn_font_get_glyph_metrics(MatnFont.MatnFontPointer font, long glyph_id, MatnGlyphMetrics.MatnGlyphMetricsPointer out_metrics) {
        return MatnResult.getByIndex((int) matn_font_get_glyph_metrics_internal(font.getPointer(), glyph_id, out_metrics.getPointer()));
    }

    public static native int matn_font_get_glyph_metrics_internal(long font, long glyph_id, long out_metrics);/*
    	HANDLE_JAVA_EXCEPTION_START()
    	CHECK_AND_THROW_C_TYPE(env, uint32_t, glyph_id, 1, return 0);
    	return (jint)matn_font_get_glyph_metrics((MatnFont *)font, (uint32_t)glyph_id, (MatnGlyphMetrics *)out_metrics);
    	HANDLE_JAVA_EXCEPTION_END()
    	return 0;
    */

    public static MatnResult matn_shape_set_utf8(MatnFont.MatnFontPointer font, BytePointer utf8_text, int text_length, int offset, int length) {
        return MatnResult.getByIndex((int) matn_shape_set_utf8_internal(font.getPointer(), utf8_text.getPointer(), text_length, offset, length));
    }

    public static native int matn_shape_set_utf8_internal(long font, long utf8_text, int text_length, int offset, int length);/*
    	HANDLE_JAVA_EXCEPTION_START()
    	CHECK_AND_THROW_C_TYPE(env, int, length, 4, return 0);
    	CHECK_AND_THROW_C_TYPE(env, int, offset, 3, return 0);
    	CHECK_AND_THROW_C_TYPE(env, int, text_length, 2, return 0);
    	return (jint)matn_shape_set_utf8((MatnFont *)font, (const char *)utf8_text, (int)text_length, (int)offset, (int)length);
    	HANDLE_JAVA_EXCEPTION_END()
    	return 0;
    */

    public static MatnResult matn_shape_set_utf16(MatnFont.MatnFontPointer font, UShortPointer utf16_text, int text_length, int offset, int length) {
        return MatnResult.getByIndex((int) matn_shape_set_utf16_internal(font.getPointer(), utf16_text.getPointer(), text_length, offset, length));
    }

    public static native int matn_shape_set_utf16_internal(long font, long utf16_text, int text_length, int offset, int length);/*
    	HANDLE_JAVA_EXCEPTION_START()
    	CHECK_AND_THROW_C_TYPE(env, int, length, 4, return 0);
    	CHECK_AND_THROW_C_TYPE(env, int, offset, 3, return 0);
    	CHECK_AND_THROW_C_TYPE(env, int, text_length, 2, return 0);
    	return (jint)matn_shape_set_utf16((MatnFont *)font, (const uint16_t *)utf16_text, (int)text_length, (int)offset, (int)length);
    	HANDLE_JAVA_EXCEPTION_END()
    	return 0;
    */

    public static MatnResult matn_shape_set_utf32(MatnFont.MatnFontPointer font, UIntPointer utf32_text, int text_length, int offset, int length) {
        return MatnResult.getByIndex((int) matn_shape_set_utf32_internal(font.getPointer(), utf32_text.getPointer(), text_length, offset, length));
    }

    public static native int matn_shape_set_utf32_internal(long font, long utf32_text, int text_length, int offset, int length);/*
    	HANDLE_JAVA_EXCEPTION_START()
    	CHECK_AND_THROW_C_TYPE(env, int, length, 4, return 0);
    	CHECK_AND_THROW_C_TYPE(env, int, offset, 3, return 0);
    	CHECK_AND_THROW_C_TYPE(env, int, text_length, 2, return 0);
    	return (jint)matn_shape_set_utf32((MatnFont *)font, (const uint32_t *)utf32_text, (int)text_length, (int)offset, (int)length);
    	HANDLE_JAVA_EXCEPTION_END()
    	return 0;
    */

    public static int matn_shape_is_rtl(MatnFont.MatnFontPointer font) {
        return matn_shape_is_rtl_internal(font.getPointer());
    }

    public static native int matn_shape_is_rtl_internal(long font);/*
    	HANDLE_JAVA_EXCEPTION_START()
    	return (jint)matn_shape_is_rtl((MatnFont *)font);
    	HANDLE_JAVA_EXCEPTION_END()
    	return 0;
    */

    public static MatnResult matn_shape(MatnFont.MatnFontPointer font) {
        return MatnResult.getByIndex((int) matn_shape_internal(font.getPointer()));
    }

    public static native int matn_shape_internal(long font);/*
    	HANDLE_JAVA_EXCEPTION_START()
    	return (jint)matn_shape((MatnFont *)font);
    	HANDLE_JAVA_EXCEPTION_END()
    	return 0;
    */

    public static MatnBufferView.MatnBufferViewPointer matn_shape_view_buffer(MatnFont.MatnFontPointer font) {
        return new MatnBufferView.MatnBufferViewPointer(matn_shape_view_buffer_internal(font.getPointer()), false);
    }

    public static void matn_shape_view_buffer(MatnFont.MatnFontPointer font, MatnBufferView.MatnBufferViewPointer _retPar) {
        _retPar.setPointer(matn_shape_view_buffer_internal(font.getPointer()));
    }

    public static native long matn_shape_view_buffer_internal(long font);/*
    	HANDLE_JAVA_EXCEPTION_START()
    	return (jlong)matn_shape_view_buffer((const MatnFont *)font);
    	HANDLE_JAVA_EXCEPTION_END()
    	return 0;
    */

    public static MatnResult matn_gpu_draw_glyph(MatnFont.MatnFontPointer font, long glyph_id, PointerPointer<MatnGPU_Blob.MatnGPU_BlobPointer> out_blob) {
        return MatnResult.getByIndex((int) matn_gpu_draw_glyph_internal(font.getPointer(), glyph_id, out_blob.getPointer()));
    }

    public static native int matn_gpu_draw_glyph_internal(long font, long glyph_id, long out_blob);/*
    	HANDLE_JAVA_EXCEPTION_START()
    	CHECK_AND_THROW_C_TYPE(env, uint32_t, glyph_id, 1, return 0);
    	return (jint)matn_gpu_draw_glyph((MatnFont *)font, (uint32_t)glyph_id, (MatnGPU_Blob **)out_blob);
    	HANDLE_JAVA_EXCEPTION_END()
    	return 0;
    */

    public static BytePointer matn_gpu_get_vertex(MatnGPU_LANGUAGE language) {
        return new BytePointer(matn_gpu_get_vertex_internal(language.getIndex()), false);
    }

    public static void matn_gpu_get_vertex(MatnGPU_LANGUAGE language, BytePointer _retPar) {
        _retPar.setPointer(matn_gpu_get_vertex_internal(language.getIndex()));
    }

    public static native long matn_gpu_get_vertex_internal(int language);/*
    	HANDLE_JAVA_EXCEPTION_START()
    	return (jlong)matn_gpu_get_vertex((MatnGPU_LANGUAGE)language);
    	HANDLE_JAVA_EXCEPTION_END()
    	return 0;
    */

    public static BytePointer matn_gpu_get_fragment(MatnGPU_LANGUAGE language) {
        return new BytePointer(matn_gpu_get_fragment_internal(language.getIndex()), false);
    }

    public static void matn_gpu_get_fragment(MatnGPU_LANGUAGE language, BytePointer _retPar) {
        _retPar.setPointer(matn_gpu_get_fragment_internal(language.getIndex()));
    }

    public static native long matn_gpu_get_fragment_internal(int language);/*
    	HANDLE_JAVA_EXCEPTION_START()
    	return (jlong)matn_gpu_get_fragment((MatnGPU_LANGUAGE)language);
    	HANDLE_JAVA_EXCEPTION_END()
    	return 0;
    */

    public static MatnResult matn_rasterize_glyph(MatnFont.MatnFontPointer font, long glyph_id, long size_px, PointerPointer<MatnBlob.MatnBlobPointer> out_blob) {
        return MatnResult.getByIndex((int) matn_rasterize_glyph_internal(font.getPointer(), glyph_id, size_px, out_blob.getPointer()));
    }

    public static native int matn_rasterize_glyph_internal(long font, long glyph_id, long size_px, long out_blob);/*
    	HANDLE_JAVA_EXCEPTION_START()
    	CHECK_AND_THROW_C_TYPE(env, uint32_t, size_px, 2, return 0);
    	CHECK_AND_THROW_C_TYPE(env, uint32_t, glyph_id, 1, return 0);
    	return (jint)matn_rasterize_glyph((MatnFont *)font, (uint32_t)glyph_id, (uint32_t)size_px, (MatnBlob **)out_blob);
    	HANDLE_JAVA_EXCEPTION_END()
    	return 0;
    */

    public static void matn_blob_destroy(MatnBlob.MatnBlobPointer blob) {
        matn_blob_destroy_internal(blob.getPointer());
    }

    public static native void matn_blob_destroy_internal(long blob);/*
    	HANDLE_JAVA_EXCEPTION_START()
    	matn_blob_destroy((MatnBlob *)blob);
    	HANDLE_JAVA_EXCEPTION_END()
    */

    public static UBytePointer matn_blob_get_data(MatnBlob.MatnBlobPointer blob) {
        return new UBytePointer(matn_blob_get_data_internal(blob.getPointer()), false);
    }

    public static void matn_blob_get_data(MatnBlob.MatnBlobPointer blob, UBytePointer _retPar) {
        _retPar.setPointer(matn_blob_get_data_internal(blob.getPointer()));
    }

    public static native long matn_blob_get_data_internal(long blob);/*
    	HANDLE_JAVA_EXCEPTION_START()
    	return (jlong)matn_blob_get_data((const MatnBlob *)blob);
    	HANDLE_JAVA_EXCEPTION_END()
    	return 0;
    */

    public static int matn_blob_get_width(MatnBlob.MatnBlobPointer blob) {
        return matn_blob_get_width_internal(blob.getPointer());
    }

    public static native int matn_blob_get_width_internal(long blob);/*
    	HANDLE_JAVA_EXCEPTION_START()
    	return (jint)matn_blob_get_width((const MatnBlob *)blob);
    	HANDLE_JAVA_EXCEPTION_END()
    	return 0;
    */

    public static int matn_blob_get_height(MatnBlob.MatnBlobPointer blob) {
        return matn_blob_get_height_internal(blob.getPointer());
    }

    public static native int matn_blob_get_height_internal(long blob);/*
    	HANDLE_JAVA_EXCEPTION_START()
    	return (jint)matn_blob_get_height((const MatnBlob *)blob);
    	HANDLE_JAVA_EXCEPTION_END()
    	return 0;
    */

    public static int matn_blob_get_stride(MatnBlob.MatnBlobPointer blob) {
        return matn_blob_get_stride_internal(blob.getPointer());
    }

    public static native int matn_blob_get_stride_internal(long blob);/*
    	HANDLE_JAVA_EXCEPTION_START()
    	return (jint)matn_blob_get_stride((const MatnBlob *)blob);
    	HANDLE_JAVA_EXCEPTION_END()
    	return 0;
    */

    public static int matn_blob_get_left(MatnBlob.MatnBlobPointer blob) {
        return matn_blob_get_left_internal(blob.getPointer());
    }

    public static native int matn_blob_get_left_internal(long blob);/*
    	HANDLE_JAVA_EXCEPTION_START()
    	return (jint)matn_blob_get_left((const MatnBlob *)blob);
    	HANDLE_JAVA_EXCEPTION_END()
    	return 0;
    */

    public static int matn_blob_get_top(MatnBlob.MatnBlobPointer blob) {
        return matn_blob_get_top_internal(blob.getPointer());
    }

    public static native int matn_blob_get_top_internal(long blob);/*
    	HANDLE_JAVA_EXCEPTION_START()
    	return (jint)matn_blob_get_top((const MatnBlob *)blob);
    	HANDLE_JAVA_EXCEPTION_END()
    	return 0;
    */

    public static MatnPixelFormat matn_blob_get_format(MatnBlob.MatnBlobPointer blob) {
        return MatnPixelFormat.getByIndex((int) matn_blob_get_format_internal(blob.getPointer()));
    }

    public static native int matn_blob_get_format_internal(long blob);/*
    	HANDLE_JAVA_EXCEPTION_START()
    	return (jint)matn_blob_get_format((const MatnBlob *)blob);
    	HANDLE_JAVA_EXCEPTION_END()
    	return 0;
    */

    public static void matn_cleanup() {
        matn_cleanup_internal();
    }

    public static native void matn_cleanup_internal();/*
    	HANDLE_JAVA_EXCEPTION_START()
    	matn_cleanup();
    	HANDLE_JAVA_EXCEPTION_END()
    */
}
