package com.github.zeroeighteightzero.matn;

import com.badlogic.gdx.math.MathUtils;

public class Utils {

    public static long glyphHash(Font font, long glyphID) {
        long h = 1L;
        float[] vars = font.getVarCoords();
        if (vars != null) {
            for (float v : vars) {
                h = 31L * h + Float.floatToIntBits(v);
            }
        }
        h = 31L * h + Float.floatToIntBits(font.boldX);
        h = 31L * h + Float.floatToIntBits(font.boldY);
        h = 31L * h + Float.floatToIntBits(font.slant);
        h = 31L * h + font.mtFont.getPointer();
        h = 31L * h + glyphID;
        h = 31L * h + (font.boldInPlace ? 1231L : 1237L);
        return h;
    }

    public static long glyphHashWithSize(Font font, long glyphID, int size) {
        long h = glyphHash(font, glyphID);
        h = 31L * h + size;
        return h;
    }

    public static int getFontSize(int fontSize) {
        if (fontSize < 12) {
            return fontSize;
        } else if (fontSize < 120) {
            return 6 * MathUtils.ceil(fontSize / 6f);
        }
        return 12 * MathUtils.ceil(fontSize / 12f);
    }

}
