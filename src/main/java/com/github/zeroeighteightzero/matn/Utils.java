package com.github.zeroeighteightzero.matn;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.NumberUtils;

/**
 * Static helpers for hashing glyphs and deriving glyph rasterization sizes.
 */
public class Utils {

    /**
     * Computes a hash identifying a glyph for a given font and its current variation,
     * synthetic-bold, and synthetic-slant settings.
     *
     * @param font the font to hash for
     * @param glyphID the font-specific glyph identifier
     * @return a hash combining the font state and glyph identifier
     */
    public static long glyphHash(Font font, long glyphID) {
        long h = 1L;
        float[] vars = font.getVarCoords();
        if (vars != null) {
            for (float v : vars) {
                h = 31L * h + NumberUtils.floatToIntBits(v);
            }
        }
        h = 31L * h + NumberUtils.floatToIntBits(font.boldX);
        h = 31L * h + NumberUtils.floatToIntBits(font.boldY);
        h = 31L * h + NumberUtils.floatToIntBits(font.slant);
        h = 31L * h + NumberUtils.floatToIntBits(font.outlineWidth);
        h = 31L * h + (font.outlineColor.r != +0.0f ? NumberUtils.floatToIntBits(font.outlineColor.r) : 0);
        h = 31L * h + (font.outlineColor.g != +0.0f ? NumberUtils.floatToIntBits(font.outlineColor.g) : 0);
        h = 31L * h + (font.outlineColor.b != +0.0f ? NumberUtils.floatToIntBits(font.outlineColor.b) : 0);
        h = 31L * h + (font.outlineColor.a != +0.0f ? NumberUtils.floatToIntBits(font.outlineColor.a) : 0);
        h = 31L * h + font.mtFont.getPointer();
        h = 31L * h + glyphID;
        h = 31L * h + (font.boldInPlace ? 1231L : 1237L);
        return h;
    }

    /**
     * Computes a hash identifying a glyph for a given font state and rasterization size.
     *
     * @param font the font to hash for
     * @param glyphID the font-specific glyph identifier
     * @param size the (stepped) rasterization size
     * @return a hash combining the font state, glyph identifier, and size
     */
    public static long glyphHashWithSize(Font font, long glyphID, int size) {
        long h = glyphHash(font, glyphID);
        h = 31L * h + size;
        return h;
    }

    /**
     * Steps a requested font size to the discrete rasterization size used by the atlas.
     *
     * <p>Sizes below 12 are returned unchanged, sizes below 120 are stepped to a multiple
     * of 6, and larger sizes are stepped to a multiple of 12.</p>
     *
     * @param fontSize the requested font size
     * @return the stepped rasterization size
     */
    public static int getFontSize(int fontSize) {
        if (fontSize < 12) {
            return fontSize;
        } else if (fontSize < 120) {
            return 6 * MathUtils.ceil(fontSize / 6f);
        }
        return 12 * MathUtils.ceil(fontSize / 12f);
    }

}
