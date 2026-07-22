package com.github.zeroeighteightzero.matn;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class Main implements ApplicationListener {

    public static void main(String[] args) {
        Lwjgl3ApplicationConfiguration configuration = new Lwjgl3ApplicationConfiguration();
        configuration.setOpenGLEmulation(Lwjgl3ApplicationConfiguration.GLEmulation.GL32, 3, 2);
        new Lwjgl3Application(new Main(), configuration);
    }

    SpriteBatch batch;
    OrthographicCamera camera;
    ScreenViewport viewport;
    GPUTextBatch gpuBatch;

    GPUGlyphAtlas gpuAtlas;

    Font[] fonts;
    Layout[] layouts;

    float fontSize = 48;
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

    @Override
    public void create() {

        batch = new SpriteBatch();
        camera = new OrthographicCamera();
        viewport = new ScreenViewport(camera);

        Typeface face = Typeface.fromFile(Gdx.files.internal("Inter/Inter-VariableFont_opsz,wght.ttf"));
        face.createFont().setSyntheticBold(.01f, .01f, false);

        gpuAtlas = new GPUGlyphAtlas();
        gpuBatch = new GPUTextBatch();

        int numWeights = 8;
        float weightStart = face.varAxes[1].min;
        float weightEnd = face.varAxes[1].max;
        float step = (weightEnd - weightStart) / (numWeights - 1);

        fonts = new Font[numWeights];
        layouts = new Layout[numWeights];

        for (int i = 0; i < numWeights; i++) {
            float w = weightStart + i * step;

            Font font = face.createFont();
            font.opticalSize(face.varAxes[0].max);
            font.weight(w);
            font.applyVariation();

            Layout layout = new Layout(text, font, fontSize);
            layout.wrap(false);

            fonts[i] = font;
            layouts[i] = layout;
        }

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
                for (Layout layout : layouts) {
                    layout.setText(text);
                }
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

        gpuBatch.setProjectionMatrix(camera.combined);
        gpuBatch.begin();
        gpuBatch.setColor(Color.WHITE);

        for (int i = 0; i < fonts.length; ++i) {
            Font font = fonts[i];
            Layout layout = layouts[i];
            font.drawGPUText(gpuBatch, gpuAtlas, layout, 32, -32 - font.getLineHeight(fontSize) * i + Gdx.graphics.getHeight() - font.getAscender(fontSize));
        }

        gpuBatch.end();

    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
    }
}