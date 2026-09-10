package ru.example.liquidglass;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;
import ru.example.liquidglass.hud.VisualHud;
import ru.example.liquidglass.hud.HudEditor;
import ru.example.liquidglass.render.ShaderRenderUtil;
import ru.example.liquidglass.screen.ClickGuiScreen;

public final class LiquidGlassClient implements ClientModInitializer {
    public static final String MOD_ID = "liquidglass";
    private static KeyBinding openGuiKey;

    @Override
    public void onInitializeClient() {
        ShaderRenderUtil.register();
        openGuiKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.liquidglass.open_gui",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_RIGHT_SHIFT,
                "category.liquidglass"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openGuiKey.wasPressed()) {
                client.setScreen(client.currentScreen instanceof ClickGuiScreen ? null : new ClickGuiScreen());
            }
        });

        HudRenderCallback.EVENT.register(VisualHud::render);

        ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
            if (!(screen instanceof ChatScreen)) return;
            ScreenEvents.afterRender(screen).register((screen1, context, mouseX, mouseY, tickDelta) ->
                    HudEditor.renderOverlay(context, mouseX, mouseY));
            ScreenMouseEvents.afterMouseClick(screen).register((screen1, mouseX, mouseY, button) ->
                    HudEditor.mouseClicked(mouseX, mouseY, button));
            ScreenMouseEvents.afterMouseRelease(screen).register((screen1, mouseX, mouseY, button) ->
                    HudEditor.mouseReleased(button));
            ScreenMouseEvents.afterMouseScroll(screen).register((screen1, mouseX, mouseY, horizontal, vertical) ->
                    HudEditor.mouseScrolled(mouseX, mouseY, vertical));
        });
    }
}
