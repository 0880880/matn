# Matn

A font rasterization and shaping library for libGDX using [HarfBuzz](https://github.com/harfbuzz/harfbuzz/).

- Font rasterization
- GPU glyph rendering (Slug)
- Complex script shaping

---

## Todo
- MSDF glyph rendering
- Text wrapping
- GPU text layout
- Padding for atlas

## Usage

Create a `Typeface`

<!-- @formatter:off -->
```java
Typeface face = new Typeface(myFontFile);
```

Create an atlas

```java
GlyphAtlas atlas = new GlyphAtlas();
```

Create a `Font` from the `Typeface` and set options

```java
Font boldFont = new Font(face, atlas);

boldFont.weight(800); // Sets the OpenType MM weight
// if doesn't have weight/variations
boldFont.setSyntheticBold(.01f);
```

Draw:

```java
@Override
public void render() {
    batch.begin();
    batch.setColor(Color.WHITE);

    boldFont.drawText(batch, "Hello, World!", 48, x, y);

    batch.end();
}
```

After use:
```java
font.dispose();
atlas.dispose();
face.dispose();
```
<!-- @formatter:on -->
