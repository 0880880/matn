package com.github.zeroeighteightzero.matn;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.LongMap;

/**
 * Texture atlas for font glyphs. Glyphs are packed with true Bottom-Left placement
 * (Baker–Coffman–Rivest): each glyph is placed at the lexicographically minimal
 * feasible (y, x). That is the placement rule used by FQW-ordered Bottom-Left
 * (Hougardy & Zondervan, STACS 2026). Because {@link #getGlyph} is online, the
 * offline FQW global ordering cannot be applied; placement is irrevocable BL.
 */
public class GlyphAtlas implements Disposable {

    public static class Page {

        /**
         * Axis-aligned rectangle already packed into this page.
         */
        public static class Rect {
            public final int x, y, width, height;

            public Rect(int x, int y, int width, int height) {
                this.x = x;
                this.y = y;
                this.width = width;
                this.height = height;
            }

            /**
             * True if this rect overlaps the open interval of the proposed placement.
             */
            boolean overlaps(int ox, int oy, int ow, int oh) {
                return ox < x + width && ox + ow > x && oy < y + height && oy + oh > y;
            }
        }

        public final Texture texture;
        private final Pixmap.Format format;

        final Array<Rect> placed = new Array<>();

        public Page(int pageSize, Pixmap.Format format) {
            this.format = format;
            texture = new Texture(pageSize, pageSize, format);

            Pixmap something = new Pixmap(14, 44, format);
            something.setColor(0, 1, 0, 1);
            something.fill();
            texture.draw(something, 0, 0);
        }

        void place(Pixmap pixmap, int x, int y) {
            Pixmap correctPixmap = pixmap;
            if (pixmap.getFormat() != this.format) {
                correctPixmap = new Pixmap(pixmap.getWidth(), pixmap.getHeight(), format);
                correctPixmap.drawPixmap(pixmap, 0, 0);
            }
            texture.draw(correctPixmap, x, y);
        }

    }

    public Array<Page> pages = new Array<>(1);
    public LongMap<Glyph> glyphMap = new LongMap<>();
    public final int pageSize;
    public final Pixmap.Format format;

    public GlyphAtlas(int pageSize, Pixmap.Format format) {
        this.pageSize = pageSize;
        this.format = format;
        pages.add(new Page(pageSize, format));
    }

    public GlyphAtlas(int pageSize) {
        this(pageSize, Pixmap.Format.RGBA8888);
    }

    public Glyph getGlyph(Font font, long glyphID, int size) {
        int steppedSize = Utils.getFontSize(size);
        long hash = Utils.glyphHashWithSize(font, glyphID, steppedSize);
        if (glyphMap.containsKey(hash)) {
            return glyphMap.get(hash);
        }
        Font.GlyphMetrics metrics = font.getGlyphMetrics(glyphID);
        Pixmap pixmap = font.rasterize(glyphID, steppedSize);
        int width = pixmap.getWidth(), height = pixmap.getHeight();

        // Glyphs with zero area (e.g., space) don't consume atlas space.
        if (width == 0 || height == 0) {
            pixmap.dispose();
            Glyph glyph = new Glyph(this, glyphID, steppedSize, 0, 0, 0, 0, 0, metrics.bearingX, metrics.bearingY);
            glyphMap.put(hash, glyph);
            return glyph;
        }

        // Glyph larger than a whole page cannot be packed.
        if (width > pageSize || height > pageSize) {
            pixmap.dispose();
            throw new RuntimeException("Glyph too large for page size: "
                    + width + "x" + height + " > " + pageSize);
        }

        // Try Bottom-Left on every existing page.
        for (Page page : pages) {
            int[] pos = findBottomLeft(page, width, height);
            if (pos != null) {
                int x = pos[0], y = pos[1];
                page.placed.add(new Page.Rect(x, y, width, height));
                page.place(pixmap, x, y);

                Glyph glyph = new Glyph(this, glyphID, steppedSize, pages.indexOf(page, true),
                        x, y, width, height, metrics.bearingX, metrics.bearingY);
                glyphMap.put(hash, glyph);
                pixmap.dispose();
                return glyph;
            }
        }

        // No page could accommodate the glyph → create a new page; place at (0, 0).
        Page newPage = new Page(pageSize, format);
        pages.add(newPage);
        newPage.placed.add(new Page.Rect(0, 0, width, height));
        newPage.place(pixmap, 0, 0);

        Glyph glyph = new Glyph(this, glyphID, steppedSize, pages.size - 1, 0, 0, width, height, metrics.bearingX, metrics.bearingY);
        glyphMap.put(hash, glyph);
        pixmap.dispose();
        return glyph;
    }

    /**
     * True Bottom-Left placement (Baker et al.; used by FQW-ordered BL, STACS 2026).
     * <p>
     * By the supporter property of BL, a feasible lower-left corner is always at an
     * intersection of a vertical line through a previous right edge (or {@code x = 0})
     * and a horizontal line through a previous top edge (or {@code y = 0}). Candidates
     * are therefore complete: we enumerate those points and return the lexicographically
     * minimal feasible {@code (y, x)}.
     *
     * @return {@code {x, y}} or {@code null} if the page is full
     */
    private int[] findBottomLeft(Page page, int width, int height) {
        IntArray candX = new IntArray(page.placed.size + 1);
        IntArray candY = new IntArray(page.placed.size + 1);
        candX.add(0);
        candY.add(0);
        for (Page.Rect r : page.placed) {
            candX.add(r.x + r.width);
            candY.add(r.y + r.height);
        }
        sortUnique(candX);
        sortUnique(candY);

        int bestX = -1;
        int bestY = Integer.MAX_VALUE;

        for (int yi = 0; yi < candY.size; yi++) {
            int y = candY.get(yi);
            if (y + height > pageSize) {
                continue;
            }
            // ys are sorted ascending; once y exceeds bestY nothing better remains
            if (y > bestY) {
                break;
            }

            for (int xi = 0; xi < candX.size; xi++) {
                int x = candX.get(xi);
                if (x + width > pageSize) {
                    continue;
                }
                if (y == bestY && bestX >= 0 && x >= bestX) {
                    continue;
                }

                if (fits(page, x, y, width, height)) {
                    if (y < bestY || (y == bestY && (bestX < 0 || x < bestX))) {
                        bestX = x;
                        bestY = y;
                    }
                }
            }
        }

        if (bestX < 0) {
            return null;
        }
        return new int[]{bestX, bestY};
    }

    /**
     * Whether {@code [x, x+w) × [y, y+h)} lies entirely free inside the page.
     */
    private boolean fits(Page page, int x, int y, int w, int h) {
        if (x < 0 || y < 0 || x + w > pageSize || y + h > pageSize) {
            return false;
        }
        for (Page.Rect r : page.placed) {
            if (r.overlaps(x, y, w, h)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Sort ascending and drop duplicates in place.
     */
    private static void sortUnique(IntArray a) {
        a.sort();
        int write = 0;
        for (int read = 0; read < a.size; read++) {
            int v = a.get(read);
            if (write == 0 || v != a.get(write - 1)) {
                a.set(write++, v);
            }
        }
        a.size = write;
    }

    @Override
    public void dispose() {
        for (Page page : pages) {
            page.texture.dispose();
        }
        pages.clear();
        glyphMap.clear();
    }
}