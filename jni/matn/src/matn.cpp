#include "matn.h"

#include <algorithm>
#include <cassert>
#include <cstdint>
#include <cstring>
#include <mutex>
#include <memory>
#include <string>
#include <vector>

#include <hb-ot.h>
#include <hb-gpu.h>
#include <hb-raster.h>
#include <hb.h>

#include <SheenBidi/SheenBidi.h>

static hb_gpu_draw_t *g_hb_gpu_draw;
static hb_raster_draw_t *g_hb_raster_draw;
static hb_raster_paint_t *g_hb_raster_paint;
static std::once_flag g_hb_init_flag;

static void init_hb() {
  g_hb_gpu_draw = hb_gpu_draw_create_or_fail();
  g_hb_raster_draw = hb_raster_draw_create_or_fail();
  g_hb_raster_paint = hb_raster_paint_create_or_fail();

  hb_raster_draw_set_transform(g_hb_raster_draw, 1.f, 0.f, 0.f, 1.f, 0.f, 0.f);
  hb_raster_paint_set_transform(g_hb_raster_paint, 1.f, 0.f, 0.f, 1.f, 0.f, 0.f);
}

static hb_gpu_draw_t *get_hb_gpu_draw() {
  std::call_once(g_hb_init_flag, init_hb);
  return g_hb_gpu_draw;
}

static hb_raster_draw_t *get_hb_raster_draw() {
  std::call_once(g_hb_init_flag, init_hb);
  return g_hb_raster_draw;
}

static hb_raster_paint_t *get_hb_raster_paint() {
  std::call_once(g_hb_init_flag, init_hb);
  return g_hb_raster_paint;
}

struct MatnTypeface {
  hb_blob_t *hb_blob;
  hb_face_t *hb_face;

  std::vector<MatnVarAxis> axes;

  std::vector<MatnVarInstance> instances;
  std::vector<std::vector<float>> instance_coords;
  std::vector<std::string> instance_names;

  int has_color = 0, is_scalable = 0, has_variations = 0;

  uint32_t upem;
  float inv_upem;

  int font_count = 0;

  ~MatnTypeface() {
    assert(font_count == 0);
    if (hb_face) {
      hb_face_destroy(hb_face);
    }
    if (hb_blob) {
      hb_blob_destroy(hb_blob);
    }
  }
};

struct InternalBuffer {
  std::vector<uint32_t> glyph_ids;
  std::vector<float> x_advances;
  std::vector<float> y_advances;
  std::vector<float> x_offsets;
  std::vector<float> y_offsets;
  std::vector<uint32_t> clusters;
};

struct MatnFont {
  MatnTypeface *face;
  hb_font_t *hb_font = nullptr;

  InternalBuffer buffer;
  MatnBufferView buffer_view;
  hb_buffer_t *hb_buf = nullptr;
  hb_blob_t *hb_gpu_blob = nullptr;
  hb_raster_image_t *hb_img = nullptr;

  uint32_t size_px = 0;

  float ascender, descender, line_gap;

  ~MatnFont() {
    if (hb_buf) {
      hb_buffer_destroy(hb_buf);
    }
    if (hb_gpu_blob) {
      hb_blob_destroy(hb_gpu_blob);
    }
    if (hb_img) {
      hb_raster_image_destroy(hb_img);
    }
    if (hb_font) {
      hb_font_destroy(hb_font);
    }
    --face->font_count;
  }
};

struct MatnBlob {
  std::vector<uint8_t> pixels;
  int width = 0;
  int height = 0;
  int stride = 0;
  int left = 0;
  int top = 0;
  MatnPixelFormat format = MATN_PIXEL_FORMAT_A8;
};

struct MatnTextRuns {
  std::vector<uint32_t> offsets;
  std::vector<uint32_t> lengths;
  std::vector<unsigned char> levels;
};

static void populate_face(MatnTypeface *face) {
  face->has_color = hb_ot_color_has_layers(face->hb_face) ||
                    hb_ot_color_has_paint(face->hb_face) ||
                    hb_ot_color_has_png(face->hb_face) ||
                    hb_ot_color_has_svg(face->hb_face);
  face->has_variations = hb_ot_var_has_data(face->hb_face);
  {
    hb_blob_t *blob =
        hb_face_reference_table(face->hb_face, HB_TAG('g', 'l', 'y', 'f'));
    int has = hb_blob_get_length(blob) > 0;
    hb_blob_destroy(blob);
    if (has) {
      face->is_scalable = has;
    } else {
      blob = hb_face_reference_table(face->hb_face, HB_TAG('C', 'F', 'F', ' '));
      has = hb_blob_get_length(blob) > 0;
      hb_blob_destroy(blob);
      face->is_scalable = has;
    }
  }

  face->upem = hb_face_get_upem(face->hb_face);
  face->inv_upem = 1.f / ((float) face->upem);

  uint32_t axis_count = hb_ot_var_get_axis_count(face->hb_face);
  std::vector<hb_ot_var_axis_info_t> axes;
  axes.reserve(axis_count);
  face->axes.resize(axis_count);
  hb_ot_var_get_axis_infos(face->hb_face, 0, &axis_count, axes.data());

  for (uint32_t i = 0; i < axis_count; ++i) {
    face->axes[i].min_value = axes[i].min_value;
    face->axes[i].default_value = axes[i].default_value;
    face->axes[i].max_value = axes[i].max_value;
    face->axes[i].tag[0] = (char)((axes[i].tag >> 24) & 0xFF);
    face->axes[i].tag[1] = (char)((axes[i].tag >> 16) & 0xFF);
    face->axes[i].tag[2] = (char)((axes[i].tag >> 8) & 0xFF);
    face->axes[i].tag[3] = (char)(axes[i].tag & 0xFF);
  }

  uint32_t named_count = hb_ot_var_get_named_instance_count(face->hb_face);
  face->instances.reserve(named_count);
  face->instance_coords.reserve(named_count);
  face->instance_names.reserve(named_count);

  for (uint32_t i = 0; i < named_count; ++i) {
    hb_ot_name_id_t name_id =
        hb_ot_var_named_instance_get_subfamily_name_id(face->hb_face, i);
    char name_buf[64];
    uint32_t name_buf_size = 64;
    hb_ot_name_get_utf8(face->hb_face, name_id, HB_LANGUAGE_INVALID,
                        &name_buf_size, name_buf);
    face->instance_names.emplace_back(name_buf);

    uint32_t coords_len = axis_count;
    std::vector<float> coords(axis_count);
    hb_ot_var_named_instance_get_design_coords(face->hb_face, i, &coords_len,
                                               coords.data());
    face->instance_coords.push_back(coords);
  }
}

MatnResult matn_typeface_from_file(const char *path, int index,
                                MatnTypeface **out_typeface) {
  if (!path || !out_typeface) {
    return MATN_ERR_INVALID_ARGUMENT;
  }

  auto face = std::make_unique<MatnTypeface>();

  face->hb_blob = hb_blob_create_from_file_or_fail(path);

  if (!face->hb_blob) {
    return MATN_ERR_FILE_NOT_FOUND;
  }

  face->hb_face = hb_face_create_or_fail(face->hb_blob, index);

  if (!face->hb_face) {
    return MATN_ERR_FONT_LOAD_FAILED;
  }

  populate_face(face.get());

  *out_typeface = face.release();
  return MATN_SUCCESS;
}

MatnResult matn_typeface_from_memory(const char *data, uint32_t size,
                                  int index, MatnTypeface **out_typeface) {
  if (!data || size == 0 || !out_typeface) {
    return MATN_ERR_INVALID_ARGUMENT;
  }

  auto face = std::make_unique<MatnTypeface>();

  face->hb_blob = hb_blob_create_or_fail(data, size, HB_MEMORY_MODE_DUPLICATE,
                                         nullptr, nullptr);

  if (!face->hb_blob) {
    return MATN_ERR_OUT_OF_MEMORY;
  }

  face->hb_face = hb_face_create_or_fail(face->hb_blob, index);

  if (!face->hb_face) {
    return MATN_ERR_FONT_LOAD_FAILED;
  }

  populate_face(face.get());

  *out_typeface = face.release();
  return MATN_SUCCESS;
}

void matn_typeface_destroy(MatnTypeface *face) { delete face; }

int matn_typeface_has_color(MatnTypeface *face) { return face->has_color; }

int matn_typeface_is_scalable(MatnTypeface *face) { return face->is_scalable; }

int matn_typeface_has_variations(MatnTypeface *face) {
  return face->has_variations;
}

uint32_t matn_typeface_get_upem(MatnTypeface *face) {
  return face->upem;
}

int matn_typeface_get_var_axis_count(const MatnTypeface *face) {
  return face ? face->axes.size() : 0;
}

int matn_typeface_get_var_axes(const MatnTypeface *face, MatnVarAxis *out_axes,
                             int max_axes) {
  if (!face || !out_axes || max_axes <= 0) {
    return 0;
  }
  int copy_slots = std::min(max_axes, static_cast<int>(face->axes.size()));
  std::memcpy(out_axes, face->axes.data(), copy_slots * sizeof(MatnVarAxis));
  return copy_slots;
}

int matn_typeface_get_named_instance_count(const MatnTypeface *face) {
  return face ? face->instances.size() : 0;
}

MatnResult matn_typeface_get_named_instance(const MatnTypeface *face, int index,
                                         MatnVarInstance *out_instance) {
  if (!face || !out_instance || index < 0 || static_cast<uint32_t>(index) >= face->instances.size()) {
    return MATN_ERR_INVALID_ARGUMENT;
  }
  *out_instance = face->instances[index];
  return MATN_SUCCESS;
}

void update_font_size(MatnFont *font, uint32_t size_px) {
  if (font->face->is_scalable && size_px != font->size_px) {
    font->size_px = size_px;
    hb_font_set_ppem(font->hb_font, font->size_px, font->size_px);
  }
}

MatnResult matn_typeface_create_font(MatnTypeface *face, MatnFont **out_font) {
  if (!face || !out_font) {
    return MATN_ERR_INVALID_ARGUMENT;
  }

  auto font = std::make_unique<MatnFont>();
  font->face = face;
  font->hb_font = hb_font_create(face->hb_face);
  font->hb_buf = hb_buffer_create();

  {
    hb_font_extents_t font_extents;
    if (hb_font_get_h_extents(font->hb_font, &font_extents)) {
      font->ascender = font_extents.ascender * face->inv_upem;
      font->descender = font_extents.descender * face->inv_upem;
      font->line_gap = font_extents.line_gap * face->inv_upem;
    }
  }

  uint32_t x_ppem = 0, y_ppem = 0;
  hb_font_get_ppem(font->hb_font, &x_ppem, &y_ppem);
  font->size_px = y_ppem;

  ++face->font_count;

  update_font_size(font.get(), 16);

  *out_font = font.release();
  return MATN_SUCCESS;
}

void matn_font_destroy(MatnFont *font) { delete font; }

float matn_font_get_ascender(const MatnFont *font) {
  return font ? font->ascender : 0;
}
float matn_font_get_descender(const MatnFont *font) {
  return font ? font->descender : 0;
}
float matn_font_get_line_gap(const MatnFont *font) {
  return font ? font->line_gap : 0;
}
uint32_t matn_font_get_size_px(const MatnFont *font) {
  return font ? font->size_px : 0;
}

MatnResult matn_font_set_var_coords(MatnFont *font, const float *coords,
                                 uint32_t coords_count) {
  if (!font || !coords || !font->face->has_variations) {
    return MATN_ERR_INVALID_ARGUMENT;
  }

  hb_font_set_var_coords_design (font->hb_font, coords, coords_count);

  return MATN_SUCCESS;
}

void matn_font_get_synthetic_bold(const MatnFont *font, float *x, float *y, int *in_place) {
  if (font) {
    hb_font_get_synthetic_bold(font->hb_font, x, y, in_place);
  }
}

void matn_font_set_synthetic_bold(const MatnFont *font, float x, float y, int in_place) {
  if (font) {
    hb_font_set_synthetic_bold(font->hb_font, x, y, in_place);
  }
}

float matn_font_get_synthetic_slant(const MatnFont *font) {
  return font ? hb_font_get_synthetic_slant(font->hb_font) : 0;
}

void matn_font_set_synthetic_slant(const MatnFont *font, float slant) {
  if (font) {
    hb_font_set_synthetic_slant(font->hb_font, slant);
  }
}

uint32_t matn_font_get_glyph_id(const MatnFont *font, uint32_t codepoint) {
  if (!font) {
    return 0;
  }
  uint32_t gid;
  hb_font_get_nominal_glyph(font->hb_font, codepoint, &gid);
  return gid;
}

MatnResult matn_font_get_glyph_metrics(MatnFont *font, uint32_t glyph_id, MatnGlyphMetrics *out_metrics) {
  if (!font || !out_metrics) {
    return MATN_ERR_INVALID_ARGUMENT;
  }

  hb_glyph_extents_t extents;

  if (hb_font_get_glyph_extents(font->hb_font, glyph_id, &extents)) {
    out_metrics->bearing_x = extents.x_bearing * font->face->inv_upem;
    out_metrics->bearing_y = extents.y_bearing * font->face->inv_upem;
    out_metrics->width = extents.width * font->face->inv_upem;
    out_metrics->height = extents.height * font->face->inv_upem;
  } else {
    return MATN_ERR_DATA_NOT_FOUND;
  }

  return MATN_SUCCESS;
}

MatnResult matn_shape_text(MatnFont *font, const char *utf8_text, int text_length,
                        const char *language, const char *script,
                        MatnDirection direction) {
  if (!font || !utf8_text) {
    return MATN_ERR_INVALID_ARGUMENT;
  }

  hb_buffer_reset(font->hb_buf);
  hb_buffer_add_utf8(font->hb_buf, utf8_text, text_length, 0, -1);

  if ((!language && !script) || direction == MATN_DIRECTION_INVALID) {
    hb_buffer_guess_segment_properties(font->hb_buf);
  } else {
    if (language) {
      hb_buffer_set_language(font->hb_buf,
                             hb_language_from_string(language, -1));
    }
    if (script) {
      hb_buffer_set_script(font->hb_buf, hb_script_from_string(script, -1));
    }
    switch (direction) {
    case MATN_DIRECTION_LTR:
      hb_buffer_set_direction(font->hb_buf, HB_DIRECTION_LTR);
      break;
    case MATN_DIRECTION_RTL:
      hb_buffer_set_direction(font->hb_buf, HB_DIRECTION_RTL);
      break;
    case MATN_DIRECTION_TTB:
      hb_buffer_set_direction(font->hb_buf, HB_DIRECTION_TTB);
      break;
    case MATN_DIRECTION_BTT:
      hb_buffer_set_direction(font->hb_buf, HB_DIRECTION_BTT);
      break;
    default:
      break;
    }
  }

  hb_shape(font->hb_font, font->hb_buf, nullptr, 0);

  uint32_t glyph_count;
  hb_glyph_info_t *glyph_info =
      hb_buffer_get_glyph_infos(font->hb_buf, &glyph_count);
  hb_glyph_position_t *glyph_pos =
      hb_buffer_get_glyph_positions(font->hb_buf, &glyph_count);

  if (glyph_count == 0) {
    hb_buffer_destroy(font->hb_buf);
    return MATN_ERR_SHAPING_FAILED;
  }

  if (glyph_count > font->buffer.glyph_ids.size()) {
    font->buffer.glyph_ids.resize(glyph_count);
    font->buffer.x_advances.resize(glyph_count);
    font->buffer.y_advances.resize(glyph_count);
    font->buffer.x_offsets.resize(glyph_count);
    font->buffer.y_offsets.resize(glyph_count);
    font->buffer.clusters.resize(glyph_count);

    font->buffer_view.glyph_ids = font->buffer.glyph_ids.data();
    font->buffer_view.x_advances = font->buffer.x_advances.data();
    font->buffer_view.y_advances = font->buffer.y_advances.data();
    font->buffer_view.x_offsets = font->buffer.x_offsets.data();
    font->buffer_view.y_offsets = font->buffer.y_offsets.data();
    font->buffer_view.clusters = font->buffer.clusters.data();
  }
  font->buffer_view.length = glyph_count;

  for (uint32_t i = 0; i < glyph_count; i++) {
    font->buffer.glyph_ids[i] = glyph_info[i].codepoint;
    font->buffer.x_advances[i] = glyph_pos[i].x_advance * font->face->inv_upem;
    font->buffer.y_advances[i] = glyph_pos[i].y_advance * font->face->inv_upem;
    font->buffer.x_offsets[i] = glyph_pos[i].x_offset * font->face->inv_upem;
    font->buffer.y_offsets[i] = glyph_pos[i].y_offset * font->face->inv_upem;
    font->buffer.clusters[i] = glyph_info[i].cluster;
  }

  return MATN_SUCCESS;
}

MatnResult matn_shape_set_utf8(MatnFont *font, const char *utf8_text, int text_length, int offset, int length) {
  if (!font || !utf8_text || text_length < 0 || offset < 0 || length < 0 || offset > text_length || offset + length > text_length) {
    return MATN_ERR_INVALID_ARGUMENT;
  }

  hb_buffer_clear_contents(font->hb_buf);
  hb_buffer_add_utf8(font->hb_buf, utf8_text, text_length, offset, length);
  hb_buffer_guess_segment_properties(font->hb_buf);

  return MATN_SUCCESS;
}

MatnResult matn_shape_set_utf16(MatnFont *font, const uint16_t *utf16_text, int text_length, int offset, int length) {
  if (!font || !utf16_text || text_length < 0 || offset < 0 || length < 0 || offset > text_length || offset + length > text_length) {
    return MATN_ERR_INVALID_ARGUMENT;
  }

  hb_buffer_clear_contents(font->hb_buf);
  hb_buffer_add_utf16(font->hb_buf, utf16_text, text_length, offset, length);
  hb_buffer_guess_segment_properties(font->hb_buf);

  return MATN_SUCCESS;
}

MatnResult matn_shape_set_utf32(MatnFont *font, const uint32_t *utf32_text, int text_length, int offset, int length) {
  if (!font || !utf32_text || text_length < 0 || offset < 0 || length < 0 || offset > text_length || offset + length > text_length) {
    return MATN_ERR_INVALID_ARGUMENT;
  }

  hb_buffer_clear_contents(font->hb_buf);
  hb_buffer_add_utf32(font->hb_buf, utf32_text, text_length, offset, length);
  hb_buffer_guess_segment_properties(font->hb_buf);

  return MATN_SUCCESS;
}

int matn_shape_is_rtl(MatnFont *font) {
  if (!font) {
    return MATN_ERR_INVALID_ARGUMENT;
  }

  return hb_buffer_get_direction(font->hb_buf) == HB_DIRECTION_RTL;
}

MatnResult matn_shape(MatnFont *font) {
  if (!font) {
    return MATN_ERR_INVALID_ARGUMENT;
  }

  hb_shape(font->hb_font, font->hb_buf, nullptr, 0);

  uint32_t glyph_count;
  hb_glyph_info_t *glyph_info =
      hb_buffer_get_glyph_infos(font->hb_buf, &glyph_count);
  hb_glyph_position_t *glyph_pos =
      hb_buffer_get_glyph_positions(font->hb_buf, &glyph_count);

  if (glyph_count == 0) {
    hb_buffer_destroy(font->hb_buf);
    return MATN_ERR_SHAPING_FAILED;
  }

  if (glyph_count > font->buffer.glyph_ids.size()) {
    font->buffer.glyph_ids.resize(glyph_count);
    font->buffer.x_advances.resize(glyph_count);
    font->buffer.y_advances.resize(glyph_count);
    font->buffer.x_offsets.resize(glyph_count);
    font->buffer.y_offsets.resize(glyph_count);
    font->buffer.clusters.resize(glyph_count);

    font->buffer_view.glyph_ids = font->buffer.glyph_ids.data();
    font->buffer_view.x_advances = font->buffer.x_advances.data();
    font->buffer_view.y_advances = font->buffer.y_advances.data();
    font->buffer_view.x_offsets = font->buffer.x_offsets.data();
    font->buffer_view.y_offsets = font->buffer.y_offsets.data();
    font->buffer_view.clusters = font->buffer.clusters.data();
  }
  font->buffer_view.length = glyph_count;

  for (uint32_t i = 0; i < glyph_count; i++) {
    font->buffer.glyph_ids[i] = glyph_info[i].codepoint;
    font->buffer.x_advances[i] = glyph_pos[i].x_advance * font->face->inv_upem;
    font->buffer.y_advances[i] = glyph_pos[i].y_advance * font->face->inv_upem;
    font->buffer.x_offsets[i] = glyph_pos[i].x_offset * font->face->inv_upem;
    font->buffer.y_offsets[i] = glyph_pos[i].y_offset * font->face->inv_upem;
    font->buffer.clusters[i] = glyph_info[i].cluster;
  }

  return MATN_SUCCESS;
}

const MatnBufferView *matn_shape_view_buffer(const MatnFont *font) {
  return &font->buffer_view;
}

MatnResult matn_gpu_draw_glyph(MatnFont *font, uint32_t glyph_id, MatnGPU_Blob **out_blob) {
  if (!font || !out_blob) {
    return MATN_ERR_INVALID_ARGUMENT;
  }

  hb_gpu_draw_t *draw = get_hb_gpu_draw();

  if (font->hb_gpu_blob) {
    hb_gpu_draw_recycle_blob(draw, font->hb_gpu_blob);
  }

  hb_glyph_extents_t extents;

  hb_gpu_draw_glyph(draw, font->hb_font, glyph_id);
  font->hb_gpu_blob = hb_gpu_draw_encode(draw, &extents);

  if (!*out_blob) {
    auto blob = std::make_unique<MatnGPU_Blob>();
    blob->length = hb_blob_get_length(font->hb_gpu_blob);
    blob->data = hb_blob_get_data(font->hb_gpu_blob, &blob->length);
    blob->min_x = extents.x_bearing;
    blob->min_y = extents.y_bearing;
    blob->max_x = extents.x_bearing + extents.width;
    blob->max_y = extents.y_bearing + extents.height;

    *out_blob = blob.release();
  } else {
    MatnGPU_Blob *o = *out_blob;
    o->length = hb_blob_get_length(font->hb_gpu_blob);
    o->data = hb_blob_get_data(font->hb_gpu_blob, &o->length);
    o->min_x = extents.x_bearing;
    o->min_y = extents.y_bearing;
    o->max_x = extents.x_bearing + extents.width;
    o->max_y = extents.y_bearing + extents.height;
  }
  return MATN_SUCCESS;
}

const char *matn_gpu_get_vertex(MatnGPU_LANGUAGE language) {
  switch (language) {
  case MATN_GPU_LANGUAGE_GLSL: {
    static const std::string glsl_vertex_shader =
        std::string(hb_gpu_shader_source(HB_GPU_SHADER_STAGE_VERTEX,
                                         HB_GPU_SHADER_LANG_GLSL)) +
        hb_gpu_draw_shader_source(HB_GPU_SHADER_STAGE_VERTEX,
                                  HB_GPU_SHADER_LANG_GLSL);

    return glsl_vertex_shader.c_str();
  }

  case MATN_GPU_LANGUAGE_WGSL: {
    static const std::string wgsl_vertex_shader =
        std::string(hb_gpu_shader_source(HB_GPU_SHADER_STAGE_VERTEX,
                                         HB_GPU_SHADER_LANG_WGSL)) +
        hb_gpu_draw_shader_source(HB_GPU_SHADER_STAGE_VERTEX,
                                  HB_GPU_SHADER_LANG_WGSL);

    return wgsl_vertex_shader.c_str();
  }

  case MATN_GPU_LANGUAGE_HLSL: {
    static const std::string hlsl_vertex_shader =
        std::string(hb_gpu_shader_source(HB_GPU_SHADER_STAGE_VERTEX,
                                         HB_GPU_SHADER_LANG_HLSL)) +
        hb_gpu_draw_shader_source(HB_GPU_SHADER_STAGE_VERTEX,
                                  HB_GPU_SHADER_LANG_HLSL);

    return hlsl_vertex_shader.c_str();
  }

  case MATN_GPU_LANGUAGE_MSL: {
    static const std::string msl_vertex_shader =
        std::string(hb_gpu_shader_source(HB_GPU_SHADER_STAGE_VERTEX,
                                         HB_GPU_SHADER_LANG_MSL)) +
        hb_gpu_draw_shader_source(HB_GPU_SHADER_STAGE_VERTEX,
                                  HB_GPU_SHADER_LANG_MSL);

    return msl_vertex_shader.c_str();
  }

  default:
    return nullptr;
  }
}

const char *matn_gpu_get_fragment(MatnGPU_LANGUAGE language) {
  switch (language) {
  case MATN_GPU_LANGUAGE_GLSL: {
    static const std::string glsl_fragment_shader =
        std::string(hb_gpu_shader_source(HB_GPU_SHADER_STAGE_FRAGMENT,
                                         HB_GPU_SHADER_LANG_GLSL)) +
        hb_gpu_draw_shader_source(HB_GPU_SHADER_STAGE_FRAGMENT,
                                  HB_GPU_SHADER_LANG_GLSL);

    return glsl_fragment_shader.c_str();
  }

  case MATN_GPU_LANGUAGE_WGSL: {
    static const std::string wgsl_fragment_shader =
        std::string(hb_gpu_shader_source(HB_GPU_SHADER_STAGE_FRAGMENT,
                                         HB_GPU_SHADER_LANG_WGSL)) +
        hb_gpu_draw_shader_source(HB_GPU_SHADER_STAGE_FRAGMENT,
                                  HB_GPU_SHADER_LANG_WGSL);

    return wgsl_fragment_shader.c_str();
  }

  case MATN_GPU_LANGUAGE_HLSL: {
    static const std::string hlsl_fragment_shader =
        std::string(hb_gpu_shader_source(HB_GPU_SHADER_STAGE_FRAGMENT,
                                         HB_GPU_SHADER_LANG_HLSL)) +
        hb_gpu_draw_shader_source(HB_GPU_SHADER_STAGE_FRAGMENT,
                                  HB_GPU_SHADER_LANG_HLSL);

    return hlsl_fragment_shader.c_str();
  }

  case MATN_GPU_LANGUAGE_MSL: {
    static const std::string msl_fragment_shader =
        std::string(hb_gpu_shader_source(HB_GPU_SHADER_STAGE_FRAGMENT,
                                         HB_GPU_SHADER_LANG_MSL)) +
        hb_gpu_draw_shader_source(HB_GPU_SHADER_STAGE_FRAGMENT,
                                  HB_GPU_SHADER_LANG_MSL);

    return msl_fragment_shader.c_str();
  }

  default:
    return nullptr;
  }
}

MatnResult matn_rasterize_glyph(MatnFont *font, uint32_t glyph_id,
                             uint32_t size_px, MatnBlob **out_blob) {
  if (!font || !out_blob) {
    return MATN_ERR_INVALID_ARGUMENT;
  }

  update_font_size(font, size_px);

  hb_glyph_extents_t extents;

  if (!hb_font_get_glyph_extents(font->hb_font, glyph_id, &extents)) {
    return MATN_ERR_RASTERIZATION_FAILED;
  }

  bool is_color = hb_ot_color_glyph_has_paint(font->face->hb_face, glyph_id) ||
                  hb_ot_color_has_layers(font->face->hb_face);

  float scale = font->face->upem / ((float) size_px);

  if (is_color) {
    hb_raster_paint_t *paint = get_hb_raster_paint();
    hb_raster_paint_set_scale_factor(paint, scale, scale);
    if (font->hb_img != nullptr) {
      hb_raster_paint_recycle_image(paint, font->hb_img);
    }
    hb_glyph_extents_t glyph_extents;
    hb_font_get_glyph_extents(font->hb_font, glyph_id, &glyph_extents);
    hb_raster_paint_set_glyph_extents(paint, &glyph_extents);
    hb_raster_paint_glyph(paint, font->hb_font, glyph_id);
    font->hb_img = hb_raster_paint_render(paint);
  } else {
    hb_raster_draw_t *draw = get_hb_raster_draw();
    hb_raster_draw_set_scale_factor(draw, scale, scale);
    if (font->hb_img != nullptr) {
      hb_raster_draw_recycle_image(draw, font->hb_img);
    }

    hb_raster_draw_set_glyph_extents(draw, &extents);
    hb_raster_draw_glyph(draw, font->hb_font, glyph_id);
    font->hb_img = hb_raster_draw_render(draw);
  }

  if (!font->hb_img) {
    return MATN_ERR_RASTERIZATION_FAILED;
  }

  hb_raster_extents_t raster_extents;

  hb_raster_image_get_extents(font->hb_img, &raster_extents);
  auto blob = std::make_unique<MatnBlob>();
  blob->width = raster_extents.width;
  blob->height = raster_extents.height;
  blob->stride = raster_extents.stride;
  blob->top = raster_extents.y_origin;
  blob->left = raster_extents.x_origin;

  size_t length = static_cast<size_t>(std::abs(blob->stride) * blob->height);
  const uint8_t *buf = hb_raster_image_get_buffer(font->hb_img);
  blob->pixels.assign(buf, buf + length);

  if (font->face->has_color) {
    blob->format = MATN_PIXEL_FORMAT_BGRA32;
  } else {
    blob->format = MATN_PIXEL_FORMAT_A8;
  }

  *out_blob = blob.release();
  return MATN_SUCCESS;
}

void matn_blob_destroy(MatnBlob *blob) { delete blob; }

const uint8_t *matn_blob_get_data(const MatnBlob *blob) {
  return blob ? blob->pixels.data() : nullptr;
}
int matn_blob_get_width(const MatnBlob *blob) { return blob ? blob->width : 0; }
int matn_blob_get_height(const MatnBlob *blob) { return blob ? blob->height : 0; }
int matn_blob_get_stride(const MatnBlob *blob) { return blob ? blob->stride : 0; }
int matn_blob_get_left(const MatnBlob *blob) { return blob ? blob->left : 0; }
int matn_blob_get_top(const MatnBlob *blob) { return blob ? blob->top : 0; }
MatnPixelFormat matn_blob_get_format(const MatnBlob *blob) {
  return blob ? blob->format : MATN_PIXEL_FORMAT_A8;
}

MatnResult matn_blob_get_all(const MatnBlob *blob, MatnBlobView *out) {
  if (!blob || !out) {
    return MATN_ERR_INVALID_ARGUMENT;
  }

  out->data = blob->pixels.data();
  out->width = blob->width;
  out->height = blob->height;
  out->stride = blob->stride;
  out->top = blob->top;
  out->left = blob->left;
  out->format = blob->format;

  return MATN_SUCCESS;
}

MatnTextRuns *matn_run_bidi(const char *text, uint32_t length) {
    SBCodepointSequence seq;
    seq.stringEncoding = SBStringEncodingUTF8;
    seq.stringBuffer = static_cast<const void *>(text);
    seq.stringLength = length;

    SBAlgorithmRef alg = SBAlgorithmCreate(&seq);

    SBUInteger p_offset = 0;

    std::vector<uint32_t> offsets;
    std::vector<uint32_t> lengths;
    std::vector<unsigned char> levels;

    while (p_offset < length) {
        SBUInteger p_length;
        SBAlgorithmGetParagraphBoundary(alg,
            p_offset, length,
            &p_length, NULL);

        SBParagraphRef paragraph = SBAlgorithmCreateParagraph(alg, p_offset, p_length, SBLevelDefaultLTR);

        SBLineRef line = SBParagraphCreateLine(paragraph, 0, p_length);

        const SBRun *runs = SBLineGetRunsPtr(line);

        for (uint32_t i = 0; i < SBLineGetRunCount(line); ++i) {
            const SBRun run = runs[i];
            offsets.push_back(run.offset);
            lengths.push_back(run.length);
            levels.push_back(run.level);
        }

        SBLineRelease(line);

        SBParagraphRelease(paragraph);

        p_offset += p_length;
    }

    MatnTextRuns *tr = new MatnTextRuns();
    tr->offsets = offsets;
    tr->lengths = lengths;
    tr->levels = levels;
    return tr;
}

uint32_t matn_bidi_run_count(MatnTextRuns *tr) {
    return tr ? tr->offsets.size() : 0;
}
uint32_t *matn_bidi_get_offsets(MatnTextRuns *tr) {
    return tr ? tr->offsets.data() : nullptr;
}
uint32_t *matn_bidi_get_lengths(MatnTextRuns *tr) {
    return tr ? tr->lengths.data() : nullptr;
}
unsigned char *matn_bidi_get_levels(MatnTextRuns *tr) {
    return tr ? tr->levels.data() : nullptr;
}

void matn_bidi_destroy(MatnTextRuns *tr) {
    delete tr;
}

void matn_cleanup() {
  if (g_hb_gpu_draw) {
    hb_gpu_draw_destroy(g_hb_gpu_draw);
    g_hb_gpu_draw = nullptr;
  }
  if (g_hb_raster_draw) {
    hb_raster_draw_destroy(g_hb_raster_draw);
    g_hb_raster_draw = nullptr;
  }
  if (g_hb_raster_paint) {
    hb_raster_paint_destroy(g_hb_raster_paint);
    g_hb_raster_paint = nullptr;
  }
}