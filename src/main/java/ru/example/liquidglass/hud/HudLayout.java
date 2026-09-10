package ru.example.liquidglass.hud;

import net.minecraft.client.MinecraftClient;

/** Shared, screen-space layout for every HUD element. Values are intentionally
 * kept in memory for now; they can later be serialized to a config file. */
public final class HudLayout {
    private HudLayout() {}

    public static int infoX = 3;
    public static int infoY = 3;
    public static float infoScale = 1.0f;

    public static int armorX = 3;
    public static int armorY = 52;
    public static float armorScale = 1.0f;

    public static int hotkeysX = 3;
    public static int hotkeysY = 154;
    public static float hotkeysScale = 1.0f;

    public static int potionsX = Integer.MIN_VALUE;
    public static int potionsY = 8;
    public static float potionsScale = 1.0f;

    public static int staffX = Integer.MIN_VALUE;
    public static int staffY = 112;
    public static float staffScale = 1.0f;

    public static int targetX = Integer.MIN_VALUE;
    public static int targetY = Integer.MIN_VALUE;
    public static float targetScale = 1.0f;

    public static int cooldownsX = 3;
    public static int cooldownsY = 245;
    public static float cooldownsScale = 1.0f;

    public static void resolveDefaults(int screenWidth, int screenHeight) {
        if (potionsX == Integer.MIN_VALUE) potionsX = screenWidth - 188;
        if (staffX == Integer.MIN_VALUE) staffX = screenWidth - 188;
        if (targetX == Integer.MIN_VALUE) targetX = (screenWidth - 190) / 2;
        if (targetY == Integer.MIN_VALUE) targetY = screenHeight - 82;
    }

    public static void resolveDefaults(MinecraftClient client) {
        float scale = guiScale(client);
        int margin = Math.max(2, Math.round(8.0f / scale));
        int rightWidth = Math.max(1, Math.round(180.0f / scale));
        int targetWidth = Math.max(1, Math.round(190.0f / scale));
        int targetHeight = Math.max(1, Math.round(48.0f / scale));
        if (potionsX == Integer.MIN_VALUE) potionsX = client.getWindow().getScaledWidth() - rightWidth - margin;
        if (staffX == Integer.MIN_VALUE) staffX = client.getWindow().getScaledWidth() - rightWidth - margin;
        if (targetX == Integer.MIN_VALUE) targetX = (client.getWindow().getScaledWidth() - targetWidth) / 2;
        if (targetY == Integer.MIN_VALUE) targetY = client.getWindow().getScaledHeight() - targetHeight - margin;
    }

    public static float renderScale(MinecraftClient client, float userScale) {
        return Math.max(0.25f, userScale / guiScale(client));
    }

    public static float guiScale(MinecraftClient client) {
        return Math.max(1.0f, client.getWindow().getFramebufferWidth()
                / (float) Math.max(1, client.getWindow().getScaledWidth()));
    }

    public static float clampScale(float scale) {
        return Math.max(0.65f, Math.min(2.0f, scale));
    }
}
