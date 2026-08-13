package com.github.zeroeighteightzero.matn._native;

import com.badlogic.gdx.jnigen.runtime.CHandler;
import com.badlogic.gdx.jnigen.runtime.c.CTypeInfo;
import java.util.HashMap;

public class FFITypes {

    /*JNI
		#include <jnigen.h>
		#include <matn.h>
*/
    /*JNI
		#if !((defined(_WIN32) && ARCH_BITS == 32) || (defined(_WIN32) && ARCH_BITS == 64) || (!defined(_WIN32) && ARCH_BITS == 32 && !(defined(__i386__) && defined(__ANDROID__))) || (!defined(_WIN32) && ARCH_BITS == 64) || (defined(__i386__) && defined(__ANDROID__)))
			#error Unsupported OS/Platform
		#endif
		

		#if defined(_WIN32) && ARCH_BITS == 32
		static_assert(sizeof(const uint16_t) == 2, "Type const uint16_t has unexpected size.");
		static_assert(alignof(const uint16_t) == 2, "Type const uint16_t has unexpected alignment.");
		static_assert(sizeof(const uint8_t) == 1, "Type const uint8_t has unexpected size.");
		static_assert(alignof(const uint8_t) == 1, "Type const uint8_t has unexpected alignment.");
		static_assert(sizeof(const char) == 1, "Type const char has unexpected size.");
		static_assert(alignof(const char) == 1, "Type const char has unexpected alignment.");
		static_assert(sizeof(char) == 1, "Type char has unexpected size.");
		static_assert(alignof(char) == 1, "Type char has unexpected alignment.");
		static_assert(sizeof(uint32_t) == 4, "Type uint32_t has unexpected size.");
		static_assert(alignof(uint32_t) == 4, "Type uint32_t has unexpected alignment.");
		static_assert(sizeof(const float) == 4, "Type const float has unexpected size.");
		static_assert(alignof(const float) == 4, "Type const float has unexpected alignment.");
		static_assert(sizeof(const uint32_t) == 4, "Type const uint32_t has unexpected size.");
		static_assert(alignof(const uint32_t) == 4, "Type const uint32_t has unexpected alignment.");
		static_assert(sizeof(float) == 4, "Type float has unexpected size.");
		static_assert(alignof(float) == 4, "Type float has unexpected alignment.");
		static_assert(sizeof(int) == 4, "Type int has unexpected size.");
		static_assert(alignof(int) == 4, "Type int has unexpected alignment.");
		static_assert(sizeof(MatnVarInstance) == 12, "Type MatnVarInstance has unexpected size.");
		static_assert(alignof(MatnVarInstance) == 4, "Type MatnVarInstance has unexpected alignment.");
		static_assert(offsetof(MatnVarInstance, name) == 0, "Type MatnVarInstance has unexpected offset.");
		static_assert(offsetof(MatnVarInstance, coords) == 4, "Type MatnVarInstance has unexpected offset.");
		static_assert(offsetof(MatnVarInstance, coord_count) == 8, "Type MatnVarInstance has unexpected offset.");
		static_assert(sizeof(MatnBufferView) == 28, "Type MatnBufferView has unexpected size.");
		static_assert(alignof(MatnBufferView) == 4, "Type MatnBufferView has unexpected alignment.");
		static_assert(offsetof(MatnBufferView, glyph_ids) == 0, "Type MatnBufferView has unexpected offset.");
		static_assert(offsetof(MatnBufferView, x_advances) == 4, "Type MatnBufferView has unexpected offset.");
		static_assert(offsetof(MatnBufferView, y_advances) == 8, "Type MatnBufferView has unexpected offset.");
		static_assert(offsetof(MatnBufferView, x_offsets) == 12, "Type MatnBufferView has unexpected offset.");
		static_assert(offsetof(MatnBufferView, y_offsets) == 16, "Type MatnBufferView has unexpected offset.");
		static_assert(offsetof(MatnBufferView, clusters) == 20, "Type MatnBufferView has unexpected offset.");
		static_assert(offsetof(MatnBufferView, length) == 24, "Type MatnBufferView has unexpected offset.");
		static_assert(sizeof(MatnGlyphMetrics) == 16, "Type MatnGlyphMetrics has unexpected size.");
		static_assert(alignof(MatnGlyphMetrics) == 4, "Type MatnGlyphMetrics has unexpected alignment.");
		static_assert(offsetof(MatnGlyphMetrics, bearing_x) == 0, "Type MatnGlyphMetrics has unexpected offset.");
		static_assert(offsetof(MatnGlyphMetrics, bearing_y) == 4, "Type MatnGlyphMetrics has unexpected offset.");
		static_assert(offsetof(MatnGlyphMetrics, width) == 8, "Type MatnGlyphMetrics has unexpected offset.");
		static_assert(offsetof(MatnGlyphMetrics, height) == 12, "Type MatnGlyphMetrics has unexpected offset.");
		static_assert(sizeof(MatnVarAxis) == 16, "Type MatnVarAxis has unexpected size.");
		static_assert(alignof(MatnVarAxis) == 4, "Type MatnVarAxis has unexpected alignment.");
		static_assert(offsetof(MatnVarAxis, tag) == 0, "Type MatnVarAxis has unexpected offset.");
		static_assert(offsetof(MatnVarAxis, min_value) == 4, "Type MatnVarAxis has unexpected offset.");
		static_assert(offsetof(MatnVarAxis, default_value) == 8, "Type MatnVarAxis has unexpected offset.");
		static_assert(offsetof(MatnVarAxis, max_value) == 12, "Type MatnVarAxis has unexpected offset.");
		static_assert(sizeof(MatnGPU_Blob) == 24, "Type MatnGPU_Blob has unexpected size.");
		static_assert(alignof(MatnGPU_Blob) == 4, "Type MatnGPU_Blob has unexpected alignment.");
		static_assert(offsetof(MatnGPU_Blob, data) == 0, "Type MatnGPU_Blob has unexpected offset.");
		static_assert(offsetof(MatnGPU_Blob, length) == 4, "Type MatnGPU_Blob has unexpected offset.");
		static_assert(offsetof(MatnGPU_Blob, min_x) == 8, "Type MatnGPU_Blob has unexpected offset.");
		static_assert(offsetof(MatnGPU_Blob, min_y) == 12, "Type MatnGPU_Blob has unexpected offset.");
		static_assert(offsetof(MatnGPU_Blob, max_x) == 16, "Type MatnGPU_Blob has unexpected offset.");
		static_assert(offsetof(MatnGPU_Blob, max_y) == 20, "Type MatnGPU_Blob has unexpected offset.");
		#endif // defined(_WIN32) && ARCH_BITS == 32
		

		#if defined(_WIN32) && ARCH_BITS == 64
		static_assert(sizeof(const uint16_t) == 2, "Type const uint16_t has unexpected size.");
		static_assert(alignof(const uint16_t) == 2, "Type const uint16_t has unexpected alignment.");
		static_assert(sizeof(const uint8_t) == 1, "Type const uint8_t has unexpected size.");
		static_assert(alignof(const uint8_t) == 1, "Type const uint8_t has unexpected alignment.");
		static_assert(sizeof(const char) == 1, "Type const char has unexpected size.");
		static_assert(alignof(const char) == 1, "Type const char has unexpected alignment.");
		static_assert(sizeof(char) == 1, "Type char has unexpected size.");
		static_assert(alignof(char) == 1, "Type char has unexpected alignment.");
		static_assert(sizeof(uint32_t) == 4, "Type uint32_t has unexpected size.");
		static_assert(alignof(uint32_t) == 4, "Type uint32_t has unexpected alignment.");
		static_assert(sizeof(const float) == 4, "Type const float has unexpected size.");
		static_assert(alignof(const float) == 4, "Type const float has unexpected alignment.");
		static_assert(sizeof(const uint32_t) == 4, "Type const uint32_t has unexpected size.");
		static_assert(alignof(const uint32_t) == 4, "Type const uint32_t has unexpected alignment.");
		static_assert(sizeof(float) == 4, "Type float has unexpected size.");
		static_assert(alignof(float) == 4, "Type float has unexpected alignment.");
		static_assert(sizeof(int) == 4, "Type int has unexpected size.");
		static_assert(alignof(int) == 4, "Type int has unexpected alignment.");
		static_assert(sizeof(MatnVarInstance) == 24, "Type MatnVarInstance has unexpected size.");
		static_assert(alignof(MatnVarInstance) == 8, "Type MatnVarInstance has unexpected alignment.");
		static_assert(offsetof(MatnVarInstance, name) == 0, "Type MatnVarInstance has unexpected offset.");
		static_assert(offsetof(MatnVarInstance, coords) == 8, "Type MatnVarInstance has unexpected offset.");
		static_assert(offsetof(MatnVarInstance, coord_count) == 16, "Type MatnVarInstance has unexpected offset.");
		static_assert(sizeof(MatnBufferView) == 56, "Type MatnBufferView has unexpected size.");
		static_assert(alignof(MatnBufferView) == 8, "Type MatnBufferView has unexpected alignment.");
		static_assert(offsetof(MatnBufferView, glyph_ids) == 0, "Type MatnBufferView has unexpected offset.");
		static_assert(offsetof(MatnBufferView, x_advances) == 8, "Type MatnBufferView has unexpected offset.");
		static_assert(offsetof(MatnBufferView, y_advances) == 16, "Type MatnBufferView has unexpected offset.");
		static_assert(offsetof(MatnBufferView, x_offsets) == 24, "Type MatnBufferView has unexpected offset.");
		static_assert(offsetof(MatnBufferView, y_offsets) == 32, "Type MatnBufferView has unexpected offset.");
		static_assert(offsetof(MatnBufferView, clusters) == 40, "Type MatnBufferView has unexpected offset.");
		static_assert(offsetof(MatnBufferView, length) == 48, "Type MatnBufferView has unexpected offset.");
		static_assert(sizeof(MatnGlyphMetrics) == 16, "Type MatnGlyphMetrics has unexpected size.");
		static_assert(alignof(MatnGlyphMetrics) == 4, "Type MatnGlyphMetrics has unexpected alignment.");
		static_assert(offsetof(MatnGlyphMetrics, bearing_x) == 0, "Type MatnGlyphMetrics has unexpected offset.");
		static_assert(offsetof(MatnGlyphMetrics, bearing_y) == 4, "Type MatnGlyphMetrics has unexpected offset.");
		static_assert(offsetof(MatnGlyphMetrics, width) == 8, "Type MatnGlyphMetrics has unexpected offset.");
		static_assert(offsetof(MatnGlyphMetrics, height) == 12, "Type MatnGlyphMetrics has unexpected offset.");
		static_assert(sizeof(MatnVarAxis) == 16, "Type MatnVarAxis has unexpected size.");
		static_assert(alignof(MatnVarAxis) == 4, "Type MatnVarAxis has unexpected alignment.");
		static_assert(offsetof(MatnVarAxis, tag) == 0, "Type MatnVarAxis has unexpected offset.");
		static_assert(offsetof(MatnVarAxis, min_value) == 4, "Type MatnVarAxis has unexpected offset.");
		static_assert(offsetof(MatnVarAxis, default_value) == 8, "Type MatnVarAxis has unexpected offset.");
		static_assert(offsetof(MatnVarAxis, max_value) == 12, "Type MatnVarAxis has unexpected offset.");
		static_assert(sizeof(MatnGPU_Blob) == 32, "Type MatnGPU_Blob has unexpected size.");
		static_assert(alignof(MatnGPU_Blob) == 8, "Type MatnGPU_Blob has unexpected alignment.");
		static_assert(offsetof(MatnGPU_Blob, data) == 0, "Type MatnGPU_Blob has unexpected offset.");
		static_assert(offsetof(MatnGPU_Blob, length) == 8, "Type MatnGPU_Blob has unexpected offset.");
		static_assert(offsetof(MatnGPU_Blob, min_x) == 12, "Type MatnGPU_Blob has unexpected offset.");
		static_assert(offsetof(MatnGPU_Blob, min_y) == 16, "Type MatnGPU_Blob has unexpected offset.");
		static_assert(offsetof(MatnGPU_Blob, max_x) == 20, "Type MatnGPU_Blob has unexpected offset.");
		static_assert(offsetof(MatnGPU_Blob, max_y) == 24, "Type MatnGPU_Blob has unexpected offset.");
		#endif // defined(_WIN32) && ARCH_BITS == 64
		

		#if !defined(_WIN32) && ARCH_BITS == 32 && !(defined(__i386__) && defined(__ANDROID__))
		static_assert(sizeof(const uint16_t) == 2, "Type const uint16_t has unexpected size.");
		static_assert(alignof(const uint16_t) == 2, "Type const uint16_t has unexpected alignment.");
		static_assert(sizeof(const uint8_t) == 1, "Type const uint8_t has unexpected size.");
		static_assert(alignof(const uint8_t) == 1, "Type const uint8_t has unexpected alignment.");
		static_assert(sizeof(const char) == 1, "Type const char has unexpected size.");
		static_assert(alignof(const char) == 1, "Type const char has unexpected alignment.");
		static_assert(sizeof(char) == 1, "Type char has unexpected size.");
		static_assert(alignof(char) == 1, "Type char has unexpected alignment.");
		static_assert(sizeof(uint32_t) == 4, "Type uint32_t has unexpected size.");
		static_assert(alignof(uint32_t) == 4, "Type uint32_t has unexpected alignment.");
		static_assert(sizeof(const float) == 4, "Type const float has unexpected size.");
		static_assert(alignof(const float) == 4, "Type const float has unexpected alignment.");
		static_assert(sizeof(const uint32_t) == 4, "Type const uint32_t has unexpected size.");
		static_assert(alignof(const uint32_t) == 4, "Type const uint32_t has unexpected alignment.");
		static_assert(sizeof(float) == 4, "Type float has unexpected size.");
		static_assert(alignof(float) == 4, "Type float has unexpected alignment.");
		static_assert(sizeof(int) == 4, "Type int has unexpected size.");
		static_assert(alignof(int) == 4, "Type int has unexpected alignment.");
		static_assert(sizeof(MatnVarInstance) == 12, "Type MatnVarInstance has unexpected size.");
		static_assert(alignof(MatnVarInstance) == 4, "Type MatnVarInstance has unexpected alignment.");
		static_assert(offsetof(MatnVarInstance, name) == 0, "Type MatnVarInstance has unexpected offset.");
		static_assert(offsetof(MatnVarInstance, coords) == 4, "Type MatnVarInstance has unexpected offset.");
		static_assert(offsetof(MatnVarInstance, coord_count) == 8, "Type MatnVarInstance has unexpected offset.");
		static_assert(sizeof(MatnBufferView) == 28, "Type MatnBufferView has unexpected size.");
		static_assert(alignof(MatnBufferView) == 4, "Type MatnBufferView has unexpected alignment.");
		static_assert(offsetof(MatnBufferView, glyph_ids) == 0, "Type MatnBufferView has unexpected offset.");
		static_assert(offsetof(MatnBufferView, x_advances) == 4, "Type MatnBufferView has unexpected offset.");
		static_assert(offsetof(MatnBufferView, y_advances) == 8, "Type MatnBufferView has unexpected offset.");
		static_assert(offsetof(MatnBufferView, x_offsets) == 12, "Type MatnBufferView has unexpected offset.");
		static_assert(offsetof(MatnBufferView, y_offsets) == 16, "Type MatnBufferView has unexpected offset.");
		static_assert(offsetof(MatnBufferView, clusters) == 20, "Type MatnBufferView has unexpected offset.");
		static_assert(offsetof(MatnBufferView, length) == 24, "Type MatnBufferView has unexpected offset.");
		static_assert(sizeof(MatnGlyphMetrics) == 16, "Type MatnGlyphMetrics has unexpected size.");
		static_assert(alignof(MatnGlyphMetrics) == 4, "Type MatnGlyphMetrics has unexpected alignment.");
		static_assert(offsetof(MatnGlyphMetrics, bearing_x) == 0, "Type MatnGlyphMetrics has unexpected offset.");
		static_assert(offsetof(MatnGlyphMetrics, bearing_y) == 4, "Type MatnGlyphMetrics has unexpected offset.");
		static_assert(offsetof(MatnGlyphMetrics, width) == 8, "Type MatnGlyphMetrics has unexpected offset.");
		static_assert(offsetof(MatnGlyphMetrics, height) == 12, "Type MatnGlyphMetrics has unexpected offset.");
		static_assert(sizeof(MatnVarAxis) == 16, "Type MatnVarAxis has unexpected size.");
		static_assert(alignof(MatnVarAxis) == 4, "Type MatnVarAxis has unexpected alignment.");
		static_assert(offsetof(MatnVarAxis, tag) == 0, "Type MatnVarAxis has unexpected offset.");
		static_assert(offsetof(MatnVarAxis, min_value) == 4, "Type MatnVarAxis has unexpected offset.");
		static_assert(offsetof(MatnVarAxis, default_value) == 8, "Type MatnVarAxis has unexpected offset.");
		static_assert(offsetof(MatnVarAxis, max_value) == 12, "Type MatnVarAxis has unexpected offset.");
		static_assert(sizeof(MatnGPU_Blob) == 24, "Type MatnGPU_Blob has unexpected size.");
		static_assert(alignof(MatnGPU_Blob) == 4, "Type MatnGPU_Blob has unexpected alignment.");
		static_assert(offsetof(MatnGPU_Blob, data) == 0, "Type MatnGPU_Blob has unexpected offset.");
		static_assert(offsetof(MatnGPU_Blob, length) == 4, "Type MatnGPU_Blob has unexpected offset.");
		static_assert(offsetof(MatnGPU_Blob, min_x) == 8, "Type MatnGPU_Blob has unexpected offset.");
		static_assert(offsetof(MatnGPU_Blob, min_y) == 12, "Type MatnGPU_Blob has unexpected offset.");
		static_assert(offsetof(MatnGPU_Blob, max_x) == 16, "Type MatnGPU_Blob has unexpected offset.");
		static_assert(offsetof(MatnGPU_Blob, max_y) == 20, "Type MatnGPU_Blob has unexpected offset.");
		#endif // !defined(_WIN32) && ARCH_BITS == 32 && !(defined(__i386__) && defined(__ANDROID__))
		

		#if !defined(_WIN32) && ARCH_BITS == 64
		static_assert(sizeof(const uint16_t) == 2, "Type const uint16_t has unexpected size.");
		static_assert(alignof(const uint16_t) == 2, "Type const uint16_t has unexpected alignment.");
		static_assert(sizeof(const uint8_t) == 1, "Type const uint8_t has unexpected size.");
		static_assert(alignof(const uint8_t) == 1, "Type const uint8_t has unexpected alignment.");
		static_assert(sizeof(const char) == 1, "Type const char has unexpected size.");
		static_assert(alignof(const char) == 1, "Type const char has unexpected alignment.");
		static_assert(sizeof(char) == 1, "Type char has unexpected size.");
		static_assert(alignof(char) == 1, "Type char has unexpected alignment.");
		static_assert(sizeof(uint32_t) == 4, "Type uint32_t has unexpected size.");
		static_assert(alignof(uint32_t) == 4, "Type uint32_t has unexpected alignment.");
		static_assert(sizeof(const float) == 4, "Type const float has unexpected size.");
		static_assert(alignof(const float) == 4, "Type const float has unexpected alignment.");
		static_assert(sizeof(const uint32_t) == 4, "Type const uint32_t has unexpected size.");
		static_assert(alignof(const uint32_t) == 4, "Type const uint32_t has unexpected alignment.");
		static_assert(sizeof(float) == 4, "Type float has unexpected size.");
		static_assert(alignof(float) == 4, "Type float has unexpected alignment.");
		static_assert(sizeof(int) == 4, "Type int has unexpected size.");
		static_assert(alignof(int) == 4, "Type int has unexpected alignment.");
		static_assert(sizeof(MatnVarInstance) == 24, "Type MatnVarInstance has unexpected size.");
		static_assert(alignof(MatnVarInstance) == 8, "Type MatnVarInstance has unexpected alignment.");
		static_assert(offsetof(MatnVarInstance, name) == 0, "Type MatnVarInstance has unexpected offset.");
		static_assert(offsetof(MatnVarInstance, coords) == 8, "Type MatnVarInstance has unexpected offset.");
		static_assert(offsetof(MatnVarInstance, coord_count) == 16, "Type MatnVarInstance has unexpected offset.");
		static_assert(sizeof(MatnBufferView) == 56, "Type MatnBufferView has unexpected size.");
		static_assert(alignof(MatnBufferView) == 8, "Type MatnBufferView has unexpected alignment.");
		static_assert(offsetof(MatnBufferView, glyph_ids) == 0, "Type MatnBufferView has unexpected offset.");
		static_assert(offsetof(MatnBufferView, x_advances) == 8, "Type MatnBufferView has unexpected offset.");
		static_assert(offsetof(MatnBufferView, y_advances) == 16, "Type MatnBufferView has unexpected offset.");
		static_assert(offsetof(MatnBufferView, x_offsets) == 24, "Type MatnBufferView has unexpected offset.");
		static_assert(offsetof(MatnBufferView, y_offsets) == 32, "Type MatnBufferView has unexpected offset.");
		static_assert(offsetof(MatnBufferView, clusters) == 40, "Type MatnBufferView has unexpected offset.");
		static_assert(offsetof(MatnBufferView, length) == 48, "Type MatnBufferView has unexpected offset.");
		static_assert(sizeof(MatnGlyphMetrics) == 16, "Type MatnGlyphMetrics has unexpected size.");
		static_assert(alignof(MatnGlyphMetrics) == 4, "Type MatnGlyphMetrics has unexpected alignment.");
		static_assert(offsetof(MatnGlyphMetrics, bearing_x) == 0, "Type MatnGlyphMetrics has unexpected offset.");
		static_assert(offsetof(MatnGlyphMetrics, bearing_y) == 4, "Type MatnGlyphMetrics has unexpected offset.");
		static_assert(offsetof(MatnGlyphMetrics, width) == 8, "Type MatnGlyphMetrics has unexpected offset.");
		static_assert(offsetof(MatnGlyphMetrics, height) == 12, "Type MatnGlyphMetrics has unexpected offset.");
		static_assert(sizeof(MatnVarAxis) == 16, "Type MatnVarAxis has unexpected size.");
		static_assert(alignof(MatnVarAxis) == 4, "Type MatnVarAxis has unexpected alignment.");
		static_assert(offsetof(MatnVarAxis, tag) == 0, "Type MatnVarAxis has unexpected offset.");
		static_assert(offsetof(MatnVarAxis, min_value) == 4, "Type MatnVarAxis has unexpected offset.");
		static_assert(offsetof(MatnVarAxis, default_value) == 8, "Type MatnVarAxis has unexpected offset.");
		static_assert(offsetof(MatnVarAxis, max_value) == 12, "Type MatnVarAxis has unexpected offset.");
		static_assert(sizeof(MatnGPU_Blob) == 32, "Type MatnGPU_Blob has unexpected size.");
		static_assert(alignof(MatnGPU_Blob) == 8, "Type MatnGPU_Blob has unexpected alignment.");
		static_assert(offsetof(MatnGPU_Blob, data) == 0, "Type MatnGPU_Blob has unexpected offset.");
		static_assert(offsetof(MatnGPU_Blob, length) == 8, "Type MatnGPU_Blob has unexpected offset.");
		static_assert(offsetof(MatnGPU_Blob, min_x) == 12, "Type MatnGPU_Blob has unexpected offset.");
		static_assert(offsetof(MatnGPU_Blob, min_y) == 16, "Type MatnGPU_Blob has unexpected offset.");
		static_assert(offsetof(MatnGPU_Blob, max_x) == 20, "Type MatnGPU_Blob has unexpected offset.");
		static_assert(offsetof(MatnGPU_Blob, max_y) == 24, "Type MatnGPU_Blob has unexpected offset.");
		#endif // !defined(_WIN32) && ARCH_BITS == 64
		

		#if defined(__i386__) && defined(__ANDROID__)
		static_assert(sizeof(const uint16_t) == 2, "Type const uint16_t has unexpected size.");
		static_assert(alignof(const uint16_t) == 2, "Type const uint16_t has unexpected alignment.");
		static_assert(sizeof(const uint8_t) == 1, "Type const uint8_t has unexpected size.");
		static_assert(alignof(const uint8_t) == 1, "Type const uint8_t has unexpected alignment.");
		static_assert(sizeof(const char) == 1, "Type const char has unexpected size.");
		static_assert(alignof(const char) == 1, "Type const char has unexpected alignment.");
		static_assert(sizeof(char) == 1, "Type char has unexpected size.");
		static_assert(alignof(char) == 1, "Type char has unexpected alignment.");
		static_assert(sizeof(uint32_t) == 4, "Type uint32_t has unexpected size.");
		static_assert(alignof(uint32_t) == 4, "Type uint32_t has unexpected alignment.");
		static_assert(sizeof(const float) == 4, "Type const float has unexpected size.");
		static_assert(alignof(const float) == 4, "Type const float has unexpected alignment.");
		static_assert(sizeof(const uint32_t) == 4, "Type const uint32_t has unexpected size.");
		static_assert(alignof(const uint32_t) == 4, "Type const uint32_t has unexpected alignment.");
		static_assert(sizeof(float) == 4, "Type float has unexpected size.");
		static_assert(alignof(float) == 4, "Type float has unexpected alignment.");
		static_assert(sizeof(int) == 4, "Type int has unexpected size.");
		static_assert(alignof(int) == 4, "Type int has unexpected alignment.");
		static_assert(sizeof(MatnVarInstance) == 12, "Type MatnVarInstance has unexpected size.");
		static_assert(alignof(MatnVarInstance) == 4, "Type MatnVarInstance has unexpected alignment.");
		static_assert(offsetof(MatnVarInstance, name) == 0, "Type MatnVarInstance has unexpected offset.");
		static_assert(offsetof(MatnVarInstance, coords) == 4, "Type MatnVarInstance has unexpected offset.");
		static_assert(offsetof(MatnVarInstance, coord_count) == 8, "Type MatnVarInstance has unexpected offset.");
		static_assert(sizeof(MatnBufferView) == 28, "Type MatnBufferView has unexpected size.");
		static_assert(alignof(MatnBufferView) == 4, "Type MatnBufferView has unexpected alignment.");
		static_assert(offsetof(MatnBufferView, glyph_ids) == 0, "Type MatnBufferView has unexpected offset.");
		static_assert(offsetof(MatnBufferView, x_advances) == 4, "Type MatnBufferView has unexpected offset.");
		static_assert(offsetof(MatnBufferView, y_advances) == 8, "Type MatnBufferView has unexpected offset.");
		static_assert(offsetof(MatnBufferView, x_offsets) == 12, "Type MatnBufferView has unexpected offset.");
		static_assert(offsetof(MatnBufferView, y_offsets) == 16, "Type MatnBufferView has unexpected offset.");
		static_assert(offsetof(MatnBufferView, clusters) == 20, "Type MatnBufferView has unexpected offset.");
		static_assert(offsetof(MatnBufferView, length) == 24, "Type MatnBufferView has unexpected offset.");
		static_assert(sizeof(MatnGlyphMetrics) == 16, "Type MatnGlyphMetrics has unexpected size.");
		static_assert(alignof(MatnGlyphMetrics) == 4, "Type MatnGlyphMetrics has unexpected alignment.");
		static_assert(offsetof(MatnGlyphMetrics, bearing_x) == 0, "Type MatnGlyphMetrics has unexpected offset.");
		static_assert(offsetof(MatnGlyphMetrics, bearing_y) == 4, "Type MatnGlyphMetrics has unexpected offset.");
		static_assert(offsetof(MatnGlyphMetrics, width) == 8, "Type MatnGlyphMetrics has unexpected offset.");
		static_assert(offsetof(MatnGlyphMetrics, height) == 12, "Type MatnGlyphMetrics has unexpected offset.");
		static_assert(sizeof(MatnVarAxis) == 16, "Type MatnVarAxis has unexpected size.");
		static_assert(alignof(MatnVarAxis) == 4, "Type MatnVarAxis has unexpected alignment.");
		static_assert(offsetof(MatnVarAxis, tag) == 0, "Type MatnVarAxis has unexpected offset.");
		static_assert(offsetof(MatnVarAxis, min_value) == 4, "Type MatnVarAxis has unexpected offset.");
		static_assert(offsetof(MatnVarAxis, default_value) == 8, "Type MatnVarAxis has unexpected offset.");
		static_assert(offsetof(MatnVarAxis, max_value) == 12, "Type MatnVarAxis has unexpected offset.");
		static_assert(sizeof(MatnGPU_Blob) == 24, "Type MatnGPU_Blob has unexpected size.");
		static_assert(alignof(MatnGPU_Blob) == 4, "Type MatnGPU_Blob has unexpected alignment.");
		static_assert(offsetof(MatnGPU_Blob, data) == 0, "Type MatnGPU_Blob has unexpected offset.");
		static_assert(offsetof(MatnGPU_Blob, length) == 4, "Type MatnGPU_Blob has unexpected offset.");
		static_assert(offsetof(MatnGPU_Blob, min_x) == 8, "Type MatnGPU_Blob has unexpected offset.");
		static_assert(offsetof(MatnGPU_Blob, min_y) == 12, "Type MatnGPU_Blob has unexpected offset.");
		static_assert(offsetof(MatnGPU_Blob, max_x) == 16, "Type MatnGPU_Blob has unexpected offset.");
		static_assert(offsetof(MatnGPU_Blob, max_y) == 20, "Type MatnGPU_Blob has unexpected offset.");
		#endif // defined(__i386__) && defined(__ANDROID__)
		

		static_assert(IS_UNSIGNED_TYPE(const uint16_t), "Type const uint16_t is expected unsigned.");
		static_assert(IS_UNSIGNED_TYPE(const uint8_t), "Type const uint8_t is expected unsigned.");
		static_assert(IS_UNSIGNED_TYPE(uint32_t), "Type uint32_t is expected unsigned.");
		static_assert(IS_SIGNED_TYPE(const float), "Type const float is expected signed.");
		static_assert(IS_UNSIGNED_TYPE(const uint32_t), "Type const uint32_t is expected unsigned.");
		static_assert(IS_SIGNED_TYPE(float), "Type float is expected signed.");
		static_assert(IS_SIGNED_TYPE(int), "Type int is expected signed.");
*/
    public static void init() {
    }

    private final static HashMap<Integer, CTypeInfo> ffiIdMap = new HashMap<>();

    public static CTypeInfo getCTypeInfo(int id) {
        return ffiIdMap.get(id);
    }

    /*JNI
static native_type* getNativeType(int id) {
native_type* nativeType = (native_type*)malloc(sizeof(native_type));
switch(id) {
	case -2:
		nativeType->type = VOID_TYPE;
		return nativeType;
	case -1:
		nativeType->type = POINTER_TYPE;
		return nativeType;
	case 0:
		GET_NATIVE_TYPE(char, nativeType);
		return nativeType;
	case 1:
		GET_NATIVE_TYPE(const char, nativeType);
		return nativeType;
	case 2:
		GET_NATIVE_TYPE(const float, nativeType);
		return nativeType;
	case 3:
		GET_NATIVE_TYPE(const uint16_t, nativeType);
		return nativeType;
	case 4:
		GET_NATIVE_TYPE(const uint32_t, nativeType);
		return nativeType;
	case 5:
		GET_NATIVE_TYPE(const uint8_t, nativeType);
		return nativeType;
	case 6:
		GET_NATIVE_TYPE(float, nativeType);
		return nativeType;
	case 7:
		GET_NATIVE_TYPE(int, nativeType);
		return nativeType;
	case 8:
		GET_NATIVE_TYPE(uint32_t, nativeType);
		return nativeType;
	case 10:
		nativeType->type = STRUCT_TYPE;
		nativeType->field_count = 7;
		nativeType->fields = (native_type**)malloc(sizeof(native_type*) * 7);
		nativeType->fields[0] = getNativeType(-1);
		nativeType->fields[1] = getNativeType(-1);
		nativeType->fields[2] = getNativeType(-1);
		nativeType->fields[3] = getNativeType(-1);
		nativeType->fields[4] = getNativeType(-1);
		nativeType->fields[5] = getNativeType(-1);
		nativeType->fields[6] = getNativeType(8);
		return nativeType;
	case 12:
		nativeType->type = STRUCT_TYPE;
		nativeType->field_count = 6;
		nativeType->fields = (native_type**)malloc(sizeof(native_type*) * 6);
		nativeType->fields[0] = getNativeType(-1);
		nativeType->fields[1] = getNativeType(8);
		nativeType->fields[2] = getNativeType(7);
		nativeType->fields[3] = getNativeType(7);
		nativeType->fields[4] = getNativeType(7);
		nativeType->fields[5] = getNativeType(7);
		return nativeType;
	case 13:
		nativeType->type = STRUCT_TYPE;
		nativeType->field_count = 4;
		nativeType->fields = (native_type**)malloc(sizeof(native_type*) * 4);
		nativeType->fields[0] = getNativeType(6);
		nativeType->fields[1] = getNativeType(6);
		nativeType->fields[2] = getNativeType(6);
		nativeType->fields[3] = getNativeType(6);
		return nativeType;
	case 15:
		nativeType->type = STRUCT_TYPE;
		nativeType->field_count = 7;
		nativeType->fields = (native_type**)malloc(sizeof(native_type*) * 7);
		nativeType->fields[0] = getNativeType(0);
		nativeType->fields[1] = getNativeType(0);
		nativeType->fields[2] = getNativeType(0);
		nativeType->fields[3] = getNativeType(0);
		nativeType->fields[4] = getNativeType(6);
		nativeType->fields[5] = getNativeType(6);
		nativeType->fields[6] = getNativeType(6);
		return nativeType;
	case 16:
		nativeType->type = STRUCT_TYPE;
		nativeType->field_count = 3;
		nativeType->fields = (native_type**)malloc(sizeof(native_type*) * 3);
		nativeType->fields[0] = getNativeType(-1);
		nativeType->fields[1] = getNativeType(-1);
		nativeType->fields[2] = getNativeType(7);
		return nativeType;
	default:
		free(nativeType);
		return NULL;
	}
}
*/
    private native static long getNativeType(int id);/*
    	return reinterpret_cast<jlong>(getNativeType(id));
    */

    private native static void freeNativeType(long nativeType);/*
    	free_native_type((native_type*)nativeType);
    */

    private static void registerCTypeInfo(int id) {
        long nativeType = getNativeType(id);
        ffiIdMap.put(id, CHandler.constructCTypeFromNativeType(nativeType));
        freeNativeType(nativeType);
    }

    static {
        registerCTypeInfo(-2);
        registerCTypeInfo(-1);
        registerCTypeInfo(0);
        registerCTypeInfo(1);
        registerCTypeInfo(2);
        registerCTypeInfo(3);
        registerCTypeInfo(4);
        registerCTypeInfo(5);
        registerCTypeInfo(6);
        registerCTypeInfo(7);
        registerCTypeInfo(8);
        registerCTypeInfo(10);
        registerCTypeInfo(12);
        registerCTypeInfo(13);
        registerCTypeInfo(15);
        registerCTypeInfo(16);
    }
}
