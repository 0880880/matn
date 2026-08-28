package com.github.zeroeighteightzero.matn;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Window;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3WindowListener;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class FontViewer implements ApplicationListener, Lwjgl3WindowListener {

    public static void main(String[] args) {
        Lwjgl3ApplicationConfiguration configuration = new Lwjgl3ApplicationConfiguration();
        configuration.setOpenGLEmulation(Lwjgl3ApplicationConfiguration.GLEmulation.GL32, 3, 2);
        FontViewer fontViewer = new FontViewer();
        configuration.setWindowListener(fontViewer);
        new Lwjgl3Application(fontViewer, configuration);
    }

    SpriteBatch batch;
    OrthographicCamera camera;
    ScreenViewport viewport;
    ScreenViewport guiViewport;
    SimpleGPUGlyphBatch gpuBatch;

    GlyphAtlas atlas;

    Typeface face;
    Typeface prevFace;
    Font[] fonts;
    Layout[] layouts;

    int fontSize = 64;
    float targetZoom = 1;
    boolean dragging = false;
    int dragButton = -1;
    float lastMouseX;
    float lastMouseY;
    float pressX;
    float pressY;

    private static final float MIN_ZOOM = .01f;
    private static final float MAX_ZOOM = 100f;
    private static final float ZOOM_SENSITIVITY = 0.12f;
    private static final float ZOOM_SMOOTHING = 18f;
    private static final float CLICK_THRESHOLD_PX = 6f;

    String text = "The quick brown fox jumps over the lazy dog.";

    Typeface guiFace;
    Font guiFont;
    Layout guiLayout;

    boolean useGPU = false;

    private void disposeFonts() {
        if (fonts == null) {
            return;
        }
        for (Font font : fonts) {
            font.dispose();
        }
    }

    private void update() {

        if (face.weight() != null) {
            float weightStart = face.weight().min;
            float weightEnd = face.weight().max;
            int stops = 8;
            if (prevFace != face) {
                fonts = new Font[stops];
            }
            layouts = new Layout[stops];
            for (int i = 0; i < stops; i++) {
                float t = i / (float) (stops-1);
                float w = MathUtils.lerp(weightStart, weightEnd, t);

                if (prevFace != face) {
                    Font font = new Font(face, atlas);
                    if (face.opticalSize() != null) {
                        font.opticalSize(face.opticalSize().max);
                    }
                    font.weight(w);
                    font.applyVariation();

                    fonts[i] = font;
                }

                Layout layout = new Layout(text, fontSize);
                layout.setFont(fonts[i]);
                layout.markup();

                layouts[i] = layout;
            }
        } else {
            if (prevFace != face) {
                fonts = new Font[1];

                Font font = new Font(face, atlas);
                if (face.opticalSize() != null) {
                    font.opticalSize(face.opticalSize().max);
                }
                font.applyVariation();

                fonts[0] = font;
            }

            layouts = new Layout[1];
            Layout layout = new Layout(text, fontSize);
            layout.setFont(fonts[0]);
            layout.markup();

            layouts[0] = layout;
        }
        prevFace = face;
    }

    @Override
    public void create() {

        batch = new SpriteBatch();
        camera = new OrthographicCamera();
        viewport = new ScreenViewport(camera);
        guiViewport = new ScreenViewport();

        atlas = new GlyphAtlas(512, Pixmap.Format.RGBA8888);
        atlas.padding = 1;

        face = new Typeface("Inter/Inter-VariableFont_opsz,wght.ttf");

        gpuBatch = new SimpleGPUGlyphBatch();

        update();

        guiFace = new Typeface("Inter/Inter-VariableFont_opsz,wght.ttf");
        guiFont = new Font(guiFace, atlas);
        guiFont.outlineWidth = 3;
        guiLayout = new Layout("Drop a font to view.\nCtrl+G to toggle GPU mode", 24);
        guiLayout.setFont(guiFont);
        guiLayout.markup();

        Gdx.input.setInputProcessor(new InputProcessor() {
            @Override
            public boolean keyDown(int keycode) {
                return false;
            }

            @Override
            public boolean keyUp(int keycode) {
                return false;
            }

            @Override
            public boolean keyTyped(char character) {
                if (character == '\n' || character == '\r') {
                    return false;
                }
                if (character == '\b') {
                    if (text.isEmpty()) {
                        return false;
                    }
                    text = text.substring(0, text.length() - 1);
                } else {
                    text += character;
                }
                update();
                return true;
            }

            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                if (button == Input.Buttons.LEFT) {
                    dragging = true;
                    dragButton = button;
                    lastMouseX = screenX;
                    lastMouseY = screenY;
                    pressX = screenX;
                    pressY = screenY;
                    return true;
                }
                return false;
            }

            @Override
            public boolean touchDragged(int screenX, int screenY, int pointer) {
                if (!dragging || dragButton != Input.Buttons.LEFT) {
                    return false;
                }

                float dx = screenX - lastMouseX;
                float dy = screenY - lastMouseY;

                lastMouseX = screenX;
                lastMouseY = screenY;

                camera.position.x -= dx * camera.zoom;
                camera.position.y += dy * camera.zoom;

                return true;
            }

            @Override
            public boolean touchUp(int screenX, int screenY, int pointer, int button) {
                if (button == Input.Buttons.LEFT && dragging) {
                    dragging = false;
                    dragButton = -1;

                    float dist2 = Vector2.dst2(pressX, pressY, screenX, screenY);
                    if (dist2 <= CLICK_THRESHOLD_PX * CLICK_THRESHOLD_PX) {
                        Gdx.app.log("Input", "Clicked at " + screenX + ", " + screenY);
                    }

                    return true;
                }
                return false;
            }

            @Override
            public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
                return false;
            }

            @Override
            public boolean mouseMoved(int screenX, int screenY) {
                return false;
            }

            @Override
            public boolean scrolled(float amountX, float amountY) {
                targetZoom *= (float) Math.exp(amountY * ZOOM_SENSITIVITY);
                targetZoom = MathUtils.clamp(
                        targetZoom,
                        MIN_ZOOM,
                        MAX_ZOOM
                );
                return false;
            }
        });
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        guiViewport.update(width, height, true);
    }

    @Override
    public void render() {

        Gdx.graphics.setTitle("FPS=" + Gdx.graphics.getFramesPerSecond());

        ScreenUtils.clear(Color.BLACK);

        float alpha = 1f - (float) Math.exp(
                -ZOOM_SMOOTHING * Gdx.graphics.getDeltaTime()
        );

        camera.zoom = MathUtils.lerp(camera.zoom, targetZoom, alpha);

        if (Math.abs(camera.zoom - targetZoom) < 0.01f) {
            camera.zoom = targetZoom;
        }

        camera.update();
        viewport.apply();

        if (Gdx.input.isKeyJustPressed(Input.Keys.G) && Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT)) {
            useGPU = !useGPU;
        }

        if (useGPU) {
            gpuBatch.setProjectionMatrix(camera.combined);
            gpuBatch.begin();

            float y = 0;

            for (Layout l : layouts) {
                l.drawGPU(gpuBatch, 0, -y);
                y += l.height;
            }

            gpuBatch.end();
        } else {

            batch.setProjectionMatrix(camera.combined);
            batch.begin();
            batch.setColor(Color.WHITE);

            float y = 0;

            for (Layout l : layouts) {
                l.draw(batch, 0, -y);
                y += l.height;
            }

            batch.end();
        }

        batch.setProjectionMatrix(guiViewport.getCamera().combined);
        batch.begin();
        batch.setColor(Color.WHITE);

        guiLayout.draw(batch, 0, Gdx.graphics.getHeight()-guiFont.getLineHeight(guiLayout.fontSize));

        batch.end();

    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
        disposeFonts();
        guiFont.dispose();
        atlas.dispose();
        guiFace.dispose();
        face.dispose();
    }

    @Override
    public void created(Lwjgl3Window window) {

    }

    @Override
    public void iconified(boolean isIconified) {

    }

    @Override
    public void maximized(boolean isMaximized) {

    }

    @Override
    public void focusLost() {

    }

    @Override
    public void focusGained() {

    }

    @Override
    public boolean closeRequested() {
        return true;
    }

    @Override
    public void filesDropped(String[] files) {
        for (String f : files) {
            if (f.endsWith(".ttf")) {
                try {
                    Typeface newFace = new Typeface(Gdx.files.absolute(f));
                    atlas.dispose();
                    atlas = new GlyphAtlas(512);
                    disposeFonts();
                    face.dispose();
                    face = newFace;
                    update();
                    break;
                } catch (Exception e) {
                    System.err.println("Failed to load font \"" + f + "\"");
                }
            }
        }
    }

    @Override
    public void refreshRequested() {

    }
}
