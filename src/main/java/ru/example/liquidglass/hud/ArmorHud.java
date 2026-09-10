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
    public static final int WIDTH = 112;
    public static final int HEIGHT = 132;

    private ArmorHud() {}

    public static void render(DrawContext context, RenderTickCounter tickCounter) {
        if (!ClientConfig.armorHudEnabled && !HudEditor.isEditing()) return;
        MinecraftClient client = MinecraftClient.getInstance();
        PlayerEntity player = client.player;
        if (player == null || client.options.hudHidden) return;

        int x = HudLayout.armorX;
        int y = HudLayout.armorY;
        float scale = HudLayout.armorScale;
        ShaderRenderUtil.drawGlassPanel(context, x, y, Math.round(WIDTH * scale), Math.round(HEIGHT * scale), 10.0f * scale);

        context.getMatrices().push();
        context.getMatrices().translate(x, y, 0.0f);
        context.getMatrices().scale(scale, scale, 1.0f);
        context.drawText(client.textRenderer, HudStyle.text("ARMOR"), 10, 8, HudStyle.WHITE, false);
        context.fill(10, 24, WIDTH - 10, 25, 0x556B7B92);

        int row = 0;
        for (ItemStack stack : player.getArmorItems()) {
            int rowY = 30 + row * 24;
            if (!stack.isEmpty()) {
                context.drawItem(stack, 8, rowY);
                int max = stack.getMaxDamage();
                int current = max - stack.getDamage();
                String name = stack.getName().getString();
                if (name.length() > 11) name = name.substring(0, 11);
                context.drawText(client.textRenderer, HudStyle.text(name), 34, rowY + 1, HudStyle.MUTED, false);
                context.drawText(client.textRenderer, HudStyle.text(current + "/" + max), 34, rowY + 12, color(current, max), false);
            } else {
                context.drawText(client.textRenderer, HudStyle.text("EMPTY"), 34, rowY + 6, 0xFF66748B, false);
            }
            row++;
        }
        context.getMatrices().pop();
    }

    private static int color(int current, int max) {
        if (max <= 0) return HudStyle.WHITE;
        float ratio = current / (float) max;
        return ratio <= .15f ? HudStyle.DANGER : ratio <= .40f ? 0xFFFFB45E : 0xFF8EF7BA;
    }
}
