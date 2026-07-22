package com.github.zeroeighteightzero.matn;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.FloatArray;

public class Layout {

    protected Font.Paragraph paragraph = null;
    protected final Array<Line> lines = new Array<>(true, 8);
    protected Font.ShapeResult shapeResult;
    protected Font font;
    protected float fontSize;
    protected final StringBuilder text = new StringBuilder();
    protected boolean wrap = false;
    protected float maxWidth;

    public float width, height, lineHeight;

    private boolean textDirty = false;
    private boolean fontDirty = false;
    private boolean wrapDirty = false;

    public final FloatArray advances = new FloatArray(true, 8);
    public final FloatArray offsets = new FloatArray(true, 16);
    public final FloatArray sizing = new FloatArray(true, 16);
    public final FloatArray rotation = new FloatArray(true, 8);

    public Layout(Font font, float fontSize) {
        this.font = font;
        this.fontSize = fontSize;
    }

    public Layout(String text, Font font, float fontSize) {
        this.font = font;
        this.fontSize = fontSize;
        addText(text);
    }

    private void shape() {
        shapeResult = font.shape(paragraph);
    }

    // this is basic
    private int findBreakBefore(int i) {
        for (int j = i - 1; j >= 0; --j) {
            char ch = text.charAt(j);
            if (ch == ' ' || ch == '\t' || Character.isWhitespace(ch)) {
                return j;
            }
        }
        return i - 1;
    }

    private void wrapLines() {
        advances.clear();
        offsets.clear();
        sizing.clear();
        rotation.clear();
        width = 0;
        height = 0;
        Line currentLine = new Line(shapeResult.advances.length);
        float penX = 0;
        lineHeight = font.getLineHeight(fontSize);
        for (int i = 0; i < shapeResult.advances.length; ++i) {
            currentLine.width = penX;
            width = Math.max(width, penX);
            long cluster = shapeResult.clusters[i];
            char ch = text.charAt((int) cluster);
            if (ch == '\n') {
                lines.add(currentLine);
                currentLine = new Line(shapeResult.advances.length - i - 1);
                penX = 0;
                height += lineHeight;
            } else {
                float adv = shapeResult.advances[i].x * fontSize;
                if (wrap && i > 0 && penX + adv > maxWidth) {
                    int brk = findBreakBefore((int) cluster);
                    int distance = 0;
                    while (currentLine.notEmpty() && currentLine.clusters.get(currentLine.clusters.size - 1) != brk) {
                        ++distance;
                        currentLine.glyphs.pop();
                        advances.pop();
                        offsets.pop();
                        offsets.pop();
                        sizing.pop();
                        sizing.pop();
                        rotation.pop();
                        currentLine.clusters.pop();
                    }
                    lines.add(currentLine);
                    currentLine = new Line(shapeResult.advances.length - i - 1);
                    penX = 0;
                    height += lineHeight;
                    i -= distance + 1;
                    continue;
                }
                currentLine.glyphs.add(shapeResult.glyphIDs[i]);
                advances.add(adv);
                offsets.add(shapeResult.offsets[i].x * fontSize, shapeResult.offsets[i].y * fontSize);
                sizing.add(1, 1);
                rotation.add(0);
                currentLine.clusters.add(cluster);
                penX += adv;
            }
        }
        if (currentLine.notEmpty()) {
            lines.add(currentLine);
            height += lineHeight;
        }
        if (shapeResult.rtl) {
            lines.reverse();
        }
    }

    public void update() {
        lines.clear();
        if (textDirty) {
            paragraph = new Font.Paragraph(text.toString());
            shape();
        } else if (fontDirty) {
            shape();
        } else if (wrapDirty) {
            ;
        } else {
            shape();
        }
        wrapLines();
    }

    public void addText(String text) {
        this.text.append(text);
        textDirty = true;
        update();
    }

    public void setText(String text) {
        if (this.text.length() != text.length() || !this.text.toString().equals(text)) {
            this.text.setLength(0);
            addText(text);
        }
    }

    public String getText() {
        return text.toString();
    }

    public void font(Font font) {
        if (this.font != font) {
            fontDirty = true;
        }
        this.font = font;
    }

    public Font font() {
        return font;
    }

    public void fontSize(float px) {
        if (!MathUtils.isEqual(px, this.fontSize)) {
            wrapDirty = true;
        }
        this.fontSize = px;
    }

    public float fontSize() {
        return fontSize;
    }

    public void wrap(boolean wrap) {
        if (wrap != this.wrap) {
            wrapDirty = true;
        }
        this.wrap = wrap;
    }

    public boolean wrap() {
        return wrap;
    }

    public void maxWidth(float width) {
        if (!MathUtils.isEqual(this.maxWidth, width)) {
            wrapDirty = true;
        }
        this.maxWidth = width;
    }

    public float maxWidth() {
        return maxWidth;
    }
}
