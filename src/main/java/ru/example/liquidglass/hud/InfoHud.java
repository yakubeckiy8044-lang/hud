package ru.example.liquidglass.hud;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.entity.player.PlayerEntity;
import ru.example.liquidglass.render.ShaderRenderUtil;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/** Compact top-left status panel inspired by the supplied reference. */
public final class InfoHud {
    public static final int WIDTH = 202;
    public static final int HEIGHT = 44;
    private static final DateTimeFormatter CLOCK = DateTimeFormatter.ofPattern("HH:mm:ss");

    private InfoHud() {}

    public static void render(DrawContext context, RenderTickCounter tickCounter) {
        if (!HudModuleState.infoHudEnabled && !HudEditor.isEditing()) return;
        MinecraftClient client = MinecraftClient.getInstance();
        PlayerEntity player = client.player;
        if (player == null || client.options.hudHidden) return;

        int x = HudLayout.infoX;
        int y = HudLayout.infoY;
        float scale = HudLayout.infoScale;
        drawPanel(context, x, y, WIDTH, HEIGHT, scale);

        context.getMatrices().push();
        context.getMatrices().translate(x, y, 0.0f);
        context.getMatrices().scale(scale, scale, 1.0f);

        int ping = 0;
        if (client.getNetworkHandler() != null) {
            PlayerListEntry entry = client.getNetworkHandler().getPlayerListEntry(player.getUuid());
            if (entry != null) ping = Math.max(0, entry.getLatency());
        }
        String first = "LIQUID GLASS   " + client.getCurrentFps() + " FPS   " + LocalTime.now().format(CLOCK);
        String second = String.format("%d %d %d   %d PING",
                (int) Math.floor(player.getX()), (int) Math.floor(player.getY()),
                (int) Math.floor(player.getZ()), ping);
        context.drawText(client.textRenderer, HudStyle.text(first), 10, 8, HudStyle.WHITE, false);
        context.drawText(client.textRenderer, HudStyle.text(second), 10, 25, HudStyle.MUTED, false);
        context.getMatrices().pop();
    }

    private static void drawPanel(DrawContext context, int x, int y, int width, int height, float scale) {
        ShaderRenderUtil.drawGlassPanel(context, x, y,
                Math.round(width * scale), Math.round(height * scale), 9.0f * scale);
    }
}
