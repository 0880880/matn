package io.github.zeroeighteightzero.matn;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.FloatArray;

/**
 * Prepares text for rendering by shaping, wrapping, and splitting it into lines.
 *
 * <p>A layout holds a font, a font size, the text to render, and optional wrapping
 * constraints. After configuration, {@link #update()} re-shapes and re-wraps the text and
 * repopulates the per-glyph {@link #advances}, {@link #offsets}, {@link #sizing}, and
 * {@link #rotation} arrays consumed by the drawing methods in {@link Font}.</p>
 */
public class Layout {

    protected Font.Paragraph paragraph = null;
    protected final Array<Line> lines = new Array<>(true, 8);
    protected Font.ShapeResult shapeResult;
    protected Font font;
    protected float fontSize;
    protected final StringBuilder text = new StringBuilder();
    protected boolean wrap = false;
    protected float maxWidth;

    /** The overall width of the laid-out text. */
    public float width;
    /** The overall height of the laid-out text. */
    public float height;
    /** The height of each laid-out line. */
    public float lineHeight;

    private boolean textDirty = false;
    private boolean fontDirty = false;
    private boolean wrapDirty = false;

    /** The horizontal advance for each glyph, in drawing order. */
    public final FloatArray advances = new FloatArray(true, 8);
    /** The horizontal and vertical offset for each glyph, in drawing order. */
    public final FloatArray offsets = new FloatArray(true, 16);
    /** The horizontal and vertical scale for each glyph, in drawing order. */
    public final FloatArray sizing = new FloatArray(true, 16);
    /** The rotation for each glyph, in drawing order. */
    public final FloatArray rotation = new FloatArray(true, 8);

    /**
     * Creates a layout for the given font and font size with empty text.
     *
     * @param font the font used to shape the text
     * @param fontSize the font size in pixels
     */
    public Layout(Font font, float fontSize) {
        this.font = font;
        this.fontSize = fontSize;
    }

    /**
     * Creates a layout for the given text, font, and font size.
     *
     * @param text the initial text to lay out
     * @param font the font used to shape the text
     * @param fontSize the font size in pixels
     */
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

    /**
     * Re-shapes and re-wraps the current text, refreshing line and per-glyph data.
     *
     * <p>Calling this is generally unnecessary because the configuration mutators invoke
     * it automatically; it is exposed so that repeated property changes can be batched
     * before a single update.</p>
     */
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

    /**
     * Appends text to the layout and re-shapes it.
     *
     * @param text the text to append
     */
    public void addText(String text) {
        this.text.append(text);
        textDirty = true;
        update();
    }

    /**
     * Replaces the layout text and re-shapes it.
     *
     * <p>If the new text is identical to the current text, no work is performed.</p>
     *
     * @param text the new text
     */
    public void setText(String text) {
        if (this.text.length() != text.length() || !this.text.toString().equals(text)) {
            this.text.setLength(0);
            addText(text);
        }
    }

    /**
     * Returns the current layout text.
     *
     * @return the current text
     */
    public String getText() {
        return text.toString();
    }

    /**
     * Sets the font used to shape the text and re-shapes it.
     *
     * @param font the new font
     */
    public void font(Font font) {
        if (this.font != font) {
            fontDirty = true;
        }
        this.font = font;
    }

    /**
     * Returns the font used to shape the text.
     *
     * @return the current font
     */
    public Font font() {
        return font;
    }

    /**
     * Sets the font size in pixels and re-lays-out the text.
     *
     * @param px the font size in pixels
     */
    public void fontSize(float px) {
        if (!MathUtils.isEqual(px, this.fontSize)) {
            wrapDirty = true;
        }
        this.fontSize = px;
    }

    /**
     * Returns the font size in pixels.
     *
     * @return the current font size
     */
    public float fontSize() {
        return fontSize;
    }

    /**
     * Sets whether lines should wrap at {@link #maxWidth}.
     *
     * @param wrap {@code true} to enable line wrapping
     */
    public void wrap(boolean wrap) {
        if (wrap != this.wrap) {
            wrapDirty = true;
        }
        this.wrap = wrap;
    }

    /**
     * Returns whether line wrapping is enabled.
     *
     * @return {@code true} if lines wrap at {@link #maxWidth}
     */
    public boolean wrap() {
        return wrap;
    }

    /**
     * Sets the maximum line width used when wrapping is enabled.
     *
     * @param width the maximum line width in pixels
     */
    public void maxWidth(float width) {
        if (!MathUtils.isEqual(this.maxWidth, width)) {
            wrapDirty = true;
        }
        this.maxWidth = width;
    }

    /**
     * Returns the maximum line width used when wrapping is enabled.
     *
     * @return the maximum line width in pixels
     */
    public float maxWidth() {
        return maxWidth;
    }
}
