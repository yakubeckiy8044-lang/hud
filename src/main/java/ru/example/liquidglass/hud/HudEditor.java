package ru.example.liquidglass.hud;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ChatScreen;
import org.lwjgl.glfw.GLFW;

/** In-chat layout editor: left-drag a widget, scroll over it to resize it. */
public final class HudEditor {
    private static Element selected;
    private static double grabX;
    private static double grabY;

    private HudEditor() {}

    public static boolean isEditing() {
        return MinecraftClient.getInstance().currentScreen instanceof ChatScreen;
    }

    public static void mouseClicked(double mouseX, double mouseY, int button) {
        if (!isEditing() || button != GLFW.GLFW_MOUSE_BUTTON_1) return;
        selected = find(mouseX, mouseY);
        if (selected != null) {
            grabX = mouseX - selected.x();
            grabY = mouseY - selected.y();
        }
    }

    public static void mouseReleased(int button) {
        if (button == GLFW.GLFW_MOUSE_BUTTON_1) selected = null;
    }

    public static void mouseScrolled(double mouseX, double mouseY, double verticalAmount) {
        if (!isEditing() || verticalAmount == 0.0) return;
        Element element = selected != null ? selected : find(mouseX, mouseY);
        if (element == null) return;
        element.setScale(HudLayout.clampScale(element.scale() + (float) verticalAmount * 0.08f));
    }

    public static void renderOverlay(DrawContext context, int mouseX, int mouseY) {
        if (!isEditing()) return;
        updateDrag(mouseX, mouseY);
        MinecraftClient client = MinecraftClient.getInstance();

        for (Element element : Element.values()) {
            int color = element == selected ? 0xFF72E5D2 : 0xBFFFFFFF;
            context.drawBorder(element.x(), element.y(), element.width(), element.height(), color);
            context.drawText(client.textRenderer, HudStyle.text(element.label()),
                    element.x() + 4, Math.max(2, element.y() - 11), color, false);
        }
    }

    private static void updateDrag(int mouseX, int mouseY) {
        if (selected == null) return;
        MinecraftClient client = MinecraftClient.getInstance();
        if (GLFW.glfwGetMouseButton(client.getWindow().getHandle(), GLFW.GLFW_MOUSE_BUTTON_1)
                != GLFW.GLFW_PRESS) {
            selected = null;
            return;
        }
        selected.setPosition((int) Math.round(mouseX - grabX), (int) Math.round(mouseY - grabY));
    }

    private static Element find(double mouseX, double mouseY) {
        Element[] elements = Element.values();
        for (int i = elements.length - 1; i >= 0; i--) {
            Element element = elements[i];
            if (element.contains(mouseX, mouseY)) return element;
        }
        return null;
    }

    private enum Element {
        INFO("INFO", 202, 44) {
            int x() { return HudLayout.infoX; }
            int y() { return HudLayout.infoY; }
            float scale() { return HudLayout.infoScale; }
            void setPosition(int x, int y) { HudLayout.infoX = x; HudLayout.infoY = y; }
            void setScale(float value) { HudLayout.infoScale = value; }
        },
        ARMOR("ARMOR", 112, 132) {
            int x() { return HudLayout.armorX; }
            int y() { return HudLayout.armorY; }
            float scale() { return HudLayout.armorScale; }
            void setPosition(int x, int y) { HudLayout.armorX = x; HudLayout.armorY = y; }
            void setScale(float value) { HudLayout.armorScale = value; }
        },
        HOTKEYS("HOTKEYS", 154, 82) {
            int x() { return HudLayout.hotkeysX; }
            int y() { return HudLayout.hotkeysY; }
            float scale() { return HudLayout.hotkeysScale; }
            void setPosition(int x, int y) { HudLayout.hotkeysX = x; HudLayout.hotkeysY = y; }
            void setScale(float value) { HudLayout.hotkeysScale = value; }
        },
        POTIONS("POTIONS", 180, 130) {
            int x() { return HudLayout.potionsX; }
            int y() { return HudLayout.potionsY; }
            float scale() { return HudLayout.potionsScale; }
            void setPosition(int x, int y) { HudLayout.potionsX = x; HudLayout.potionsY = y; }
            void setScale(float value) { HudLayout.potionsScale = value; }
        },
        STAFF("STAFF", 180, 130) {
            int x() { return HudLayout.staffX; }
            int y() { return HudLayout.staffY; }
            float scale() { return HudLayout.staffScale; }
            void setPosition(int x, int y) { HudLayout.staffX = x; HudLayout.staffY = y; }
            void setScale(float value) { HudLayout.staffScale = value; }
        },
        TARGET("TARGET", 190, 48) {
            int x() { return HudLayout.targetX; }
            int y() { return HudLayout.targetY; }
            float scale() { return HudLayout.targetScale; }
            void setPosition(int x, int y) { HudLayout.targetX = x; HudLayout.targetY = y; }
            void setScale(float value) { HudLayout.targetScale = value; }
        },
        COOLDOWNS("COOLDOWNS", 156, 56) {
            int x() { return HudLayout.cooldownsX; }
            int y() { return HudLayout.cooldownsY; }
            float scale() { return HudLayout.cooldownsScale; }
            void setPosition(int x, int y) { HudLayout.cooldownsX = x; HudLayout.cooldownsY = y; }
            void setScale(float value) { HudLayout.cooldownsScale = value; }
        };

        private final String label;
        private final int baseWidth;
        private final int baseHeight;

        Element(String label, int baseWidth, int baseHeight) {
            this.label = label;
            this.baseWidth = baseWidth;
            this.baseHeight = baseHeight;
        }

        abstract int x();
        abstract int y();
        abstract float scale();
        abstract void setPosition(int x, int y);
        abstract void setScale(float value);

        String label() { return label; }
        int width() { return Math.round(baseWidth * scale()); }
        int height() { return Math.round(baseHeight * scale()); }
        boolean contains(double mouseX, double mouseY) {
            return mouseX >= x() && mouseX <= x() + width()
                    && mouseY >= y() && mouseY <= y() + height();
        }
    }
}
