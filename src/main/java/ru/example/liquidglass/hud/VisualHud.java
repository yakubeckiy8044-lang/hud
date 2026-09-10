package ru.example.liquidglass.hud;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;

public final class VisualHud {
    private VisualHud() {}

    public static void render(DrawContext context, RenderTickCounter tickCounter) {
        ArmorHud.render(context, tickCounter);
        HotkeysHud.render(context, tickCounter);
        ActivePotionsHud.render(context, tickCounter);
        StaffOnlineHud.render(context, tickCounter);
        TargetHud.render(context, tickCounter);
    }
}