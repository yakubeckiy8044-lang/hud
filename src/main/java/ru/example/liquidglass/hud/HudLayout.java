package ru.example.liquidglass.hud;

/** Shared, screen-space layout for every HUD element. Values are intentionally
 * kept in memory for now; they can later be serialized to a config file. */
public final class HudLayout {
    private HudLayout() {}

    public static int infoX = 8;
    public static int infoY = 8;
    public static float infoScale = 1.0f;

    public static int armorX = 8;
    public static int armorY = 62;
    public static float armorScale = 1.0f;

    public static int hotkeysX = 8;
    public static int hotkeysY = 204;
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

    public static void resolveDefaults(int screenWidth, int screenHeight) {
        if (potionsX == Integer.MIN_VALUE) potionsX = screenWidth - 188;
        if (staffX == Integer.MIN_VALUE) staffX = screenWidth - 188;
        if (targetX == Integer.MIN_VALUE) targetX = (screenWidth - 190) / 2;
        if (targetY == Integer.MIN_VALUE) targetY = screenHeight - 82;
    }

    public static float clampScale(float scale) {
        return Math.max(0.65f, Math.min(2.0f, scale));
    }
}
