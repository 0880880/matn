# Matn

A font rasterization and shaping library for libGDX using [HarfBuzz](https://github.com/harfbuzz/harfbuzz/).

- Font rasterization
- GPU glyph rendering (Slug)
- Complex script shaping

---

## Usage

Create a `Typeface`

<!-- @formatter:off -->
```java
Typeface face = Typeface.fromFile(myFontFile);
```

Create a `Font` from the `Typeface` and set options

```java
Font boldFont = face.createFont();

boldFont.weight(800); // Sets the OpenType MM weight
// if doesn't have weight/variations
boldFont.setSyntheticBold(.01f, .01f, false);
```

Create an atlas

```java
GlyphAtlas atlas = new GlyphAtlas(); // For CPU rasterization

GPUGlyphAtlas gpuAtlas = new GPUGlyphAtlas(); // For GPU rendering (no color)
```

Draw:

```java

@Override
public void render() {
    batch.begin();
    batch.setColor(Color.WHITE);

    boldFont.drawText(batch, atlas, "Hello, World!", 48, x, y);

    batch.end();
}
```
<!-- @formatter:on -->
