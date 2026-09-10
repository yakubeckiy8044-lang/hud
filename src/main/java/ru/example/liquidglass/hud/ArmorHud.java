package ru.example.liquidglass.hud;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import ru.example.liquidglass.ClientConfig;
import ru.example.liquidglass.render.ShaderRenderUtil;

public final class ArmorHud {
    private ArmorHud() {}

    public static void render(DrawContext context, RenderTickCounter tickCounter) {
        if (!ClientConfig.armorHudEnabled) return;
        MinecraftClient client = MinecraftClient.getInstance();
        PlayerEntity player = client.player;
        if (player == null || client.options.hudHidden) return;

        int x = ClientConfig.armorHudX;
        int y = ClientConfig.armorHudY;
        ShaderRenderUtil.drawGlassPanel(context, x, y, ClientConfig.armorHudWidth, ClientConfig.armorHudHeight, ClientConfig.glassRadius);
        context.drawText(client.textRenderer, Text.literal("ARMOR"), x + 8, y + 5, 0xFFFFFFFF, true);

        int iconX = x + 8;
        for (ItemStack stack : player.getArmorItems()) {
            if (!stack.isEmpty()) {
                context.drawItem(stack, iconX, y + 17);
                int max = stack.getMaxDamage();
                int current = max - stack.getDamage();
                context.drawText(client.textRenderer, Text.literal(current + "/" + max), iconX + 19, y + 22, color(current, max), true);
            }
            iconX += 35;
        }
    }

    private static int color(int current, int max) {
        if (max <= 0) return 0xFFFFFFFF;
        float ratio = current / (float) max;
        return ratio <= .15f ? 0xFFFF5555 : ratio <= .40f ? 0xFFFFAA00 : 0xFF80FFB0;
    }
}
