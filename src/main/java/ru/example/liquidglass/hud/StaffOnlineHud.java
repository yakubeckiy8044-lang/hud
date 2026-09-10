package ru.example.liquidglass.hud;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.text.Text;
import ru.example.liquidglass.render.ShaderRenderUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class StaffOnlineHud {
    private static final int WIDTH = 180, ROW = 20;
    private static final String[] KEYWORDS = {"helper", "mod", "admin", "owner", "staff", "support"};
    private StaffOnlineHud() {}

    public static void render(DrawContext context, RenderTickCounter tickCounter) {
        if (!HudModuleState.staffOnlineEnabled) return;
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.options.hudHidden || client.getNetworkHandler() == null) return;
        List<String> staff = new ArrayList<>();
        for (PlayerListEntry entry : client.getNetworkHandler().getPlayerList()) {
            String name = entry.getProfile().getName();
            String lower = name.toLowerCase(Locale.ROOT);
            for (String keyword : KEYWORDS) {
                if (lower.contains(keyword)) { staff.add(name); break; }
            }
            if (staff.size() >= 6) break;
        }
        if (staff.isEmpty()) return;
        int x = client.getWindow().getScaledWidth() - WIDTH - HudModuleState.staffMarginRight;
        int y = HudModuleState.staffY;
        ShaderRenderUtil.drawGlassPanel(context, x, y, WIDTH, 28 + staff.size() * ROW + 6, 10.0f);
        context.drawText(client.textRenderer, Text.literal("STAFF ONLINE"), x + 10, y + 8, 0xFFFFFFFF, true);
        for (int i = 0; i < staff.size(); i++) {
            int rowY = y + 29 + i * ROW;
            context.fill(x + 10, rowY + 5, x + 14, rowY + 9, 0xFF69E6D0);
            context.drawText(client.textRenderer, Text.literal(staff.get(i)), x + 20, rowY + 1, 0xFFDCE7F0, false);
        }
    }
}