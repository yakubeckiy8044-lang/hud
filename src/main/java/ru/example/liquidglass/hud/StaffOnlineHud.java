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
        if (!HudModuleState.staffOnlineEnabled && !HudEditor.isEditing()) return;
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
        int x = HudLayout.staffX;
        int y = HudLayout.staffY;
        float scale = HudLayout.staffScale;
        int rows = Math.max(1, staff.size());
        int height = 28 + rows * ROW + 6;
        ShaderRenderUtil.drawGlassPanel(context, x, y, Math.round(WIDTH * scale), Math.round(height * scale), 10.0f * scale);
        context.getMatrices().push();
        context.getMatrices().translate(x, y, 0.0f);
        context.getMatrices().scale(scale, scale, 1.0f);
        context.drawText(client.textRenderer, HudStyle.text("STAFF ONLINE"), 10, 8, HudStyle.WHITE, false);
        if (staff.isEmpty()) {
            context.drawText(client.textRenderer, HudStyle.text("No staff detected"), 20, 30, HudStyle.MUTED, false);
            context.getMatrices().pop();
            return;
        }
        for (int i = 0; i < staff.size(); i++) {
            int rowY = 29 + i * ROW;
            context.fill(10, rowY + 5, 14, rowY + 9, HudStyle.ACCENT);
            context.drawText(client.textRenderer, HudStyle.text(staff.get(i)), 20, rowY + 1, HudStyle.MUTED, false);
        }
        context.getMatrices().pop();
    }
}
