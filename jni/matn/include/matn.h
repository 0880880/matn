#ifndef MATN_H
#define MATN_H

#include <stddef.h>
#include <stdint.h>

#ifdef __cplusplus
extern "C" {
#endif

typedef enum {
  MATN_SUCCESS = 0,
  MATN_ERR_INVALID_ARGUMENT = -1,
  MATN_ERR_OUT_OF_MEMORY = -2,
  MATN_ERR_FILE_NOT_FOUND = -3,
  MATN_ERR_FONT_LOAD_FAILED = -4,
  MATN_ERR_SHAPING_FAILED = -5,
  MATN_ERR_RASTERIZATION_FAILED = -6,
  MATN_ERR_DATA_NOT_FOUND = -7
} MatnResult;

typedef enum {
  MATN_DIRECTION_INVALID = 0,
  MATN_DIRECTION_LTR = 4,
  MATN_DIRECTION_RTL = 5,
  MATN_DIRECTION_TTB = 6,
  MATN_DIRECTION_BTT = 7
} MatnDirection;

typedef enum { MATN_PIXEL_FORMAT_A8, MATN_PIXEL_FORMAT_BGRA32 } MatnPixelFormat;

typedef enum {
  MATN_GPU_LANGUAGE_GLSL,
  MATN_GPU_LANGUAGE_WGSL,
  MATN_GPU_LANGUAGE_HLSL,
  MATN_GPU_LANGUAGE_MSL
} MatnGPU_LANGUAGE;

typedef struct MatnTypeface MatnTypeface;
typedef struct MatnFont MatnFont;
typedef struct MatnBlob MatnBlob;
typedef struct MatnTextRuns MatnTextRuns;

typedef struct {
  char tag[4];
  float min_value;
  float default_value;
  float max_value;
} MatnVarAxis;

typedef struct {
  const char *name; // UTF-8 name (or NULL if unnamed/failed extraction)
  const float *coords;
  int coord_count;
} MatnVarInstance;

typedef struct {
  float bearing_x;
  float bearing_y;
  float width;
  float height;
} MatnGlyphMetrics;

typedef struct {
  uint32_t *glyph_ids;
  float *x_advances;
  float *y_advances;
  float *x_offsets;
  float *y_offsets;
  uint32_t *clusters;
  uint32_t length;
} MatnBufferView;

typedef struct {
  const unsigned char *data;
  int width;
  int height;
  int stride;
  int left;
  int top;
  MatnPixelFormat format;
} MatnBlobView;

typedef struct MatnGPU_Blob {
  const char *data;
  uint32_t length;
  int min_x;
  int min_y;
  int max_x;
  int max_y;
} MatnGPU_Blob;

MatnResult matn_typeface_from_file(const char *path, int index,
                                MatnTypeface **out_typeface);

MatnResult matn_typeface_from_memory(const char *data, uint32_t size,
                                  int index,
                                  MatnTypeface **out_typeface);

void matn_typeface_destroy(MatnTypeface *face);

int matn_typeface_has_color(MatnTypeface *face);

int matn_typeface_is_scalable(MatnTypeface *face);

int matn_typeface_has_variations(MatnTypeface *face);

uint32_t matn_typeface_get_upem(MatnTypeface *face);

int matn_typeface_get_var_axis_count(const MatnTypeface *face);
int matn_typeface_get_var_axes(const MatnTypeface *face, MatnVarAxis *out_axes,
                             int max_axes);

int matn_typeface_get_named_instance_count(const MatnTypeface *face);
MatnResult matn_typeface_get_named_instance(const MatnTypeface *face, int index,
                                         MatnVarInstance *out_instance);

MatnResult matn_typeface_create_font(MatnTypeface *face, MatnFont **out_font);

void matn_font_destroy(MatnFont *font);

MatnResult matn_font_set_var_coords(MatnFont *font, const float *coords,
                                 uint32_t coords_count);

void matn_font_get_synthetic_bold(const MatnFont *font, float *x, float *y, int *in_place);

void matn_font_set_synthetic_bold(const MatnFont *font, float x, float y, int in_place);

float matn_font_get_synthetic_slant(const MatnFont *font);

void matn_font_set_synthetic_slant(const MatnFont *font, float slant);

float matn_font_get_ascender(const MatnFont *font);
float matn_font_get_descender(const MatnFont *font);
float matn_font_get_line_gap(const MatnFont *font);
uint32_t matn_font_get_size_px(const MatnFont *font);

uint32_t matn_font_get_glyph_id(const MatnFont *font, uint32_t codepoint);

MatnResult matn_font_get_glyph_metrics(MatnFont *font, uint32_t glyph_id, MatnGlyphMetrics *out_metrics);

MatnResult matn_shape_set_utf8(MatnFont *font, const char *utf8_text, int text_length, int offset, int length);

MatnResult matn_shape_set_utf16(MatnFont *font, const uint16_t *utf16_text, int text_length, int offset, int length);

MatnResult matn_shape_set_utf32(MatnFont *font, const uint32_t *utf32_text, int text_length, int offset, int length);

int matn_shape_is_rtl(MatnFont *font);

MatnResult matn_shape(MatnFont *font);

const MatnBufferView *matn_shape_view_buffer(const MatnFont *font);

MatnResult matn_gpu_draw_glyph(MatnFont *font, uint32_t glyph_id, MatnGPU_Blob **out_blob);

const char *matn_gpu_get_vertex(MatnGPU_LANGUAGE language);
const char *matn_gpu_get_fragment(MatnGPU_LANGUAGE language);

MatnResult matn_rasterize_glyph(MatnFont *font, uint32_t glyph_id,
                             uint32_t size_px, MatnBlob **out_blob);

void matn_blob_destroy(MatnBlob *blob);

/* Read-Only */
const uint8_t *matn_blob_get_data(const MatnBlob *blob);
int matn_blob_get_width(const MatnBlob *blob);
int matn_blob_get_height(const MatnBlob *blob);
int matn_blob_get_stride(const MatnBlob *blob);
int matn_blob_get_left(
    const MatnBlob *blob);
int matn_blob_get_top(const MatnBlob *blob);
MatnPixelFormat matn_blob_get_format(const MatnBlob *blob);

void matn_cleanup(void);

#ifdef __cplusplus
}
#endif

#endif // MATN_H
