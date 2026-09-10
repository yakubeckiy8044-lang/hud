package ru.example.liquidglass.render;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import ru.example.liquidglass.LiquidGlassClient;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public final class ShaderRenderUtil {
    private static final Identifier VERTEX_ID = Identifier.of(LiquidGlassClient.MOD_ID, "shaders/core/liquid_glass.vsh");
    private static final Identifier FRAGMENT_ID = Identifier.of(LiquidGlassClient.MOD_ID, "shaders/core/liquid_glass.fsh");
    private static int program;
    private static int vao;
    private static int vbo;
    private static int backgroundTexture;
    private static int backgroundWidth;
    private static int backgroundHeight;
    private static boolean backgroundReady;
    private static boolean initialized;

    private ShaderRenderUtil() {}
    public static void register() {}

    /** Copies the current framebuffer once so the glass shader can blur the world behind it. */
    public static void beginFrame() {
        ensureInitialized();
        MinecraftClient client = MinecraftClient.getInstance();
        int width = client.getWindow().getFramebufferWidth();
        int height = client.getWindow().getFramebufferHeight();
        if (backgroundTexture == 0) backgroundTexture = GL11.glGenTextures();

        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, backgroundTexture);
        if (width != backgroundWidth || height != backgroundHeight) {
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
            GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, width, height, 0,
                    GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, (java.nio.ByteBuffer) null);
            backgroundWidth = width;
            backgroundHeight = height;
        }
        GL11.glCopyTexSubImage2D(GL11.GL_TEXTURE_2D, 0, 0, 0, 0, 0, width, height);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
        backgroundReady = true;
    }

    public static void endFrame() {
        backgroundReady = false;
    }

    private static void ensureInitialized() {
        if (initialized) return;
        RenderSystem.assertOnRenderThread();
        ResourceManager resources = MinecraftClient.getInstance().getResourceManager();
        int vertexShader = compile(GL20.GL_VERTEX_SHADER, readResource(resources, VERTEX_ID), VERTEX_ID.toString());
        int fragmentShader = compile(GL20.GL_FRAGMENT_SHADER, readResource(resources, FRAGMENT_ID), FRAGMENT_ID.toString());
        program = GL20.glCreateProgram();
        GL20.glAttachShader(program, vertexShader);
        GL20.glAttachShader(program, fragmentShader);
        GL20.glBindAttribLocation(program, 0, "Position");
        GL20.glBindAttribLocation(program, 1, "UV");
        GL20.glLinkProgram(program);
        if (GL20.glGetProgrami(program, GL20.GL_LINK_STATUS) == GL20.GL_FALSE) {
            throw new IllegalStateException("Liquid Glass shader link failed: " + GL20.glGetProgramInfoLog(program));
        }
        GL20.glDeleteShader(vertexShader);
        GL20.glDeleteShader(fragmentShader);
        vao = GL30.glGenVertexArrays();
        vbo = GL15.glGenBuffers();
        GL30.glBindVertexArray(vao);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
        GL20.glEnableVertexAttribArray(0);
        GL20.glVertexAttribPointer(0, 2, GL20.GL_FLOAT, false, 4 * Float.BYTES, 0L);
        GL20.glEnableVertexAttribArray(1);
        GL20.glVertexAttribPointer(1, 2, GL20.GL_FLOAT, false, 4 * Float.BYTES, 2L * Float.BYTES);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
        GL30.glBindVertexArray(0);
        initialized = true;
    }

    private static int compile(int type, String source, String name) {
        int shader = GL20.glCreateShader(type);
        GL20.glShaderSource(shader, source);
        GL20.glCompileShader(shader);
        if (GL20.glGetShaderi(shader, GL20.GL_COMPILE_STATUS) == GL20.GL_FALSE) {
            String log = GL20.glGetShaderInfoLog(shader);
            GL20.glDeleteShader(shader);
            throw new IllegalStateException(name + " compilation failed: " + log);
        }
        return shader;
    }

    private static String readResource(ResourceManager resources, Identifier id) {
        try (InputStream stream = resources.open(id)) {
            return new String(stream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException exception) {
            throw new IllegalStateException("Cannot read shader resource " + id, exception);
        }
    }

    public static void drawGlassPanel(DrawContext context, int x, int y, int width, int height, float radius) {
        ensureInitialized();
        MinecraftClient client = MinecraftClient.getInstance();
        float guiWidth = client.getWindow().getScaledWidth();
        float guiHeight = client.getWindow().getScaledHeight();
        float framebufferWidth = client.getWindow().getFramebufferWidth();
        float framebufferHeight = client.getWindow().getFramebufferHeight();
        float scaleX = framebufferWidth / guiWidth;
        float scaleY = framebufferHeight / guiHeight;
        float px = x * scaleX;
        float py = y * scaleY;
        float pw = width * scaleX;
        float ph = height * scaleY;
        float[] vertices = { px, py, 0, 0, px, py + ph, 0, 1, px + pw, py + ph, 1, 1, px + pw, py, 1, 0 };
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableCull();
        GL20.glUseProgram(program);
        GL20.glUniform2f(GL20.glGetUniformLocation(program, "ScreenSize"), framebufferWidth, framebufferHeight);
        GL20.glUniform4f(GL20.glGetUniformLocation(program, "Panel"), px, framebufferHeight - py - ph, pw, ph);
        GL20.glUniform1f(GL20.glGetUniformLocation(program, "Radius"), radius * Math.min(scaleX, scaleY));
        GL20.glUniform1i(GL20.glGetUniformLocation(program, "UseBackground"), backgroundReady ? 1 : 0);
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, backgroundReady ? backgroundTexture : 0);
        GL20.glUniform1i(GL20.glGetUniformLocation(program, "Background"), 0);
        GL30.glBindVertexArray(vao);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
        var buffer = org.lwjgl.BufferUtils.createFloatBuffer(vertices.length);
        buffer.put(vertices).flip();
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STREAM_DRAW);
        org.lwjgl.opengl.GL11.glDrawArrays(org.lwjgl.opengl.GL11.GL_TRIANGLE_FAN, 0, 4);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
        GL30.glBindVertexArray(0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
        GL20.glUseProgram(0);
        RenderSystem.enableCull();
        RenderSystem.disableBlend();
    }
}
