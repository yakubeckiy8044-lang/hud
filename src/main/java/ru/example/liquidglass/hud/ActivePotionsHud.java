package ru.example.liquidglass.hud;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.texture.Sprite;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffectUtil;
import net.minecraft.text.Text;
import ru.example.liquidglass.render.ShaderRenderUtil;

public final class ActivePotionsHud {
    private static final int WIDTH = 180, HEADER = 28, ROW = 24;
    private ActivePotionsHud() {}

    public static void render(DrawContext context, RenderTickCounter tickCounter) {
        if (!HudModuleState.activePotionsEnabled) return;
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.options.hudHidden) return;
        int count = client.player.getStatusEffects().size();
        if (count == 0) return;
        int x = client.getWindow().getScaledWidth() - WIDTH - HudModuleState.potionsMarginRight;
        int y = HudModuleState.potionsY;
        ShaderRenderUtil.drawGlassPanel(context, x, y, WIDTH, HEADER + count * ROW + 6, 10.0f);
        context.drawText(client.textRenderer, Text.literal("ACTIVE POTIONS"), x + 10, y + 8, 0xFFFFFFFF, true);
        int row = 0;
        for (StatusEffectInstance effect : client.player.getStatusEffects()) {
            int rowY = y + HEADER + row * ROW;
            Sprite sprite = client.getStatusEffectSpriteManager().getSprite(effect.getEffectType());
            context.drawSprite(x + 8, rowY, 0, 18, 18, sprite);
            Text name = effect.getEffectType().value().getName();
            if (effect.getAmplifier() > 0) name = Text.literal(name.getString() + " " + (effect.getAmplifier() + 1));
            Text timer = StatusEffectUtil.getDurationText(effect, 1.0f, 20.0f);
            context.drawText(client.textRenderer, name, x + 31, rowY + 1, 0xFFDCE7F0, false);
            context.drawText(client.textRenderer, timer, x + WIDTH - 48, rowY + 1, 0xFF69E6D0, false);
            row++;
        }
    }
}