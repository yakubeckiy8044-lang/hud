package ru.example.liquidglass.hud;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import ru.example.liquidglass.screen.ClickGuiScreen;

public final class VisualHud {
    private VisualHud() {}

    public static void render(DrawContext context, RenderTickCounter tickCounter) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.currentScreen instanceof ClickGuiScreen) return;
        HudLayout.resolveDefaults(client.getWindow().getScaledWidth(), client.getWindow().getScaledHeight());
        ArchiveHud.render(context, tickCounter);
    }
}
