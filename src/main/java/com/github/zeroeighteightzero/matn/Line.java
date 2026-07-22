package com.github.zeroeighteightzero.matn;

import com.badlogic.gdx.utils.LongArray;

public class Line {

    public final LongArray glyphs;
    public final LongArray clusters;
    public float width;

    public Line(int length) {
        glyphs = new LongArray(length);
        clusters = new LongArray(length);
    }

    public boolean notEmpty() {
        return glyphs.notEmpty();
    }

}
