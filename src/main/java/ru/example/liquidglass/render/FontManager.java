package ru.example.liquidglass.render;

import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import ru.example.liquidglass.LiquidGlassClient;

public final class FontManager {
    public static final Identifier UI_FONT = Identifier.of(LiquidGlassClient.MOD_ID, "calibri");

    private FontManager() {
    }

    public static Text text(String value) {
        return Text.literal(value).fillStyle(Style.EMPTY.withFont(UI_FONT));
    }

    public static Text title(String value) {
        return Text.literal(value).fillStyle(Style.EMPTY.withFont(UI_FONT).withBold(true));
    }
}
