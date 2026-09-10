package ru.example.liquidglass.render;

import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
public final class FontManager {
    // The default atlas is wider and easier to read at compact GUI scales
    // than the monospaced uniform provider. It also has stable Latin and
    // Cyrillic glyphs on every vanilla 1.21.4 client.
    public static final Identifier UI_FONT = Identifier.of("minecraft", "default");

    private FontManager() {
    }

    public static Text text(String value) {
        return Text.literal(value).fillStyle(Style.EMPTY.withFont(UI_FONT));
    }

    public static Text title(String value) {
        return Text.literal(value).fillStyle(Style.EMPTY.withFont(UI_FONT).withBold(true));
    }
}
