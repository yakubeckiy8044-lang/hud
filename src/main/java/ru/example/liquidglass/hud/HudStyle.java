package ru.example.liquidglass.hud;

import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

/** A clean, compact UI style based on Minecraft's bundled uniform font. */
public final class HudStyle {
    public static final int WHITE = 0xFFF4F7FF;
    public static final int MUTED = 0xFFB7C1D4;
    public static final int ACCENT = 0xFF72E5D2;
    public static final int DANGER = 0xFFFF7184;
    public static final Identifier FONT = Identifier.of("minecraft", "uniform");

    private HudStyle() {}

    public static Text text(String value) {
        return Text.literal(value).fillStyle(Style.EMPTY.withFont(FONT));
    }
}
