package com.github.zeroeighteightzero.matn;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Colors;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.utils.*;

public class RichLayout {

    private Font font;
    protected int fontSize;
    private String text;
    public Color baseColor = Color.WHITE;
    public int outlineWidth = 1;
    public Color outlineColor = Color.BLACK;
    public boolean msdf = false;
    public float width, height;

    public float maxWidth;
    public boolean wrap = false;

    public Array<Run> runs = new Array<>(4);

    public FloatArray offsets = new FloatArray();
    public FloatArray advances = new FloatArray();

    public static final short BOLD = 1 << 10;
    public static final short OBLIQUE = 1 << 9;
    public static final short UNDERLINE = 1 << 8;
    public static final short STRIKETHROUGH = 1 << 7;
    public static final short SUBSCRIPT = 1 << 5;
    public static final short MIDSCRIPT = 2 << 5;
    public static final short SUPERSCRIPT = 3 << 5;
    public static final short OUTLINE = 1 << 4;

    private boolean hasWeight;
    private boolean hasItalic;
    private boolean hasSlant;

    public RichLayout(String text, int fontSize) {
        this.text = text;
        this.fontSize = fontSize;
    }

    public void setFont(Font font) {
        hasWeight = font.hasVariableAxis("wght");
        hasItalic = font.hasVariableAxis("ital");
        hasSlant = font.hasVariableAxis("slnt");
        this.font = font;
        markup();
    }

    public void setFontSize(int fontSize) {
        this.fontSize = fontSize;
        markup();
    }

    public void setText(String text) {
        this.text = text;
        markup();
    }

    private void setFontState(short flags) {
        font.setSyntheticBold(0);
        font.setSyntheticSlant(0);
        if (hasWeight) {
            font.weight(500);
        }
        if (hasItalic) {
            font.italic(0);
        } else if (hasSlant) {
            font.slant(0);
        }
        font.outlineWidth = 0;
        if ((flags & BOLD) != 0) {
            if (hasWeight) {
                font.weight(700);
            } else {
                font.setSyntheticBold(.05f);
            }
        }
        if ((flags & OBLIQUE) != 0) {
            if (hasItalic) {
                font.italic(1);
            } else if (hasSlant) {
                font.slant(-10);
            } else {
                font.setSyntheticSlant(.22f);
            }
        }
        if ((flags & OUTLINE) != 0) {
            font.outlineWidth = outlineWidth;
        }
        font.applyVariation();
    }

    public void markup() {

        this.runs.clear();
        this.advances.clear();
        this.offsets.clear();

        ShortArray flagsHistory = new ShortArray(5);
        FloatArray colorHistory = new FloatArray(5);
        FloatArray scaleHistory = new FloatArray(5);

        IntArray segments = new IntArray(10);
        ShortArray flags = new ShortArray(5);
        FloatArray colors = new FloatArray(5);
        FloatArray scales = new FloatArray(5);

        short currentFlags = 0;
        float currentColor = baseColor.toFloatBits();
        float currentScale = 1;

        StringBuilder sb = new StringBuilder(text.length()/3);

        boolean brackets = false;
        boolean escape = false;

        int start = 0;

        for (int i = 0; i <= text.length(); ) {

            if (i == text.length()) {
                if (!brackets && sb.length() > 0) {
                    segments.add(start, i);
                    flags.add(currentFlags);
                    colors.add(currentColor);
                    scales.add(currentScale);
                }
                break;
            }

            int codePoint = text.codePointAt(i);

            if (codePoint == '[' && !escape) {
                if (sb.length() > 0) {
                    segments.add(start, i);
                    flags.add(currentFlags);
                    colors.add(currentColor);
                    scales.add(currentScale);
                    sb.setLength(0);
                }
                brackets = true;
            } else if (codePoint == ']' && !escape) {
                String s =  sb.toString();
                start = i + 1;
                switch (s) {
                    case "":
                        if (colorHistory.isEmpty()) {
                            currentFlags = 0;
                            currentColor = baseColor.toFloatBits();
                            currentScale = 1;
                        } else {
                            currentFlags = flagsHistory.pop();
                            currentColor = colorHistory.pop();
                            currentScale = scaleHistory.pop();
                        }
                        break;
                    case " ":
                        flagsHistory.clear();
                        colorHistory.clear();
                        scaleHistory.clear();
                        currentFlags = 0;
                        currentColor = baseColor.toFloatBits();
                        currentScale = 1;
                        break;
                    case "*":
                    case "B":
                    case "BOLD":
                    case "STRONG":
                        flagsHistory.add(currentFlags);
                        colorHistory.add(currentColor);
                        scaleHistory.add(currentScale);
                        currentFlags ^= BOLD;
                        break;
                    case "/":
                    case "I":
                    case "OBLIQUE":
                    case "ITALIC":
                        flagsHistory.add(currentFlags);
                        colorHistory.add(currentColor);
                        scaleHistory.add(currentScale);
                        currentFlags ^= OBLIQUE;
                        break;
                    case "^":
                    case "SUPER":
                    case "SUPERSCRIPT":
                        flagsHistory.add(currentFlags);
                        colorHistory.add(currentColor);
                        scaleHistory.add(currentScale);
                        currentFlags ^= SUPERSCRIPT;
                        break;
                    case "=":
                    case "MID":
                    case "MIDSCRIPT":
                        flagsHistory.add(currentFlags);
                        colorHistory.add(currentColor);
                        scaleHistory.add(currentScale);
                        currentFlags ^= MIDSCRIPT;
                        break;
                    case ".":
                    case "SUB":
                    case "SUBSCRIPT":
                        flagsHistory.add(currentFlags);
                        colorHistory.add(currentColor);
                        scaleHistory.add(currentScale);
                        currentFlags ^= SUBSCRIPT;
                        break;
                    case "_":
                    case "U":
                    case "UNDER":
                    case "UNDERLINE":
                        flagsHistory.add(currentFlags);
                        colorHistory.add(currentColor);
                        scaleHistory.add(currentScale);
                        currentFlags ^= UNDERLINE;
                        break;
                    case "~":
                    case "STRIKE":
                    case "STRIKETHROUGH":
                        flagsHistory.add(currentFlags);
                        colorHistory.add(currentColor);
                        scaleHistory.add(currentScale);
                        currentFlags ^= STRIKETHROUGH;
                        break;
                    case "#":
                    case "OUTLINE":
                    case "BLACK OUTLINE":
                    case "BLACKEN":
                        flagsHistory.add(currentFlags);
                        colorHistory.add(currentColor);
                        scaleHistory.add(currentScale);
                        currentFlags ^= OUTLINE;
                        break;
                    case "%":
                    case "NOSCALE":
                        flagsHistory.add(currentFlags);
                        colorHistory.add(currentColor);
                        scaleHistory.add(currentScale);
                        currentScale = 1;
                        break;
                }
                if (s.startsWith("%")) {
                    try {
                        float percentage = Float.parseFloat(s.substring(1));
                        flagsHistory.add(currentFlags);
                        colorHistory.add(currentColor);
                        scaleHistory.add(currentScale);
                        currentScale = percentage / 100f;
                    } catch (Exception ignored) {}
                }
                Color foundColor = Colors.get(s);
                if (foundColor != null) {
                    flagsHistory.add(currentFlags);
                    colorHistory.add(currentColor);
                    scaleHistory.add(currentScale);
                    currentColor = foundColor.toFloatBits();
                }
                brackets = false;
                sb.setLength(0);
            } else {
                sb.appendCodePoint(codePoint);
            }
            escape = codePoint == '\\';

            i += Character.charCount(codePoint);
        }

        Paragraph p = new Paragraph(text);

        float penX = 0;
        float penY = 0;

        font.outlineColor = outlineColor;
        for (int i = 0; i < colors.size; ++i) {
            int pStart = segments.get(i * 2);
            int pEnd = segments.get(i * 2 + 1);
            float col = colors.get(i);
            short flag = flags.get(i);
            setFontState(flag);
            Font.ShapeResult shape = font.shape(p, pStart, pEnd-pStart);
            float sx = scales.get(i);
            float sy = scales.get(i);

            boolean subscript = (flag & SUBSCRIPT) != 0;
            boolean midscript = (flag & MIDSCRIPT) != 0;
            boolean superscript = (flag & SUPERSCRIPT) != 0;

            if (subscript || midscript || superscript) {
                sx *= .4f;
                sy *= .4f;
            }

            Run run = new Run(pEnd - pStart, flag, col, sx, sy);
            run.font = font;
            run.x = penX;
            run.y = penY;

            float lineHeight = font.getLineHeight(fontSize);

            height = lineHeight;
            width = 0;

            for (int j = 0; j < shape.advances.length; ++j) {
                run.width = penX - run.x;
                boolean isWrap = wrap && maxWidth > 0 && penX + shape.advances[j].x * fontSize * sx > maxWidth;
                boolean isNewline = text.charAt((int) shape.clusters[j]) == '\n';
                if (isWrap || isNewline) {
                    runs.add(run);
                    run = new Run(pEnd - pStart, flag, col, sx, sy);
                    run.font = font;
                    penX = 0;
                    penY -= lineHeight;
                    height += lineHeight;
                    run.x = penX;
                    run.y = penY;
                    if (isNewline) {
                        continue;
                    }
                }
                Glyph glyph = font.atlas.getGlyph(font, shape.glyphIDs[j], fontSize, msdf);
                float adv = shape.advances[j].x * fontSize * sx;
                run.add(glyph);
                run.clusters.add(shape.clusters[j]);
                float ox = shape.offsets[j].x * fontSize * sx + (sx-1) * glyph.width / 2f;
                float oy = shape.offsets[j].y * fontSize * sy;

                if (midscript) {
                    oy += glyph.height;
                }
                if (subscript) {
                    oy -= (font.getAscender(fontSize) + font.getDescender(fontSize)) / 2f;
                }
                offsets.add(ox, oy);
                advances.add(adv);
                penX += adv;
                width = Math.max(width, penX);
            }
            run.width = penX - run.x;

            runs.add(run);
        }

        p.dispose();

    }

    public void draw(Batch batch, float x, float y) {
        int idx = 0;
        for (int j = 0; j < this.runs.size; ++j) {
            Run run = this.runs.get(j);
            short flags = run.flags;
            float color = run.color;
            float sx = run.scaleX;
            float sy = run.scaleY;
            batch.setPackedColor(color);
            float penX = 0;
            for (int k = 0; k < run.glyphs.size; ++k) {
                Glyph glyph = run.getGlyph(k);
                float ox = this.offsets.get(idx * 2);
                float oy = this.offsets.get(idx * 2 + 1);
                font.drawGlyph(batch, glyph, this.fontSize,  x + penX + run.x + ox, y + run.y + oy, sx, sy, 0, color);
                penX += this.advances.get(idx);
                idx++;
            }
            boolean outline = (flags & RichLayout.OUTLINE) != 0;
            if ((flags & RichLayout.UNDERLINE) != 0) {
                float underlinePosition = run.font.face.underlinePosition * this.fontSize + run.font.face.underlineThickness * this.fontSize * .5f;
                float start = run.x;
                float end = 0;
                idx -= run.glyphs.size;
                for (int k = 0; k < run.glyphs.size; ++k) {
                    if (underlinePosition > run.glyphs.get(k).top) {
                        if (outline) {
                            batch.setColor(this.outlineColor);
                            batch.draw(run.font.atlas.pixel, x + start - this.outlineWidth, y + run.y + underlinePosition * sy - this.outlineWidth, end + this.outlineWidth * 2, (run.font.face.underlineThickness * this.fontSize + this.outlineWidth * 2) * sy);
                            batch.setPackedColor(color);
                        }
                        batch.draw(run.font.atlas.pixel, x + start, y + run.y + underlinePosition * sy, end + run.x - start, run.font.face.underlineThickness * this.fontSize * sy);
                        start += end + this.advances.get(idx);
                        end = -this.advances.get(idx);
                    }
                    end += this.advances.get(idx);
                    idx++;
                }
                if (outline) {
                    batch.setColor(this.outlineColor);
                    batch.draw(run.font.atlas.pixel, x + start - this.outlineWidth, y + run.y + underlinePosition * sy - this.outlineWidth, end + this.outlineWidth * 2, (run.font.face.underlineThickness * this.fontSize + this.outlineWidth * 2) * sy);
                    batch.setPackedColor(color);
                }
                batch.draw(run.font.atlas.pixel, x + start, y + run.y + underlinePosition * sy, end, run.font.face.underlineThickness * this.fontSize * sy);
            }
            if ((flags & RichLayout.STRIKETHROUGH) != 0) {
                if (outline) {
                    batch.setColor(this.outlineColor);
                    batch.draw(run.font.atlas.pixel, x + run.x - this.outlineWidth, y + run.y + run.font.face.strikoutPosition * this.fontSize * sy - this.outlineWidth - run.font.face.strikoutThickness * this.fontSize * sy * .5f, run.width + this.outlineWidth * 2, (run.font.face.strikoutThickness * this.fontSize + this.outlineWidth * 2) * sy);
                    batch.setPackedColor(color);
                }
                batch.draw(run.font.atlas.pixel, x + run.x, y + run.y + run.font.face.strikoutPosition * this.fontSize * sy - run.font.face.strikoutThickness * this.fontSize * sy * .5f, run.width, run.font.face.strikoutThickness * this.fontSize * sy);
            }
        }
    }

    /**
     * A single laid-out line of glyphs.
     *
     * <p>Collects the glyph identifiers and their source clusters produced by shaping and
     * wrapping, together with the accumulated width of the line.</p>
     */
    public static class Run {

        public float x, y;
        public Font font;
        /** The font-specific glyph identifiers in this line, in drawing order. */
        public final Array<Glyph> glyphs;
        public final float color;
        public final short flags;
        public final float scaleX;
        public final float scaleY;
        /** The source UTF-16 cluster index for each glyph in {@link #glyphs}. */
        public final LongArray clusters;
        /** The horizontal width of this line. */
        public float width;

        /**
         * Creates an empty line with the given initial capacity.
         *
         * @param length the initial capacity in glyphs
         */
        public Run(int length, short flags, float color, float scaleX, float scaleY) {
            glyphs = new Array<>(length);
            clusters = new LongArray(length);
            this.flags = flags;
            this.color = color;
            this.scaleX = scaleX;
            this.scaleY = scaleY;
        }

        public void add(Glyph glyph) {
            glyphs.add(glyph);
        }

        public Glyph getGlyph(int index) {
            return glyphs.get(index);
        }

        /**
         * Returns whether this line contains at least one glyph.
         *
         * @return {@code true} if the line is not empty
         */
        public boolean notEmpty() {
            return glyphs.notEmpty();
        }

    }
}
