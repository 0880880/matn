package com.github.zeroeighteightzero.matn;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;

import java.nio.Buffer;

/**
 * Draws batched quads using indices.
 *
 * @author mzechner
 * @author Nathan Sweet
 * @see Batch
 */
public class GPUTextBatch implements Disposable {

    private static final int GLYPH_SIZE = 32;

    private final Mesh.VertexDataType currentDataType;

    private final Mesh mesh;

    final float[] vertices;
    int idx = 0;
    GlyphAtlas lastAtlas = null;
    GPUGlyph lastGlyph = null;

    boolean drawing = false;

    private final Matrix4 transformMatrix = new Matrix4();
    private final Matrix4 projectionMatrix = new Matrix4();
    private final Matrix4 combinedMatrix = new Matrix4();

    private boolean blendingDisabled = false;
    private int blendSrcFunc = GL20.GL_SRC_ALPHA;
    private int blendDstFunc = GL20.GL_ONE_MINUS_SRC_ALPHA;
    private int blendSrcFuncAlpha = GL20.GL_SRC_ALPHA;
    private int blendDstFuncAlpha = GL20.GL_ONE_MINUS_SRC_ALPHA;

    private final ShaderProgram shader;
    private ShaderProgram customShader = null;
    private final boolean ownsShader;

    private final Color color = new Color(1, 1, 1, 1);
    float colorPacked = Color.WHITE_FLOAT_BITS;

    /**
     * Number of render calls since the last {@link #begin()}.
     **/
    public int renderCalls = 0;

    /**
     * Number of rendering calls, ever. Will not be reset unless set manually.
     **/
    public int totalRenderCalls = 0;

    /**
     * The maximum number of sprites rendered in one batch so far.
     **/
    public int maxGlyphsInBatch = 0;

    /**
     * Constructs a new SpriteBatch with a size of 1000, one buffer, and the default shader.
     *
     * @see com.badlogic.gdx.graphics.g2d.SpriteBatch#SpriteBatch(int, ShaderProgram)
     */
    public GPUTextBatch() {
        this(1000);
    }

    /**
     * Constructs a new SpriteBatch. Sets the projection matrix to an orthographic projection with y-axis point upwards, x-axis
     * point to the right and the origin being in the bottom left corner of the screen. The projection will be pixel perfect with
     * respect to the current screen resolution.
     * <p>
     *
     * @param size The max number of sprites in a single batch. Max of 8191.
     */
    public GPUTextBatch(int size) {
        // 32767 is max vertex index, so 32767 / 4 vertices per sprite = 8191 sprites max.
        if (size > 8191) throw new IllegalArgumentException("Can't have more than 8191 sprites per batch: " + size);

        Mesh.VertexDataType vertexDataType = (Gdx.gl30 != null) ? Mesh.VertexDataType.VertexBufferObjectWithVAO : Mesh.VertexDataType.VertexBufferObject;

        currentDataType = vertexDataType;

        mesh = new Mesh(currentDataType, false, size * 4, size * 6,
                new VertexAttribute(VertexAttributes.Usage.Position, 2, ShaderProgram.POSITION_ATTRIBUTE),
                new VertexAttribute(VertexAttributes.Usage.TextureCoordinates, 2, GL20.GL_FLOAT, false, ShaderProgram.TEXCOORD_ATTRIBUTE + "0"),
                new VertexAttribute(VertexAttributes.Usage.ColorPacked, 4, ShaderProgram.COLOR_ATTRIBUTE),
                new VertexAttribute(VertexAttributes.Usage.Normal, 2, ShaderProgram.NORMAL_ATTRIBUTE),
                new VertexAttribute(VertexAttributes.Usage.Generic, 1, GL20.GL_FLOAT, false, "a_emPerPos"),
                new VertexAttribute(VertexAttributes.Usage.Generic, 1, GL20.GL_FLOAT, false, "a_glyphLoc"));

        projectionMatrix.setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        vertices = new float[size * GLYPH_SIZE];

        int len = size * 6;
        short[] indices = new short[len];
        short j = 0;
        for (int i = 0; i < len; i += 6, j += 4) {
            indices[i] = j;
            indices[i + 1] = (short) (j + 1);
            indices[i + 2] = (short) (j + 2);
            indices[i + 3] = (short) (j + 2);
            indices[i + 4] = (short) (j + 3);
            indices[i + 5] = j;
        }
        mesh.setIndices(indices);

        shader = createDefaultShader();
        ownsShader = true;

        // Pre bind the mesh to force the upload of indices data.
        if (vertexDataType != Mesh.VertexDataType.VertexArray) {
            mesh.getIndexData().bind();
            mesh.getIndexData().unbind();
        }
    }

    /**
     * Returns a new instance of the default shader used by SpriteBatch for GL2 when no shader is specified.
     */
    static public ShaderProgram createDefaultShader() {

        String vertexShader = "#version 330\n" +
                Font.getVertexShader() + "\n" +
                "uniform mat4 u_projTrans;\n" +
                "uniform vec2 u_viewport;\n" +
                "\n" +
                "in vec2 " + ShaderProgram.POSITION_ATTRIBUTE + ";\n" +
                "in vec2 " + ShaderProgram.TEXCOORD_ATTRIBUTE + "0;\n" +
                "in vec4 " + ShaderProgram.COLOR_ATTRIBUTE + ";\n" +
                "in vec2 " + ShaderProgram.NORMAL_ATTRIBUTE + ";\n" +
                "in float a_emPerPos;\n" +
                "in float a_glyphLoc;\n" +
                "\n" +
                "out vec2 v_texCoords;\n" +
                "out vec4 v_color;\n" +
                "flat out uint v_glyphLoc;\n" +
                "\n" +
                "void main () {\n" +
                "  v_color = " + ShaderProgram.COLOR_ATTRIBUTE + ";\n" +
                "  v_color.a = v_color.a * (255.0/254.0);\n" +
                "  vec2 pos = " + ShaderProgram.POSITION_ATTRIBUTE + ";\n" +
                "  vec2 tex = " + ShaderProgram.TEXCOORD_ATTRIBUTE + "0;\n" +
                "  vec4 jac = vec4 (a_emPerPos, 0.0, 0.0, -a_emPerPos);\n" +
                "  hb_gpu_dilate (pos, tex, " + ShaderProgram.NORMAL_ATTRIBUTE + ", jac,\n" +
                "                 u_projTrans, u_viewport);\n" +
                "  gl_Position = u_projTrans * vec4 (pos, 0.0, 1.0);\n" +
                "  v_texCoords = tex;\n" +
                "  v_glyphLoc = floatBitsToUint(a_glyphLoc);\n" +
                "}";

        String fragmentShader = "#version 330\n" +
                Font.getFragmentShader() + "\n" +
                "in vec2 v_texCoords;\n" +
                "in vec4 v_color;\n" +
                "flat in uint v_glyphLoc;\n" +
                "out vec4 fragColor;\n" +
                "\n" +
                "void main () {\n" +
                "  float coverage = hb_gpu_draw(v_texCoords, v_glyphLoc);\n" +
                "  fragColor = v_color * vec4(1.0, 1.0, 1.0, coverage);\n" +
                "}";

        ShaderProgram shader = new ShaderProgram(vertexShader, fragmentShader);
        if (!shader.isCompiled()) throw new IllegalArgumentException("Error compiling shader: " + shader.getLog());
        return shader;
    }

    public void begin() {
        if (drawing) throw new IllegalStateException("SpriteBatch.end must be called before begin.");
        renderCalls = 0;

        Gdx.gl.glDepthMask(false);
        if (customShader != null)
            customShader.bind();
        else
            shader.bind();
        setupMatrices();

        drawing = true;
    }

    public void end() {
        if (!drawing) {
            throw new IllegalStateException("SpriteBatch.begin must be called before end.");
        }
        if (idx > 0) {
            flush();
        }
        lastAtlas = null;
        drawing = false;

        GL20 gl = Gdx.gl;
        gl.glDepthMask(true);
        if (isBlendingEnabled()) {
            gl.glDisable(GL20.GL_BLEND);
        }
    }

    public void setColor(Color tint) {
        color.set(tint);
        colorPacked = tint.toFloatBits();
    }

    public void setColor(float r, float g, float b, float a) {
        color.set(r, g, b, a);
        colorPacked = color.toFloatBits();
    }

    public Color getColor() {
        return color;
    }

    public void setPackedColor(float packedColor) {
        Color.abgr8888ToColor(color, packedColor);
        this.colorPacked = packedColor;
    }

    public float getPackedColor() {
        return colorPacked;
    }

    public void drawGlyph(GPUGlyph glyph, float size, int minX, int minY, int maxX, int maxY, float x, float y) {
        if (!drawing) {
            throw new IllegalStateException("SpriteBatch.begin must be called before draw.");
        }

        float[] vertices = this.vertices;

        GlyphAtlas atlas = glyph.atlas;
        int glyphLoc = glyph.location;

        if (atlas != lastAtlas || (lastGlyph != null && glyph.page != lastGlyph.page)) {
            switchAtlas(glyph);
        } else if (idx == vertices.length) {
            flush();
        }

        float scale = size / glyph.upem;
        final float fx1 = x + minX * scale;
        final float fy1 = y + minY * scale;
        final float fx2 = x + maxX * scale;
        final float fy2 = y + maxY * scale;
        final float u = minX;
        final float v = minY;
        final float u2 = maxX;
        final float v2 = maxY;

        float emPerPos = 1f / scale;

        float color = this.colorPacked;

        int idx = this.idx;
        vertices[idx] = fx1; // a_position
        vertices[idx + 1] = fy1; // a_position
        vertices[idx + 2] = u; // a_texCoords0
        vertices[idx + 3] = v; // a_texCoords0
        vertices[idx + 4] = color; // a_color
        vertices[idx + 5] = -1; // a_normal
        vertices[idx + 6] = 1; // a_normal
        vertices[idx + 7] = emPerPos; // a_emPerPos
        vertices[idx + 8] = Float.intBitsToFloat(glyphLoc); // a_glyphLoc

        vertices[idx + 9] = fx1; // a_position
        vertices[idx + 10] = fy2; // a_position
        vertices[idx + 11] = u; // a_texCoords0
        vertices[idx + 12] = v2; // a_texCoords0
        vertices[idx + 13] = color; // a_color
        vertices[idx + 14] = -1; // a_normal
        vertices[idx + 15] = -1; // a_normal
        vertices[idx + 16] = emPerPos; // a_emPerPos
        vertices[idx + 17] = Float.intBitsToFloat(glyphLoc); // a_glyphLoc

        vertices[idx + 18] = fx2; // a_position
        vertices[idx + 19] = fy2; // a_position
        vertices[idx + 20] = u2; // a_texCoords0
        vertices[idx + 21] = v2; // a_texCoords0
        vertices[idx + 22] = color; // a_color
        vertices[idx + 23] = 1; // a_normal
        vertices[idx + 24] = -1; // a_normal
        vertices[idx + 25] = emPerPos; // a_emPerPos
        vertices[idx + 26] = Float.intBitsToFloat(glyphLoc); // a_glyphLoc

        vertices[idx + 27] = fx2; // a_position
        vertices[idx + 28] = fy1; // a_position
        vertices[idx + 29] = u2; // a_texCoords0
        vertices[idx + 30] = v; // a_texCoords0
        vertices[idx + 31] = color; // a_color
        vertices[idx + 32] = 1; // a_normal
        vertices[idx + 33] = 1; // a_normal
        vertices[idx + 34] = emPerPos; // a_emPerPos
        vertices[idx + 35] = Float.intBitsToFloat(glyphLoc); // a_glyphLoc

        this.idx = idx + 36;
    }

    public void drawGlyph(GPUGlyph glyph, float size, float x, float y) {
        drawGlyph(glyph, size, glyph.minX, glyph.minY, glyph.maxX, glyph.maxY, x, y);
    }

    public void flush() {
        if (idx == 0) return;

        renderCalls++;
        totalRenderCalls++;
        int glyphsInBatch = idx / GLYPH_SIZE;
        if (glyphsInBatch > maxGlyphsInBatch) maxGlyphsInBatch = glyphsInBatch;
        int count = glyphsInBatch * 6;

        lastAtlas.bindGPU(lastGlyph);
        Mesh mesh = this.mesh;
        mesh.setVertices(vertices, 0, idx);

        // Only upload indices for the vertex array type
        if (currentDataType == Mesh.VertexDataType.VertexArray) {
            Buffer indicesBuffer = (Buffer) mesh.getIndicesBuffer(true);
            indicesBuffer.position(0);
            indicesBuffer.limit(count);
        }

        if (blendingDisabled) {
            Gdx.gl.glDisable(GL20.GL_BLEND);
        } else {
            Gdx.gl.glEnable(GL20.GL_BLEND);
            if (blendSrcFunc != -1)
                Gdx.gl.glBlendFuncSeparate(blendSrcFunc, blendDstFunc, blendSrcFuncAlpha, blendDstFuncAlpha);
        }

        mesh.render(customShader != null ? customShader : shader, GL20.GL_TRIANGLES, 0, count);

        idx = 0;
    }

    public void disableBlending() {
        if (blendingDisabled) return;
        flush();
        blendingDisabled = true;
    }

    public void enableBlending() {
        if (!blendingDisabled) return;
        flush();
        blendingDisabled = false;
    }

    public void setBlendFunction(int srcFunc, int dstFunc) {
        setBlendFunctionSeparate(srcFunc, dstFunc, srcFunc, dstFunc);
    }

    public void setBlendFunctionSeparate(int srcFuncColor, int dstFuncColor, int srcFuncAlpha, int dstFuncAlpha) {
        if (blendSrcFunc == srcFuncColor && blendDstFunc == dstFuncColor && blendSrcFuncAlpha == srcFuncAlpha
                && blendDstFuncAlpha == dstFuncAlpha) return;
        flush();
        blendSrcFunc = srcFuncColor;
        blendDstFunc = dstFuncColor;
        blendSrcFuncAlpha = srcFuncAlpha;
        blendDstFuncAlpha = dstFuncAlpha;
    }

    public int getBlendSrcFunc() {
        return blendSrcFunc;
    }

    public int getBlendDstFunc() {
        return blendDstFunc;
    }

    public int getBlendSrcFuncAlpha() {
        return blendSrcFuncAlpha;
    }

    public int getBlendDstFuncAlpha() {
        return blendDstFuncAlpha;
    }

    @Override
    public void dispose() {
        mesh.dispose();
        if (ownsShader && shader != null) shader.dispose();
    }

    public Matrix4 getProjectionMatrix() {
        return projectionMatrix;
    }

    public Matrix4 getTransformMatrix() {
        return transformMatrix;
    }

    public void setProjectionMatrix(Matrix4 projection) {
        if (drawing) flush();
        projectionMatrix.set(projection);
        if (drawing) setupMatrices();
    }

    public void setTransformMatrix(Matrix4 transform) {
        if (drawing) flush();
        transformMatrix.set(transform);
        if (drawing) setupMatrices();
    }

    protected void setupMatrices() {
        combinedMatrix.set(projectionMatrix).mul(transformMatrix);
        if (customShader != null) {
            customShader.setUniformMatrix("u_projTrans", combinedMatrix);
            customShader.setUniformf("u_viewport", new Vector2(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
            customShader.setUniformi("hb_gpu_atlas", 0);
        } else {
            shader.setUniformMatrix("u_projTrans", combinedMatrix);
            shader.setUniformf("u_viewport", new Vector2(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
            shader.setUniformi("hb_gpu_atlas", 0);
        }
    }

    protected void switchAtlas(GPUGlyph glyph) {
        flush();
        lastGlyph = glyph;
        lastAtlas = glyph.atlas;
    }

    public void setShader(ShaderProgram shader) {
        if (shader == customShader) // avoid unnecessary flushing in case we are drawing
            return;
        if (drawing) {
            flush();
        }
        customShader = shader;
        if (drawing) {
            if (customShader != null)
                customShader.bind();
            else
                this.shader.bind();
            setupMatrices();
        }
    }

    public ShaderProgram getShader() {
        if (customShader == null) {
            return shader;
        }
        return customShader;
    }

    public boolean isBlendingEnabled() {
        return !blendingDisabled;
    }

    public boolean isDrawing() {
        return drawing;
    }
}
