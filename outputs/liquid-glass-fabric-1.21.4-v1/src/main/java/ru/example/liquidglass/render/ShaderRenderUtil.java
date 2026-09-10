package ru.example.liquidglass.render;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import ru.example.liquidglass.LiquidGlassClient;

public final class ShaderRenderUtil {
    private static final Identifier SHADER_ID = Identifier.of(LiquidGlassClient.MOD_ID, "liquid_glass");
    private static ShaderProgram shader;

    private ShaderRenderUtil() {}

    public static void register() {}

    private static void ensureShader() {
        if (shader != null) return;
        try {
            shader = new ShaderProgram(
                    MinecraftClient.getInstance().getResourceManager(),
                    SHADER_ID.toString(),
                    VertexFormats.POSITION_TEXTURE_COLOR
            );
        } catch (Exception e) {
            throw new IllegalStateException("Unable to load " + SHADER_ID, e);
        }
    }

    public static void drawGlassPanel(DrawContext context, int x, int y, int width, int height, float radius) {
        ensureShader();
        MinecraftClient client = MinecraftClient.getInstance();
        int screenWidth = client.getWindow().getScaledWidth();
        int screenHeight = client.getWindow().getScaledHeight();
        shader.getUniformOrDefault("Size").set((float) screenWidth, (float) screenHeight);
        shader.getUniformOrDefault("Panel").set((float) x, (float) y, (float) width, (float) height);
        shader.getUniformOrDefault("Radius").set(radius);
        shader.getUniformOrDefault("BlurStrength").set(1.0f);

        MatrixStack.Entry entry = context.getMatrices().peek();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableCull();
        RenderSystem.setShader(() -> shader);

        BufferBuilder buffer = Tessellator.getInstance().begin(
                VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
        buffer.vertex(entry.getPositionMatrix(), x, y, 0).texture(0, 0).color(255, 255, 255, 255);
        buffer.vertex(entry.getPositionMatrix(), x, y + height, 0).texture(0, 1).color(255, 255, 255, 255);
        buffer.vertex(entry.getPositionMatrix(), x + width, y + height, 0).texture(1, 1).color(255, 255, 255, 255);
        buffer.vertex(entry.getPositionMatrix(), x + width, y, 0).texture(1, 0).color(255, 255, 255, 255);
        BufferRenderer.drawWithGlobalProgram(buffer.end());

        RenderSystem.enableCull();
        RenderSystem.disableBlend();
    }
}
