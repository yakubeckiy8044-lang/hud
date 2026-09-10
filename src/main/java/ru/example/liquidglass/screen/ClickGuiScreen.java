package ru.example.liquidglass.screen;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import ru.example.liquidglass.ClientConfig;
import ru.example.liquidglass.hud.HudModuleState;
import ru.example.liquidglass.render.ShaderRenderUtil;

import java.util.List;

public final class ClickGuiScreen extends Screen {
    private static final int COLUMN_WIDTH = 128;
    private static final int COLUMN_HEIGHT = 280;
    private static final int COLUMN_GAP = 12;
    private static final int HEADER_HEIGHT = 38;
    private static final int ROW_HEIGHT = 27;
    private static final int ROW_MARGIN = 8;
    private static final int PANEL_RADIUS = 12;
    private static final int ACCENT = 0xFF69E6D0;
    private static final int DISABLED = 0xFF9AA3B2;
    private static final int HOVER = 0x2638D8C8;

    private static final List<Module> MODULES = List.of(
            new Module("Armor HUD", Category.VISUALS, enabled -> ClientConfig.armorHudEnabled = enabled),
            new Module("Netherite Highlight", Category.VISUALS),
            new Module("Full Bright", Category.VISUALS),
            new Module("No Fire", Category.VISUALS),
            new Module("Hotkeys HUD", Category.VISUALS, enabled -> HudModuleState.hotkeysEnabled = enabled),
            new Module("Active Potions", Category.VISUALS, enabled -> HudModuleState.activePotionsEnabled = enabled),
            new Module("Staff Online", Category.VISUALS, enabled -> HudModuleState.staffOnlineEnabled = enabled),
            new Module("Target HUD", Category.VISUALS, enabled -> HudModuleState.targetHudEnabled = enabled),
            new Module("Anti-AFK", Category.PLAYER),
            new Module("Pearl Target", Category.PLAYER),
            new Module("Auto Respawn", Category.PLAYER),
            new Module("Auto Elytra", Category.PLAYER),
            new Module("Discord RPC", Category.MISC),
            new Module("Chat Helper", Category.MISC),
            new Module("Auto Accept TP", Category.MISC)
    );

    private int columnsX;
    private int columnsY;

    public ClickGuiScreen() {
        super(Text.literal("Liquid Glass"));
        MODULES.get(0).setEnabled(ClientConfig.armorHudEnabled);
        MODULES.get(4).setEnabled(HudModuleState.hotkeysEnabled);
        MODULES.get(5).setEnabled(HudModuleState.activePotionsEnabled);
        MODULES.get(6).setEnabled(HudModuleState.staffOnlineEnabled);
        MODULES.get(7).setEnabled(HudModuleState.targetHudEnabled);
    }

    @Override
    protected void init() { updateLayout(); }

    private void updateLayout() {
        int totalWidth = Category.values().length * COLUMN_WIDTH
                + (Category.values().length - 1) * COLUMN_GAP;
        columnsX = (width - totalWidth) / 2;
        columnsY = (height - COLUMN_HEIGHT) / 2;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        updateLayout();
        for (Category category : Category.values()) {
            int x = columnsX + category.ordinal() * (COLUMN_WIDTH + COLUMN_GAP);
            drawCategory(context, category, x, columnsY, mouseX, mouseY);
        }
    }

    private void drawCategory(DrawContext context, Category category, int x, int y, int mouseX, int mouseY) {
        ShaderRenderUtil.drawGlassPanel(context, x, y, COLUMN_WIDTH, COLUMN_HEIGHT, PANEL_RADIUS);
        String title = category.title();
        context.drawText(textRenderer, Text.literal(title),
                x + (COLUMN_WIDTH - textRenderer.getWidth(title)) / 2, y + 14, 0xFFFFFFFF, true);

        int row = 0;
        for (Module module : MODULES) {
            if (module.category() != category) continue;
            int rowX = x + ROW_MARGIN;
            int rowY = y + HEADER_HEIGHT + row * ROW_HEIGHT;
            int rowWidth = COLUMN_WIDTH - ROW_MARGIN * 2;
            int rowHeight = ROW_HEIGHT - 4;
            if (inside(mouseX, mouseY, rowX, rowY, rowWidth, rowHeight)) {
                context.fill(rowX, rowY, rowX + rowWidth, rowY + rowHeight, HOVER);
            }
            context.drawText(textRenderer, Text.literal(module.name()), rowX + 7, rowY + 5,
                    module.enabled() ? ACCENT : DISABLED, false);
            row++;
        }
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
                if (inside(mouseX, mouseY, rowX, rowY, COLUMN_WIDTH - ROW_MARGIN * 2, ROW_HEIGHT - 4)) {
                    module.toggle();
                    return true;
                }
                row++;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    private static boolean inside(double mouseX, double mouseY, int x, int y, int width, int height) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }

    @Override
    public boolean shouldPause() { return false; }
}


