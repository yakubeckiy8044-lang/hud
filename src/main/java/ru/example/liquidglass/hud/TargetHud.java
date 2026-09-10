package ru.example.liquidglass.hud;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.util.SkinTextures;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.hit.EntityHitResult;
import ru.example.liquidglass.render.ShaderRenderUtil;

public final class TargetHud {
    private static final int WIDTH = 190, HEIGHT = 48;
    private TargetHud() {}

    public static void render(DrawContext context, RenderTickCounter tickCounter) {
        if (!HudModuleState.targetHudEnabled) return;
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.options.hudHidden) return;
        if (!(client.crosshairTarget instanceof EntityHitResult hit)) return;
        if (!(hit.getEntity() instanceof PlayerEntity target)) return;
        int x = (client.getWindow().getScaledWidth() - WIDTH) / 2;
        int y = client.getWindow().getScaledHeight() - 82;
        ShaderRenderUtil.drawGlassPanel(context, x, y, WIDTH, HEIGHT, 10.0f);
        if (target instanceof AbstractClientPlayerEntity clientTarget) {
            SkinTextures skin = clientTarget.getSkinTextures();
            context.drawTexture(RenderLayer::getEntityTranslucent, skin.texture(), x + 8, y + 8,
                    8.0f, 8.0f, 18, 18, 64, 64);
        }
        context.drawText(client.textRenderer, target.getName(), x + 34, y + 8, 0xFFFFFFFF, true);
        float maxHealth = Math.max(1.0f, target.getMaxHealth());
        float health = Math.max(0.0f, Math.min(maxHealth, target.getHealth()));
        int barX = x + 34, barY = y + 27, barWidth = WIDTH - 46;
        int filled = Math.round(barWidth * health / maxHealth);
        context.fill(barX, barY, barX + barWidth, barY + 7, 0xFF283548);
        context.fill(barX, barY, barX + filled, barY + 7, healthColor(health / maxHealth));
        context.drawText(client.textRenderer, Text.literal(String.format("%.1f / %.1f HP", health, maxHealth)), barX, y + 36, 0xFFB8C6D8, false);
    }

    private static int healthColor(float ratio) {
        if (ratio <= 0.25f) return 0xFFE85C6A;
        if (ratio <= 0.55f) return 0xFFFFC15A;
        return 0xFF69E6D0;
    }
}