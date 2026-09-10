package ru.example.liquidglass.screen;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import ru.example.liquidglass.ClientConfig;
import ru.example.liquidglass.render.ShaderRenderUtil;

public final class ClickGuiScreen extends Screen {
    private static final int PANEL_X = 80, PANEL_Y = 45, PANEL_W = 360, PANEL_H = 230;
    private static final int SLIDER_X = 105, SLIDER_W = 270;

    public ClickGuiScreen() { super(Text.literal("Liquid Glass")); }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        ShaderRenderUtil.drawGlassPanel(context, PANEL_X, PANEL_Y, PANEL_W, PANEL_H, 16);
        context.drawText(textRenderer, Text.literal("LIQUID GLASS"), PANEL_X + 20, PANEL_Y + 15, 0xFFFFFFFF, true);
        context.drawText(textRenderer, Text.literal("Visual settings"), PANEL_X + 32, PANEL_Y + 53, 0xFFB7C8E8, false);
        context.fill(105, 100, 119, 114, ClientConfig.armorHudEnabled ? 0xFF6DE6A0 : 0xFF555D70);
        context.drawText(textRenderer, Text.literal("Armor HUD"), 128, 102, 0xFFFFFFFF, false);
        context.drawText(textRenderer, Text.literal("Armor HUD X: " + ClientConfig.armorHudX), SLIDER_X, 135, 0xFFFFFFFF, false);
        drawSlider(context, 150, ClientConfig.armorHudX, 0, Math.max(0, width - ClientConfig.armorHudWidth));
        context.drawText(textRenderer, Text.literal("Armor HUD Y: " + ClientConfig.armorHudY), SLIDER_X, 180, 0xFFFFFFFF, false);
        drawSlider(context, 195, ClientConfig.armorHudY, 0, Math.max(0, height - ClientConfig.armorHudHeight));
    }

    private void drawSlider(DrawContext context, int y, int value, int min, int max) {
        context.fill(SLIDER_X, y, SLIDER_X + SLIDER_W, y + 4, 0xFF343B4D);
        float normalized = max == min ? 0 : (value - min) / (float) (max - min);
        int knob = SLIDER_X + Math.round(normalized * SLIDER_W);
        context.fill(SLIDER_X, y, knob, y + 4, 0xFF72CFFF);
        context.fill(knob - 4, y - 4, knob + 4, y + 8, 0xFFFFFFFF);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button != 0) return super.mouseClicked(mouseX, mouseY, button);
        if (inside(mouseX, mouseY, 105, 100, 14, 14)) {
            ClientConfig.armorHudEnabled = !ClientConfig.armorHudEnabled;
            return true;
        }
        if (mouseY >= 145 && mouseY <= 165) {
            ClientConfig.armorHudX = sliderValue(mouseX, 0, Math.max(0, width - ClientConfig.armorHudWidth));
            return true;
        }
        if (mouseY >= 190 && mouseY <= 210) {
            ClientConfig.armorHudY = sliderValue(mouseX, 0, Math.max(0, height - ClientConfig.armorHudHeight));
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (button != 0) return false;
        if (mouseY >= 145 && mouseY <= 165) {
            ClientConfig.armorHudX = sliderValue(mouseX, 0, Math.max(0, width - ClientConfig.armorHudWidth));
            return true;
        }
        if (mouseY >= 190 && mouseY <= 210) {
            ClientConfig.armorHudY = sliderValue(mouseX, 0, Math.max(0, height - ClientConfig.armorHudHeight));
            return true;
        }
        return false;
    }

    private int sliderValue(double mouseX, int min, int max) {
        double normalized = Math.max(0, Math.min(1, (mouseX - SLIDER_X) / SLIDER_W));
        return min + (int) Math.round(normalized * (max - min));
    }

    private static boolean inside(double mx, double my, int x, int y, int w, int h) {
        return mx >= x && mx <= x + w && my >= y && my <= y + h;
    }

    @Override
    public boolean shouldPause() { return false; }
}
