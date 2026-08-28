package com.github.zeroeighteightzero.matn;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.utils.*;

public class Layout {

    private Font font;
    protected int fontSize;
    private String text;
    public Color color = Color.WHITE;
    public float width, height;

    public float maxWidth;
    public boolean wrap = false;

    public Array<Run> runs = new Array<>(4);

    public FloatArray offsets = new FloatArray();
    public FloatArray advances = new FloatArray();

    public Layout(String text, int fontSize) {
        this.text = text;
        this.fontSize = fontSize;
    }

    public void setFont(Font font) {
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

    public void markup() {

        this.runs.clear();
        this.advances.clear();
        this.offsets.clear();

        Paragraph p = new Paragraph(text);

        float penX = 0;
        float penY = 0;

        Run run = new Run(text.length());
        run.x = 0;
        run.y = 0;

        Font.ShapeResult shape = font.shape(p);

        float lineHeight = font.getLineHeight(fontSize);

        height = lineHeight;
        width = 0;

        for (int i = 0; i < shape.advances.length; ++i) {
            run.width = penX - run.x;
            boolean isWrap = wrap && maxWidth > 0 && penX + shape.advances[i].x * fontSize > maxWidth;
            boolean isNewline = text.charAt((int) shape.clusters[i]) == '\n';
            if (isWrap || isNewline) {
                runs.add(run);
                run = new Run(text.length() - i + 1);
                penX = 0;
                penY -= lineHeight;
                height += lineHeight;
                run.x = penX;
                run.y = penY;
                if (isNewline) {
                    continue;
                }
            }
            float adv = shape.advances[i].x * fontSize;
            run.add(shape.glyphIDs[i]);
            run.clusters.add(shape.clusters[i]);
            float ox = shape.offsets[i].x * fontSize;
            float oy = shape.offsets[i].y * fontSize;

            offsets.add(ox, oy);
            advances.add(adv);
            penX += adv;
            width = Math.max(width, penX);
        }
        run.width = penX - run.x;

        runs.add(run);

        p.dispose();

    }

    public void draw(Batch batch, float x, float y) {
        float colorPacked = color.toFloatBits();
        int idx = 0;
        for (int j = 0; j < this.runs.size; ++j) {
            Run run = this.runs.get(j);
            float penX = 0;
            for (int k = 0; k < run.glyphs.size; ++k) {
                int glyphID = run.getGlyphID(k);
                Glyph glyph = font.atlas.getGlyph(font, glyphID, fontSize, false);
                float ox = this.offsets.get(idx * 2);
                float oy = this.offsets.get(idx * 2 + 1);
                font.drawGlyph(batch, glyph, this.fontSize, x + penX + run.x + ox, y + run.y + oy, 1, 1, 0, colorPacked);
                penX += this.advances.get(idx);
                idx++;
            }
        }
    }

    public void drawGPU(GPUGlyphBatch batch, float x, float y) {
        float colorPacked = color.toFloatBits();
        int idx = 0;
        for (int j = 0; j < this.runs.size; ++j) {
            Run run = this.runs.get(j);
            float penX = 0;
            for (int k = 0; k < run.glyphs.size; ++k) {
                int glyphID = run.getGlyphID(k);
                GPUGlyph glyph = font.atlas.getGPUGlyph(font, glyphID);
                float ox = this.offsets.get(idx * 2);
                float oy = this.offsets.get(idx * 2 + 1);
                font.drawGPUGlyph(batch, glyph, this.fontSize, x + penX + run.x + ox, y + run.y + oy, colorPacked);
                penX += this.advances.get(idx);
                idx++;
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
        /** The font-specific glyph identifiers in this line, in drawing order. */
        public final IntArray glyphs;
        /** The source UTF-16 cluster index for each glyph in {@link #glyphs}. */
        public final LongArray clusters;
        /** The horizontal width of this line. */
        public float width;

        /**
         * Creates an empty line with the given initial capacity.
         *
         * @param length the initial capacity in glyphs
         */
        public Run(int length) {
            glyphs = new IntArray(length);
            clusters = new LongArray(length);
        }

        public void add(int gid) {
            glyphs.add(gid);
        }

        public int getGlyphID(int index) {
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
