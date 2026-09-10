package ru.example.liquidglass.screen;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import ru.example.liquidglass.ClientConfig;
import ru.example.liquidglass.render.ShaderRenderUtil;

public final class ClickGuiScreen extends Screen {
    private static final int PANEL_W = 360;
    private static final int PANEL_H = 230;
    private static final int SLIDER_W = 270;
    private static final int INNER_X = 25;
    private static final int HEADER_H = 58;
    private static final int CHECK_Y = 78;
    private static final int X_LABEL_Y = 112;
    private static final int X_SLIDER_Y = 132;
    private static final int Y_LABEL_Y = 165;
    private static final int Y_SLIDER_Y = 185;

    private int panelX;
    private int panelY;
    private int activeSlider = 0;

    public ClickGuiScreen() {
        super(Text.literal("Liquid Glass"));
    }

    @Override
    protected void init() {
        panelX = (width - PANEL_W) / 2;
        panelY = (height - PANEL_H) / 2;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        panelX = (width - PANEL_W) / 2;
        panelY = (height - PANEL_H) / 2;

        ShaderRenderUtil.drawGlassPanel(context, panelX, panelY, PANEL_W, PANEL_H, 16.0f);

        int left = panelX + INNER_X;
        context.drawText(textRenderer, Text.literal("LIQUID GLASS"), left, panelY + 15, 0xFFFFFFFF, true);
        context.drawText(textRenderer, Text.literal("Visual settings"), left, panelY + 38, 0xFFB7C8E8, false);

        int checkX = left;
        context.fill(checkX, panelY + CHECK_Y, checkX + 14, panelY + CHECK_Y + 14,
                ClientConfig.armorHudEnabled ? 0xFF6DE6A0 : 0xFF555D70);
        context.drawText(textRenderer, Text.literal("Armor HUD"), checkX + 23, panelY + CHECK_Y + 2, 0xFFFFFFFF, false);

        int sliderX = left;
        int maxX = Math.max(0, width - ClientConfig.armorHudWidth);
        int maxY = Math.max(0, height - ClientConfig.armorHudHeight);

        context.drawText(textRenderer, Text.literal("Armor HUD X: " + ClientConfig.armorHudX), sliderX, panelY + X_LABEL_Y, 0xFFFFFFFF, false);
        drawSlider(context, sliderX, panelY + X_SLIDER_Y, ClientConfig.armorHudX, 0, maxX);

        context.drawText(textRenderer, Text.literal("Armor HUD Y: " + ClientConfig.armorHudY), sliderX, panelY + Y_LABEL_Y, 0xFFFFFFFF, false);
        drawSlider(context, sliderX, panelY + Y_SLIDER_Y, ClientConfig.armorHudY, 0, maxY);
    }

    private void drawSlider(DrawContext context, int x, int y, int value, int min, int max) {
        context.fill(x, y, x + SLIDER_W, y + 4, 0xFF343B4D);
        float normalized = max <= min ? 0.0f : (value - min) / (float) (max - min);
        int knobX = x + Math.round(normalized * SLIDER_W);
        context.fill(x, y, knobX, y + 4, 0xFF72CFFF);
        context.fill(knobX - 4, y - 4, knobX + 4, y + 8, 0xFFFFFFFF);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button != 0) return super.mouseClicked(mouseX, mouseY, button);
        int left = panelX + INNER_X;
        if (inside(mouseX, mouseY, left, panelY + CHECK_Y, 14, 14)) {
            ClientConfig.armorHudEnabled = !ClientConfig.armorHudEnabled;
            return true;
        }
        if (inside(mouseX, mouseY, left, panelY + X_SLIDER_Y - 6, SLIDER_W, 16)) {
            activeSlider = 1;
            updateSlider(mouseX, true);
            return true;
        }
        if (inside(mouseX, mouseY, left, panelY + Y_SLIDER_Y - 6, SLIDER_W, 16)) {
            activeSlider = 2;
            updateSlider(mouseX, false);
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (button != 0 || activeSlider == 0) return false;
        updateSlider(mouseX, activeSlider == 1);
        return true;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0) activeSlider = 0;
        return super.mouseReleased(mouseX, mouseY, button);
    }

    private void updateSlider(double mouseX, boolean xSlider) {
        int min = 0;
        int max = xSlider
                ? Math.max(0, width - ClientConfig.armorHudWidth)
                : Math.max(0, height - ClientConfig.armorHudHeight);
        int value = min + (int) Math.round(Math.max(0.0, Math.min(1.0,
                (mouseX - (panelX + INNER_X)) / (double) SLIDER_W)) * (max - min));
        if (xSlider) ClientConfig.armorHudX = value;
        else ClientConfig.armorHudY = value;
    }

    private static boolean inside(double mx, double my, int x, int y, int w, int h) {
        return mx >= x && mx <= x + w && my >= y && my <= y + h;
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}