package com.github.zeroeighteightzero.matn;

public class VarAxis {

    public final String tag;
    public final float min, def, max;

    VarAxis(String tag, float min, float def, float max) {
        this.tag = tag;
        this.min = min;
        this.def = def;
        this.max = max;
    }

}
