package ru.example.liquidglass.hud;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.texture.Sprite;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffectUtil;
import net.minecraft.text.Text;
import ru.example.liquidglass.render.ShaderRenderUtil;

public final class ActivePotionsHud {
    private static final int WIDTH = 180, HEADER = 28, ROW = 24;
    private ActivePotionsHud() {}

    public static void render(DrawContext context, RenderTickCounter tickCounter) {
        if (!HudModuleState.activePotionsEnabled && !HudEditor.isEditing()) return;
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.options.hudHidden) return;
        int count = Math.max(1, client.player.getStatusEffects().size());
        int x = HudLayout.potionsX;
        int y = HudLayout.potionsY;
        float scale = HudLayout.potionsScale;
        int height = HEADER + count * ROW + 6;
        ShaderRenderUtil.drawGlassPanel(context, x, y, Math.round(WIDTH * scale), Math.round(height * scale), 10.0f * scale);
        context.getMatrices().push();
        context.getMatrices().translate(x, y, 0.0f);
        context.getMatrices().scale(scale, scale, 1.0f);
        context.drawText(client.textRenderer, HudStyle.text("ACTIVE POTIONS"), 10, 8, HudStyle.WHITE, false);
        if (client.player.getStatusEffects().isEmpty()) {
            context.drawText(client.textRenderer, HudStyle.text("No active effects"), 31, HEADER + 1, HudStyle.MUTED, false);
            context.getMatrices().pop();
            return;
        }
        int row = 0;
        for (StatusEffectInstance effect : client.player.getStatusEffects()) {
            int rowY = HEADER + row * ROW;
            Sprite sprite = client.getStatusEffectSpriteManager().getSprite(effect.getEffectType());
            context.drawSpriteStretched(RenderLayer::getGuiTextured, sprite, 8, rowY, 18, 18);
            Text name = effect.getEffectType().value().getName();
            if (effect.getAmplifier() > 0) name = Text.literal(name.getString() + " " + (effect.getAmplifier() + 1));
            Text timer = StatusEffectUtil.getDurationText(effect, 1.0f, 20.0f);
            context.drawText(client.textRenderer, HudStyle.text(name.getString()), 31, rowY + 1, HudStyle.MUTED, false);
            context.drawText(client.textRenderer, HudStyle.text(timer.getString()), WIDTH - 48, rowY + 1, HudStyle.ACCENT, false);
            row++;
        }
        context.getMatrices().pop();
    }
}
