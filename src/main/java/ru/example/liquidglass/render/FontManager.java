package ru.example.liquidglass.render;

import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
public final class FontManager {
    // Use Minecraft's own atlas for stable Latin/Cyrillic glyphs. A custom
    // TTF provider can fall back to tofu glyphs on clients where it fails to
    // load, while the bundled uniform provider is always available.
    public static final Identifier UI_FONT = Identifier.of("minecraft", "uniform");

    private FontManager() {
    }

    public static Text text(String value) {
        return Text.literal(value).fillStyle(Style.EMPTY.withFont(UI_FONT));
    }

    public static Text title(String value) {
        return Text.literal(value).fillStyle(Style.EMPTY.withFont(UI_FONT).withBold(true));
    }
}
