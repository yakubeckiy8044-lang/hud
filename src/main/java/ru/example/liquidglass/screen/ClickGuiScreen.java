package ru.example.liquidglass.screen;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import ru.example.liquidglass.ClientConfig;
import ru.example.liquidglass.hud.HudModuleState;
import ru.example.liquidglass.render.ShaderRenderUtil;

import java.util.List;

public final class ClickGuiScreen extends Screen {
    private static final int COLUMN_WIDTH = 150;
    private static final int COLUMN_HEIGHT = 350;
    private static final int COLUMN_GAP = 10;
    private static final int HEADER_HEIGHT = 44;
    private static final int ROW_HEIGHT = 24;
    private static final int ROW_MARGIN = 7;
    private static final int PANEL_RADIUS = 10;
    private static final int ACTIVE = 0x704B2A70;
    private static final int HOVER = 0x3A9885B8;
    private static final int TEXT = 0xFFF1EEF8;
    private static final int DISABLED = 0xFFB8B2C3;
    private static final int MUTED = 0xFF817A92;
    private static final int SEARCH_WIDTH = 154;
    private static final int SEARCH_HEIGHT = 26;
    private static final Identifier UI_FONT = Identifier.of("minecraft", "uniform");

    private static final List<Module> MODULES = List.of(
            new Module("Anti Bot", Category.COMBAT, ""),
            new Module("Attack Aura", Category.COMBAT),
            new Module("Auto Explosion", Category.COMBAT),
            new Module("Auto Swap", Category.COMBAT),
            new Module("Auto Totem", Category.COMBAT),
            new Module("Fast Bow", Category.COMBAT, ""),
            new Module("Hit Boxes", Category.COMBAT),
            new Module("No Friend Damage", Category.COMBAT, ""),
            new Module("No Player Trace", Category.COMBAT, ""),
            new Module("No Server Desync", Category.COMBAT, ""),
            new Module("No Velocity", Category.COMBAT),
            new Module("Trigger Bot", Category.COMBAT, ""),

            new Module("Air Jump", Category.MOVEMENT, ""),
            new Module("Dragon Flight", Category.MOVEMENT, ""),
            new Module("Elytra Flight", Category.MOVEMENT, ""),
            new Module("Flight", Category.MOVEMENT),
            new Module("Free Camera", Category.MOVEMENT, ""),
            new Module("Free Look", Category.MOVEMENT, ""),
            new Module("Gui Move", Category.MOVEMENT),
            new Module("No Jump Delay", Category.MOVEMENT),
            new Module("No Push", Category.MOVEMENT, ""),
            new Module("No Slow", Category.MOVEMENT, ""),
            new Module("Phase", Category.MOVEMENT, ""),
            new Module("Speed", Category.MOVEMENT, ""),

            new Module("Ambience", Category.VISUALS),
            new Module("Arrows", Category.VISUALS),
            new Module("Aspect Ratio", Category.VISUALS),
            new Module("Block ESP", Category.VISUALS),
            new Module("Full Bright", Category.VISUALS, ""),
            new Module("Armor HUD", Category.VISUALS, enabled -> ClientConfig.armorHudEnabled = enabled),
            new Module("Hotkeys HUD", Category.VISUALS, enabled -> HudModuleState.hotkeysEnabled = enabled),
            new Module("Active Potions", Category.VISUALS, enabled -> HudModuleState.activePotionsEnabled = enabled),
            new Module("Staff Online", Category.VISUALS, enabled -> HudModuleState.staffOnlineEnabled = enabled),
            new Module("Target HUD", Category.VISUALS, enabled -> HudModuleState.targetHudEnabled = enabled),
            new Module("Info HUD", Category.VISUALS, enabled -> HudModuleState.infoHudEnabled = enabled),
            new Module("Item Physics", Category.VISUALS),

            new Module("Anti AFK", Category.PLAYER, ""),
            new Module("Auto Armor", Category.PLAYER, "PREMIUM"),
            new Module("Auto Eat", Category.PLAYER, ""),
            new Module("Auto Eat Gapple", Category.PLAYER, ""),
            new Module("Auto Fish", Category.PLAYER, ""),
            new Module("Auto Myst", Category.PLAYER, "PREMIUM"),
            new Module("Auto Potion", Category.PLAYER, ""),
            new Module("Auto Respawn", Category.PLAYER),
            new Module("Auto Tool", Category.PLAYER),
            new Module("Auto Trade", Category.PLAYER, ""),
            new Module("Auto Transfer", Category.PLAYER),
            new Module("Blink", Category.PLAYER),

            new Module("Air Place", Category.MISC),
            new Module("Auto Leave", Category.MISC, ""),
            new Module("Auto Tpaccept", Category.MISC, ""),
            new Module("Discord Activity", Category.MISC),
            new Module("Funtime Helper", Category.MISC, "PREMIUM"),
            new Module("Inventory Plus", Category.MISC, ""),
            new Module("Multi Actions", Category.MISC, ""),
            new Module("Name Protect", Category.MISC, ""),
            new Module("Open Walls", Category.MISC, ""),
            new Module("Really World Helper", Category.MISC),
            new Module("Sounds", Category.MISC, "")
    );

    private int columnsX;
    private int columnsY;
    private int searchY;

    public ClickGuiScreen() {
        super(Text.literal("Liquid Glass"));
        setEnabled("Auto Totem", true);
        setEnabled("Gui Move", true);
        setEnabled("No Jump Delay", true);
        setEnabled("Discord Activity", true);
        setEnabled("Armor HUD", ClientConfig.armorHudEnabled);
        setEnabled("Hotkeys HUD", HudModuleState.hotkeysEnabled);
        setEnabled("Active Potions", HudModuleState.activePotionsEnabled);
        setEnabled("Staff Online", HudModuleState.staffOnlineEnabled);
        setEnabled("Target HUD", HudModuleState.targetHudEnabled);
        setEnabled("Info HUD", HudModuleState.infoHudEnabled);
    }

    @Override
    protected void init() {
        updateLayout();
    }

    private void updateLayout() {
        int totalWidth = Category.values().length * COLUMN_WIDTH + (Category.values().length - 1) * COLUMN_GAP;
        columnsX = Math.max(8, (width - totalWidth) / 2);
        columnsY = Math.max(18, (height - COLUMN_HEIGHT - SEARCH_HEIGHT - 14) / 2);
        searchY = columnsY + COLUMN_HEIGHT + 12;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        updateLayout();
        context.fill(0, 0, width, height, 0x57070612);
        for (Category category : Category.values()) {
            int x = columnsX + category.ordinal() * (COLUMN_WIDTH + COLUMN_GAP);
            drawCategory(context, category, x, columnsY, mouseX, mouseY);
        }
        drawSearch(context);
    }

    private void drawCategory(DrawContext context, Category category, int x, int y, int mouseX, int mouseY) {
        ShaderRenderUtil.drawGlassPanel(context, x, y, COLUMN_WIDTH, COLUMN_HEIGHT, PANEL_RADIUS);
        Text title = ui(category.title());
        int titleX = x + (COLUMN_WIDTH - textRenderer.getWidth(title)) / 2;
        context.drawText(textRenderer, title, titleX, y + 14, TEXT, false);
        context.fill(x + 12, y + HEADER_HEIGHT - 4, x + COLUMN_WIDTH - 12, y + HEADER_HEIGHT - 3, 0x382E3854);

        int row = 0;
        for (Module module : MODULES) {
            if (module.category() != category) continue;
            int rowX = x + ROW_MARGIN;
            int rowY = y + HEADER_HEIGHT + row * ROW_HEIGHT;
            int rowWidth = COLUMN_WIDTH - ROW_MARGIN * 2;
            boolean hovered = inside(mouseX, mouseY, rowX, rowY, rowWidth, ROW_HEIGHT - 2);
            if (module.enabled()) context.fill(rowX, rowY, rowX + rowWidth, rowY + ROW_HEIGHT - 2, ACTIVE);
            else if (hovered) context.fill(rowX, rowY, rowX + rowWidth, rowY + ROW_HEIGHT - 2, HOVER);

            int labelColor = module.enabled() ? TEXT : DISABLED;
            context.drawText(textRenderer, ui(module.name()), rowX + 7, rowY + 6, labelColor, false);
            if (!module.marker().isEmpty()) {
                Text marker = ui(module.marker());
                int markerX = rowX + rowWidth - textRenderer.getWidth(marker) - 7;
                context.drawText(textRenderer, marker, markerX, rowY + 6,
                        module.enabled() ? 0xFFE3B9FF : MUTED, false);
            }
            context.fill(rowX + 5, rowY + ROW_HEIGHT - 3, rowX + rowWidth - 5, rowY + ROW_HEIGHT - 2, 0x242A2940);
            row++;
        }
    }

    private void drawSearch(DrawContext context) {
        int x = (width - SEARCH_WIDTH) / 2;
        ShaderRenderUtil.drawGlassPanel(context, x, searchY, SEARCH_WIDTH, SEARCH_HEIGHT, 7.0f);
        context.drawBorder(x + 10, searchY + 7, 8, 8, 0xFFB4A9C5);
        context.fill(x + 17, searchY + 15, x + 21, searchY + 17, 0xFFB4A9C5);
        context.drawText(textRenderer, ui("Поиск"), x + 30, searchY + 8, 0xFFAAA2B8, false);
    }

    private Text ui(String value) {
        return Text.literal(value).fillStyle(Style.EMPTY.withFont(UI_FONT));
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button != 0) return super.mouseClicked(mouseX, mouseY, button);
        updateLayout();
        for (Category category : Category.values()) {
            int x = columnsX + category.ordinal() * (COLUMN_WIDTH + COLUMN_GAP);
            int row = 0;
            for (Module module : MODULES) {
                if (module.category() != category) continue;
                int rowX = x + ROW_MARGIN;
                int rowY = columnsY + HEADER_HEIGHT + row * ROW_HEIGHT;
                if (inside(mouseX, mouseY, rowX, rowY, COLUMN_WIDTH - ROW_MARGIN * 2, ROW_HEIGHT - 2)) {
                    module.toggle();
                    return true;
                }
                row++;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    private void setEnabled(String name, boolean value) {
        MODULES.stream().filter(module -> module.name().equals(name)).findFirst().ifPresent(module -> module.setEnabled(value));
    }

    private static boolean inside(double mouseX, double mouseY, int x, int y, int width, int height) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
