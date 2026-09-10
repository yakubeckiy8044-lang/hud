package ru.example.liquidglass.hud;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.text.Text;
import ru.example.liquidglass.render.ShaderRenderUtil;

public final class HotkeysHud {
    private static final int PANEL_WIDTH = 154, PANEL_HEIGHT = 82, KEY_W = 42, KEY_H = 20, GAP = 4;
    private HotkeysHud() {}

    public static void render(DrawContext context, RenderTickCounter tickCounter) {
        if (!HudModuleState.hotkeysEnabled && !HudEditor.isEditing()) return;
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.options.hudHidden) return;
        int x = HudLayout.hotkeysX, y = HudLayout.hotkeysY;
        float scale = HudLayout.hotkeysScale;
        ShaderRenderUtil.drawGlassPanel(context, x, y, Math.round(PANEL_WIDTH * scale), Math.round(PANEL_HEIGHT * scale), 10.0f * scale);
        context.getMatrices().push();
        context.getMatrices().translate(x, y, 0.0f);
        context.getMatrices().scale(scale, scale, 1.0f);
        context.drawText(client.textRenderer, HudStyle.text("HOTKEYS"), 10, 8, HudStyle.WHITE, false);
        int sx = 10, sy = 28;
        drawKey(context, client, "W", sx + KEY_W + GAP, sy, client.options.forwardKey.isPressed());
        drawKey(context, client, "A", sx, sy + KEY_H + GAP, client.options.leftKey.isPressed());
        drawKey(context, client, "S", sx + KEY_W + GAP, sy + KEY_H + GAP, client.options.backKey.isPressed());
        drawKey(context, client, "D", sx + (KEY_W + GAP) * 2, sy + KEY_H + GAP, client.options.rightKey.isPressed());
        drawKey(context, client, "LMB", sx + (KEY_W + GAP) * 3, sy, client.options.attackKey.isPressed());
        drawKey(context, client, "RMB", sx + (KEY_W + GAP) * 3, sy + KEY_H + GAP, client.options.useKey.isPressed());
        context.getMatrices().pop();
    }

    private static void drawKey(DrawContext context, MinecraftClient client, String label, int x, int y, boolean pressed) {
        context.fill(x, y, x + KEY_W, y + KEY_H, pressed ? 0xCC4FE0C7 : 0x553F5269);
        int textX = x + (KEY_W - client.textRenderer.getWidth(HudStyle.text(label))) / 2;
        context.drawText(client.textRenderer, HudStyle.text(label), textX, y + 6, pressed ? 0xFF081B20 : HudStyle.MUTED, false);
    }
}
