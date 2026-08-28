# Matn

A font rasterization and shaping library for libGDX using [HarfBuzz](https://github.com/harfbuzz/harfbuzz/).

- Font rasterization
- GPU glyph rendering (Slug)
- Complex script shaping

## Usage

```gradle
implementation "com.github.zeroeighteightzero:matn:0.3.1"
```

```java
Typeface face = new Typeface("MyFont.ttf");
GlyphAtlas atlas = new GlyphAtlas(512);
Font font = new Font(face, atlas);

Layout layout = new Layout("Hello, world!", 64); // 64px
layout.setFont(font);
layout.markup();
```

Render
```java
batch.begin();

layout.draw(batch, 0, 0); // Raster

layout.drawGPU(batch, 0, 0); // GPU (Slug)

batch.end();
```

Dispose
```java
font.dispose();
atlas.dispose();
face.dispose();
```

## Todo
- Padding for atlas
