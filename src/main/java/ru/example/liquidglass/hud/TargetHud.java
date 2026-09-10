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
        if (!HudModuleState.targetHudEnabled && !HudEditor.isEditing()) return;
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.options.hudHidden) return;
        PlayerEntity target = null;
        if (client.crosshairTarget instanceof EntityHitResult hit && hit.getEntity() instanceof PlayerEntity playerTarget) {
            target = playerTarget;
        } else if (HudEditor.isEditing()) {
            target = client.player;
        }
        if (target == null) return;
        int x = HudLayout.targetX;
        int y = HudLayout.targetY;
        float scale = HudLayout.targetScale;
        ShaderRenderUtil.drawGlassPanel(context, x, y, Math.round(WIDTH * scale), Math.round(HEIGHT * scale), 10.0f * scale);
        context.getMatrices().push();
        context.getMatrices().translate(x, y, 0.0f);
        context.getMatrices().scale(scale, scale, 1.0f);
        if (target instanceof AbstractClientPlayerEntity clientTarget) {
            SkinTextures skin = clientTarget.getSkinTextures();
            context.drawTexture(RenderLayer::getEntityTranslucent, skin.texture(), 8, 8,
                    8.0f, 8.0f, 18, 18, 64, 64);
        }
        context.drawText(client.textRenderer, HudStyle.text(target.getName().getString()), 34, 8, HudStyle.WHITE, false);
        float maxHealth = Math.max(1.0f, target.getMaxHealth());
        float health = Math.max(0.0f, Math.min(maxHealth, target.getHealth()));
        int barX = x + 34, barY = y + 27, barWidth = WIDTH - 46;
        int localBarX = 34, localBarY = 27;
        int filled = Math.round((WIDTH - 46) * health / maxHealth);
        context.fill(localBarX, localBarY, localBarX + barWidth, localBarY + 7, 0xFF283548);
        context.fill(localBarX, localBarY, localBarX + filled, localBarY + 7, healthColor(health / maxHealth));
        context.drawText(client.textRenderer, HudStyle.text(String.format("%.1f / %.1f HP", health, maxHealth)), 34, 36, HudStyle.MUTED, false);
        context.getMatrices().pop();
    }

    private static int healthColor(float ratio) {
        if (ratio <= 0.25f) return 0xFFE85C6A;
        if (ratio <= 0.55f) return 0xFFFFC15A;
        return 0xFF69E6D0;
    }
}
