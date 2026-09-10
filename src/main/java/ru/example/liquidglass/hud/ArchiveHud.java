package ru.example.liquidglass.hud;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.SkinTextures;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffectUtil;
import net.minecraft.entity.player.ItemCooldownManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Language;
import net.minecraft.util.hit.EntityHitResult;
import ru.example.liquidglass.ClientConfig;
import ru.example.liquidglass.render.ShaderRenderUtil;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/** DrawContext/Fabric 1.21.4 port of the supplied compact HUD widgets. */
public final class ArchiveHud {
    private static final int RADIUS = 8;
    private static final int HEADER = 0xB8161C2D;
    private static final int ROW = 0x3A28334B;
    private static final int ACTIVE_ROW = 0x9A6046A2;
    private static final String[] STAFF_WORDS = {"helper", "moder", "admin", "owner", "staff", "support", "куратор", "модер", "админ", "хелпер"};

    private ArchiveHud() {}

    public static void render(DrawContext context, RenderTickCounter tickCounter) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.options.hudHidden) return;
        watermark(context, client);
        armor(context, client, client.player);
        hotkeys(context, client);
        potions(context, client, client.player);
        staff(context, client);
        target(context, client, client.player);
        cooldowns(context, client, client.player);
    }

    private static void watermark(DrawContext c, MinecraftClient mc) {
        if (!HudModuleState.infoHudEnabled && !HudEditor.isEditing()) return;
        int x = HudLayout.infoX, y = HudLayout.infoY; float s = hudScale(mc, HudLayout.infoScale);
        glass(c, x, y, 202, 44, s); begin(c, x, y, s);
        c.drawText(mc.textRenderer, HudStyle.text("LIQUID GLASS"), 10, 8, HudStyle.WHITE, false);
        c.drawText(mc.textRenderer, HudStyle.text(mc.getCurrentFps() + " FPS"), 104, 8, HudStyle.ACCENT, false);
        LocalTime t = LocalTime.now();
        c.drawText(mc.textRenderer, HudStyle.text(String.format(Locale.ROOT, "%02d:%02d:%02d", t.getHour(), t.getMinute(), t.getSecond())), 157, 8, HudStyle.MUTED, false);
        c.drawText(mc.textRenderer, HudStyle.text(String.format(Locale.ROOT, "%d %d %d", (int) mc.player.getX(), (int) mc.player.getY(), (int) mc.player.getZ())), 10, 26, HudStyle.MUTED, false);
        c.drawText(mc.textRenderer, HudStyle.text(ping(mc) + " PING"), 128, 26, HudStyle.ACCENT, false); end(c);
    }

    private static void armor(DrawContext c, MinecraftClient mc, PlayerEntity p) {
        if (!ClientConfig.armorHudEnabled && !HudEditor.isEditing()) return;
        List<ItemStack> list = new ArrayList<>();
        List<ItemStack> armor = new ArrayList<>(); p.getArmorItems().forEach(armor::add);
        for (int i = armor.size() - 1; i >= 0; i--) if (!armor.get(i).isEmpty()) list.add(armor.get(i));
        if (list.isEmpty() && !HudEditor.isEditing()) return;
        int x = HudLayout.armorX, y = HudLayout.armorY; float s = hudScale(mc, HudLayout.armorScale);
        boolean vertical = x < 48 || x > mc.getWindow().getScaledWidth() - 150; int cell = 21;
        int w = vertical ? 132 : Math.max(38, cell * list.size() + 8);
        int h = vertical ? Math.max(29, cell * list.size() + 8) : 38;
        glass(c, x, y, w, h, s); begin(c, x, y, s);
        if (list.isEmpty()) {
            c.drawText(mc.textRenderer, HudStyle.text("NO ARMOR"), 30, 9, HudStyle.MUTED, false);
            end(c);
            return;
        }
        for (int i = 0; i < list.size(); i++) {
            ItemStack stack = list.get(i); int ix = vertical ? 6 : 4 + i * cell; int iy = vertical ? 4 + i * cell : 4;
            if (stack.isEmpty()) { if (vertical) c.drawText(mc.textRenderer, HudStyle.text("-"), 10, iy + 5, HudStyle.MUTED, false); continue; }
            c.drawItem(stack, ix, iy);
            if (stack.isDamageable()) {
                int max = stack.getMaxDamage(), left = Math.max(0, max - stack.getDamage()); float ratio = max <= 0 ? 1f : left / (float) max;
                int bw = vertical ? 14 : 15, bx = vertical ? ix + 1 : ix + 3, by = iy + 17;
                c.fill(bx, by, bx + bw, by + 2, 0xFF252A3B); c.fill(bx, by, bx + Math.max(1, Math.round(bw * ratio)), by + 2, durability(ratio));
                if (vertical) c.drawText(mc.textRenderer, HudStyle.text(left + "/" + max), 30, iy + 5, durability(ratio), false);
            }
        }
        end(c);
    }

    private static void hotkeys(DrawContext c, MinecraftClient mc) {
        if (!HudModuleState.hotkeysEnabled && !HudEditor.isEditing()) return;
        int x = HudLayout.hotkeysX, y = HudLayout.hotkeysY; float s = hudScale(mc, HudLayout.hotkeysScale);
        glass(c, x, y, 214, 82, s); begin(c, x, y, s); c.drawText(mc.textRenderer, HudStyle.text("HOTKEYS"), 10, 8, HudStyle.WHITE, false);
        int w = 42, h = 20, g = 4, sx = 10, sy = 28;
        key(c, mc, "W", sx + w + g, sy, mc.options.forwardKey.isPressed(), w, h);
        key(c, mc, "A", sx, sy + h + g, mc.options.leftKey.isPressed(), w, h);
        key(c, mc, "S", sx + w + g, sy + h + g, mc.options.backKey.isPressed(), w, h);
        key(c, mc, "D", sx + (w + g) * 2, sy + h + g, mc.options.rightKey.isPressed(), w, h);
        key(c, mc, "LMB", sx + (w + g) * 3, sy, mc.options.attackKey.isPressed(), w, h);
        key(c, mc, "RMB", sx + (w + g) * 3, sy + h + g, mc.options.useKey.isPressed(), w, h); end(c);
    }

    private static void potions(DrawContext c, MinecraftClient mc, PlayerEntity p) {
        if (!HudModuleState.activePotionsEnabled && !HudEditor.isEditing()) return;
        List<StatusEffectInstance> effects = new ArrayList<>(p.getStatusEffects()); int x = HudLayout.potionsX, y = HudLayout.potionsY; float s = hudScale(mc, HudLayout.potionsScale);
        int w = 180, row = 24, h = 34 + Math.max(1, effects.size()) * row; glass(c, x, y, w, h, s); begin(c, x, y, s); c.fill(0, 0, w, 26, HEADER);
        c.drawText(mc.textRenderer, HudStyle.text("ACTIVE POTIONS"), 10, 8, HudStyle.WHITE, false);
        if (effects.isEmpty()) c.drawText(mc.textRenderer, HudStyle.text("No active effects"), 31, 34, HudStyle.MUTED, false);
        for (int i = 0; i < effects.size(); i++) {
            StatusEffectInstance e = effects.get(i); int ry = 30 + i * row; Sprite sprite = mc.getStatusEffectSpriteManager().getSprite(e.getEffectType());
            c.drawSpriteStretched(RenderLayer::getGuiTextured, sprite, 8, ry, 18, 18);
            String name = Language.getInstance().get(e.getTranslationKey()); if (name == null || name.isBlank()) name = e.getEffectType().value().getName().getString();
            if (e.getAmplifier() > 0) name += " " + (e.getAmplifier() + 1); Text timer = StatusEffectUtil.getDurationText(e, 1f, 20f);
            c.drawText(mc.textRenderer, HudStyle.text(trim(name, 15)), 31, ry + 1, HudStyle.MUTED, false);
            c.drawText(mc.textRenderer, HudStyle.text(timer.getString()), w - 52, ry + 1, HudStyle.ACCENT, false);
        }
        end(c);
    }

    private static void staff(DrawContext c, MinecraftClient mc) {
        if (!HudModuleState.staffOnlineEnabled && !HudEditor.isEditing()) return; if (mc.getNetworkHandler() == null) return;
        List<String> names = new ArrayList<>();
        for (PlayerListEntry e : mc.getNetworkHandler().getPlayerList()) { String n = e.getProfile().getName(), lower = n.toLowerCase(Locale.ROOT); for (String word : STAFF_WORDS) if (lower.contains(word)) { names.add(n); break; } if (names.size() >= 6) break; }
        int x = HudLayout.staffX, y = HudLayout.staffY; float s = hudScale(mc, HudLayout.staffScale); int w = 180, row = 20, h = 34 + Math.max(1, names.size()) * row;
        glass(c, x, y, w, h, s); begin(c, x, y, s); c.fill(0, 0, w, 26, HEADER); c.drawText(mc.textRenderer, HudStyle.text("STAFF ONLINE"), 10, 8, HudStyle.WHITE, false);
        if (names.isEmpty()) c.drawText(mc.textRenderer, HudStyle.text("No staff detected"), 20, 30, HudStyle.MUTED, false);
        for (int i = 0; i < names.size(); i++) { int ry = 29 + i * row; c.fill(10, ry + 5, 14, ry + 9, HudStyle.ACCENT); c.drawText(mc.textRenderer, HudStyle.text(trim(names.get(i), 20)), 20, ry + 1, HudStyle.MUTED, false); c.fill(w - 15, ry + 5, w - 10, ry + 10, HudStyle.ACCENT); }
        end(c);
    }

    private static void target(DrawContext c, MinecraftClient mc, PlayerEntity self) {
        if (!HudModuleState.targetHudEnabled && !HudEditor.isEditing()) return; PlayerEntity target = null;
        if (mc.crosshairTarget instanceof EntityHitResult hit && hit.getEntity() instanceof PlayerEntity p) target = p; else if (HudEditor.isEditing()) target = self;
        if (target == null) return; int x = HudLayout.targetX, y = HudLayout.targetY; float s = hudScale(mc, HudLayout.targetScale); int w = 190, h = 48;
        glass(c, x, y, w, h, s); begin(c, x, y, s);
        if (target instanceof AbstractClientPlayerEntity p) { SkinTextures skin = p.getSkinTextures(); c.drawTexture(RenderLayer::getEntityTranslucent, skin.texture(), 8, 8, 8f, 8f, 18, 18, 64, 64); }
        c.drawText(mc.textRenderer, HudStyle.text(trim(target.getName().getString(), 18)), 34, 8, HudStyle.WHITE, false);
        float max = Math.max(1f, target.getMaxHealth()), hp = Math.max(0f, Math.min(max, target.getHealth())), ratio = hp / max; int bw = w - 46;
        c.fill(34, 27, 34 + bw, 34, 0xFF283548); c.fill(34, 27, 34 + Math.max(1, Math.round(bw * ratio)), 34, health(ratio));
        c.drawText(mc.textRenderer, HudStyle.text(String.format(Locale.ROOT, "%.1f / %.1f HP", hp, max)), 34, 36, HudStyle.MUTED, false); end(c);
    }

    private static void cooldowns(DrawContext c, MinecraftClient mc, PlayerEntity p) {
        if (!HudModuleState.cooldownsEnabled && !HudEditor.isEditing()) return; ItemCooldownManager manager = p.getItemCooldownManager(); List<ItemStack> stacks = new ArrayList<>(); Set<String> seen = new HashSet<>();
        for (int i = 0; i < p.getInventory().size(); i++) { ItemStack stack = p.getInventory().getStack(i); if (stack.isEmpty() || !manager.isCoolingDown(stack)) continue; if (seen.add(stack.getItem().toString())) stacks.add(stack); if (stacks.size() == 5) break; }
        if (stacks.isEmpty() && !HudEditor.isEditing()) return; int x = HudLayout.cooldownsX, y = HudLayout.cooldownsY; float s = hudScale(mc, HudLayout.cooldownsScale); int w = 156, row = 22, h = 34 + Math.max(1, stacks.size()) * row;
        glass(c, x, y, w, h, s); begin(c, x, y, s); c.fill(0, 0, w, 26, HEADER); c.drawText(mc.textRenderer, HudStyle.text("COOLDOWNS"), 10, 8, HudStyle.WHITE, false);
        if (stacks.isEmpty()) c.drawText(mc.textRenderer, HudStyle.text("No cooldowns"), 10, 34, HudStyle.MUTED, false);
        for (int i = 0; i < stacks.size(); i++) { ItemStack stack = stacks.get(i); int ry = 29 + i * row; c.drawItem(stack, 7, ry - 1); c.drawText(mc.textRenderer, HudStyle.text(trim(stack.getName().getString(), 13)), 30, ry + 1, HudStyle.MUTED, false); int bw = 45; float progress = manager.getCooldownProgress(stack, 0f); c.fill(w - 54, ry + 5, w - 9, ry + 8, 0xFF252A3B); c.fill(w - 54, ry + 5, w - 54 + Math.max(1, Math.round(bw * progress)), ry + 8, HudStyle.ACCENT); }
        end(c);
    }

    private static void key(DrawContext c, MinecraftClient mc, String label, int x, int y, boolean pressed, int w, int h) { c.fill(x, y, x + w, y + h, pressed ? ACTIVE_ROW : ROW); Text text = HudStyle.text(label); c.drawText(mc.textRenderer, text, x + (w - mc.textRenderer.getWidth(text)) / 2, y + 6, pressed ? HudStyle.WHITE : HudStyle.MUTED, false); }
    private static float hudScale(MinecraftClient mc, float userScale) { return HudLayout.renderScale(mc, userScale); }
    private static void glass(DrawContext c, int x, int y, int w, int h, float s) { ShaderRenderUtil.drawGlassPanel(c, x, y, Math.max(1, Math.round(w * s)), Math.max(1, Math.round(h * s)), RADIUS * s); }
    private static void begin(DrawContext c, int x, int y, float s) { c.getMatrices().push(); c.getMatrices().translate(x, y, 0f); c.getMatrices().scale(s, s, 1f); }
    private static void end(DrawContext c) { c.getMatrices().pop(); }
    private static int durability(float r) { return r <= .15f ? HudStyle.DANGER : r <= .40f ? 0xFFFFB45E : 0xFF8EF7BA; }
    private static int health(float r) { return r <= .25f ? 0xFFE85C6A : r <= .55f ? 0xFFFFC15A : HudStyle.ACCENT; }
    private static int ping(MinecraftClient mc) { if (mc.getNetworkHandler() == null || mc.player == null) return 0; PlayerListEntry e = mc.getNetworkHandler().getPlayerListEntry(mc.player.getUuid()); return e == null ? 0 : Math.max(0, e.getLatency()); }
    private static String trim(String value, int max) { return value.length() <= max ? value : value.substring(0, Math.max(1, max - 3)) + "..."; }
}
