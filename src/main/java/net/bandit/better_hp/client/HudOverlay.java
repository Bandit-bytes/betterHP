package net.bandit.better_hp.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.bandit.better_hp.BetterHP;
import net.bandit.better_hp.config.BetterHPConfig;
import net.bandit.better_hp.integration.AppleSkinCompat;
import net.bandit.better_hp.integration.EidolonCompat;
import net.bandit.better_hp.integration.IronsSpellbooksCompat;
import net.bandit.better_hp.integration.LsoCompat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;

/**
 * The Better HP HUD, registered once as a GUI overlay so it draws exactly once per frame.
 * Coordinates keep the original config semantics: X from the screen centre, Y up from the bottom.
 */
public final class HudOverlay implements IGuiOverlay {
    private static final Logger LOGGER = LoggerFactory.getLogger("BetterHP");

    private static final ResourceLocation HEALTH_ICON = tex("health_icon");
    private static final ResourceLocation HARDCORE_HEALTH_ICON = tex("hardcore_health_icon");
    private static final ResourceLocation FROZEN_HEALTH_ICON = tex("frozen_health_icon");
    private static final ResourceLocation POISONED_HEALTH_ICON = tex("poisoned_health_icon");
    private static final ResourceLocation WITHERED_HEALTH_ICON = tex("withered_health_icon");
    private static final ResourceLocation HUNGER_ICON = tex("hunger_icon");
    private static final ResourceLocation FROZEN_HUNGER_ICON = tex("frozen_hunger_icon");
    private static final ResourceLocation ARMOR_ICON = tex("armor_icon");
    private static final ResourceLocation TOUGHNESS_ICON = tex("toughness_icon");
    private static final ResourceLocation BREATHE_ICON = tex("breathe_icon");
    private static final ResourceLocation MOUNT_ICON = tex("mount_icon");
    private static final ResourceLocation MANA_ICON = tex("mana_icon");
    private static final ResourceLocation THIRST_ICON = tex("thirst_icon");
    private static final ResourceLocation TEMPERATURE_ICON = tex("temperature_icon");
    private static final ResourceLocation TEMPERATURE_FILL = tex("temperature_fill");

    private static final int ABSORPTION_COLOR = 0xFFFF00;
    private static final int ARMOR_COLOR = 0xAAAAAA;
    private static final int SATURATION_COLOR = 0xFFD700;
    private static final int OXYGEN_COLOR = 0x00BFFF;
    private static final int MANA_COLOR = 0x2F7DFF;
    private static final int MOUNT_COLOR = 0xAA77FF;
    private static final int FROZEN_COLOR = 0x9BE7FF;
    private static final int FROZEN_SHIMMER_COLOR = 0xE6FAFF;
    private static final int POISONED_COLOR = 0x9ACD32;
    private static final int WITHERED_COLOR = 0x8C8484;
    private static final int ETHEREAL_COLOR = 0xC77DFF;
    private static final int SHIELD_COLOR = 0xC8C8D8;
    private static final int BROKEN_COLOR = 0xA01818;
    private static final int HEAL_PREVIEW_COLOR = 0x55FF55;
    private static final int HUNGER_PREVIEW_COLOR = 0xFF7518;
    private static final int THIRST_COLOR = 0x3FA9FF;
    private static final int THIRST_SATURATION_COLOR = 0x9FD8FF;

    private boolean loggedRenderError;

    @Override
    public void render(ForgeGui gui, GuiGraphics g, float partialTick, int screenWidth, int screenHeight) {
        if (!BetterHPConfig.isLoaded()) return;
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (mc.options.hideGui || player == null || mc.gameMode == null || !mc.gameMode.canHurtPlayer()) return;

        try {
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            renderHud(g, mc, player, mc.font, screenWidth, screenHeight, player.tickCount + partialTick);
        } catch (RuntimeException e) {
            if (!loggedRenderError) {
                loggedRenderError = true;
                LOGGER.error("Better HP: error while rendering the HUD", e);
            }
        } finally {
            g.setColor(1F, 1F, 1F, 1F);
            RenderSystem.disableBlend();
        }
    }

    private void renderHud(GuiGraphics g, Minecraft mc, Player player, Font font, int sw, int sh, float time) {
        BetterHPConfig.Client cfg = BetterHPConfig.CLIENT;
        int cx = sw / 2;

        float health = player.getHealth();
        float maxHealth = player.getMaxHealth();
        int absorption = Mth.ceil(player.getAbsorptionAmount());
        int armorValue = player.getArmorValue();
        int hunger = player.getFoodData().getFoodLevel();
        float saturation = player.getFoodData().getSaturationLevel();
        int maxAir = player.getMaxAirSupply();
        int air = Math.max(0, player.getAirSupply()); // negative while drowning

        AppleSkinCompat.FoodPreview food = AppleSkinCompat.foodPreview(player);
        HeartState heart = heartState(player);

        int healthX = cx + cfg.healthDisplayX.get();
        int healthY = sh - cfg.healthDisplayY.get();
        int healthTextOffset = cfg.healthTextOffset.get();

        String healthText = cfg.showDecimalHealth.get()
                ? String.format(Locale.ROOT, "%.1f/%.1f", health, maxHealth)
                : Mth.ceil(health) + "/" + Mth.ceil(maxHealth); // ceil like vanilla: 0.5 HP never shows "0"
        int healthTextWidth = font.width(healthText);
        int healthTextStartX = healthX - healthTextWidth / 2 + healthTextOffset;
        int healthTextRightX = healthTextStartX + healthTextWidth;

        // ---- Iron's mana
        IronsSpellbooksCompat.ManaInfo mana = IronsSpellbooksCompat.getMana(player);
        if (mana != null) {
            int anchorX = cfg.manaFollowHealthWidth.get() && cfg.showNumericHealth.get()
                    ? healthTextRightX
                    : healthX;
            int ax = anchorX + cfg.manaOffsetX.get();
            int ay = healthY + cfg.manaOffsetY.get();
            String manaText = mana.mana() + "/" + mana.maxMana();
            boolean numeric = cfg.showNumericMana.get();
            if (numeric) text(g, font, manaText, ax, ay, MANA_COLOR);
            if (cfg.showManaIcon.get()) icon(g, MANA_ICON, ax + (numeric ? font.width(manaText) : 0), ay - 4);
        }

        // ---- health
        if (cfg.showNumericHealth.get()) {
            int color = healthColor(player, health, maxHealth, heart, time);
            if (cfg.showHealthOutline.get()) {
                outlinedText(g, font, healthText, healthTextStartX, healthY, color);
            } else {
                text(g, font, healthText, healthTextStartX, healthY, color);
            }

            int cursor = healthTextRightX + 5;
            if (cfg.showAbsorptionText.get() && absorption > 0) {
                int ax = cfg.absorptionFollowHealthWidth.get()
                        ? healthTextRightX + 5
                        : healthTextStartX + cfg.absorptionOffsetX.get();
                int ay = healthY + cfg.absorptionOffsetY.get();
                String abs = "+" + absorption;
                text(g, font, abs, ax, ay, ABSORPTION_COLOR);
                if (ay == healthY) cursor = Math.max(cursor, ax + font.width(abs) + 3);
            }

            float ethereal = EidolonCompat.etherealHealth(player);
            if (ethereal > 0F) cursor = trailing(g, font, "+" + Mth.ceil(ethereal), cursor, healthY, ETHEREAL_COLOR);

            LsoCompat.HealthExtras lso = LsoCompat.healthExtras(player);
            if (lso != null) {
                if (lso.shield() > 0F) cursor = trailing(g, font, "+" + Mth.ceil(lso.shield()), cursor, healthY, SHIELD_COLOR);
                if (lso.broken() > 0) cursor = trailing(g, font, "-" + lso.broken() + "❤", cursor, healthY, BROKEN_COLOR);
            }

            // AppleSkin: health the held food will regenerate. Inline (not above) because the
            // 1.20.1 layout puts the armor row directly above the health value.
            if (food != null && food.health() > 0F) {
                trailing(g, font, "+" + decimal(food.health()), cursor, healthY, withAlpha(HEAL_PREVIEW_COLOR, flashAlpha(time)));
            }
        }

        if (cfg.showHealthIcon.get()) {
            int shake = maxHealth > 0 && health / maxHealth < 0.2F ? (int) (Math.sin(player.tickCount * 0.6F) * 2) : 0;
            icon(g, healthIcon(heart, mc), healthX - 24 + shake, healthY - 4);
        }

        // ---- armor + toughness
        if (!cfg.showVanillaArmor.get() && cfg.showArmorIcon.get()) {
            int toughness = (int) player.getAttributeValue(Attributes.ARMOR_TOUGHNESS);
            if (armorValue > 0 || toughness > 0) {
                int ax = cx + cfg.armorDisplayX.get();
                int ay = sh - cfg.armorDisplayY.get();
                icon(g, ARMOR_ICON, ax - 24, ay - 4);
                text(g, font, String.valueOf(armorValue), ax, ay, ARMOR_COLOR);
                if (toughness > 0) {
                    icon(g, TOUGHNESS_ICON, ax + 20, ay - 4);
                    text(g, font, String.valueOf(toughness), ax + 38, ay, ARMOR_COLOR);
                }
            }
        }

        // ---- hunger
        int hungerX = cx + cfg.hungerDisplayX.get();
        int hungerY = sh - cfg.hungerDisplayY.get();
        boolean frozenHunger = LsoCompat.hasColdHunger(player);
        if (cfg.showNumericHunger.get()) {
            String hungerText = hunger + "/20";
            int w = font.width(hungerText);
            int start = hungerX - w / 2;
            int color = frozenHunger ? FROZEN_COLOR
                    : cfg.enableDynamicHealthColor.get() ? hungerColor(hunger) : 0xFF7518;
            text(g, font, hungerText, start, hungerY, color);

            boolean vanillaHungerOn = cfg.showVanillaHunger.get();
            if (cfg.showSaturationText.get() && saturation > 0F && !(cfg.hideSaturationIfVanillaHunger.get() && vanillaHungerOn)) {
                text(g, font, " +" + decimal(saturation), start + w + 14, hungerY, SATURATION_COLOR);
            }

            float exhaustion = AppleSkinCompat.exhaustionFraction(player);
            if (exhaustion >= 0F) {
                g.fill(start, hungerY + 10, start + w, hungerY + 11, 0x60000000);
                int filled = Math.round(w * exhaustion);
                if (filled > 0) g.fill(start, hungerY + 10, start + filled, hungerY + 11, 0xC0FFFFFF);
            }

            if (food != null && (food.hunger() > 0 || food.saturation() > 0F)) {
                int alpha = flashAlpha(time);
                int px = start;
                if (food.hunger() > 0) px = trailing(g, font, "+" + food.hunger(), px, hungerY - 10, withAlpha(HUNGER_PREVIEW_COLOR, alpha));
                if (food.saturation() > 0F) text(g, font, "+" + decimal(food.saturation()), px, hungerY - 10, withAlpha(SATURATION_COLOR, alpha));
            }
        }
        if (cfg.showHungerIcon.get()) {
            icon(g, frozenHunger ? FROZEN_HUNGER_ICON : HUNGER_ICON, hungerX + 18, hungerY - 4);
        }

        // ---- oxygen
        if (air < maxAir) {
            int ox = cx + cfg.oxygenDisplayX.get();
            int oy = sh - cfg.oxygenDisplayY.get();
            if (cfg.showNumericOxygen.get()) {
                String t = (air / 20) + "/" + (maxAir / 20);
                text(g, font, t, ox - font.width(t) / 2, oy, OXYGEN_COLOR);
            }
            if (cfg.showOxygenIcon.get()) icon(g, BREATHE_ICON, ox + 18, oy - 4);
        }

        // ---- mount
        LivingEntity mount = mountWithHealth(player);
        if (mount != null && cfg.showMountIcon.get()) {
            int mx = cx + cfg.mountDisplayX.get();
            int my = sh - cfg.mountDisplayY.get();
            icon(g, MOUNT_ICON, mx - 24, my - 4);
            text(g, font, Mth.ceil(mount.getHealth()) + "/" + Mth.ceil(mount.getMaxHealth()), mx, my, MOUNT_COLOR);
        }

        // ---- Legendary Survival Overhaul
        LsoCompat.Thirst thirst = LsoCompat.thirst(player);
        if (thirst != null) {
            int tx = cx + cfg.lsoThirstX.get();
            int ty = sh - cfg.lsoThirstY.get();
            String t = thirst.hydration() + "/" + thirst.maxHydration();
            int w = font.width(t);
            text(g, font, t, tx - w / 2, ty, THIRST_COLOR);
            icon(g, THIRST_ICON, tx + 18, ty - 4);
            if (cfg.lsoShowThirstSaturation.get() && thirst.saturation() > 0F) {
                text(g, font, " +" + decimal(thirst.saturation()), tx - w / 2 + w + 14, ty, THIRST_SATURATION_COLOR);
            }
        }
        drawTemperature(g, font, player, cx, sh, time);
    }

    private static void drawTemperature(GuiGraphics g, Font font, Player player, int cx, int sh, float time) {
        LsoCompat.Temperature temp = LsoCompat.temperature(player);
        if (temp == null) return;
        int x = cx + BetterHPConfig.CLIENT.lsoTemperatureX.get();
        int y = sh - BetterHPConfig.CLIENT.lsoTemperatureY.get();

        int color = switch (temp.state()) {
            case FROSTBITE -> 0x3F8CFF;
            case COLD -> 0x8FD3FF;
            case NORMAL -> 0x7CDB6A;
            case HOT -> 0xFFA040;
            case HEAT_STROKE -> 0xFF4A2A;
        };
        if (temp.state() == LsoCompat.State.FROSTBITE || temp.state() == LsoCompat.State.HEAT_STROKE) {
            color = lerpColor(0.35F * (0.5F + 0.5F * Mth.sin(time * 0.4F)), color, 0xFFFFFF);
        }

        icon(g, TEMPERATURE_ICON, x - 24, y - 4);
        int top = 11 - Math.round(temp.fraction() * 10F); // bulb rows 11-14 always full, tube fills with temperature
        g.setColor(((color >> 16) & 0xFF) / 255F, ((color >> 8) & 0xFF) / 255F, (color & 0xFF) / 255F, 1F);
        g.blit(TEMPERATURE_FILL, x - 24, y - 4 + top, 0, top, 16, 16 - top, 16, 16);
        g.setColor(1F, 1F, 1F, 1F);
        if (temp.exactText() != null) text(g, font, temp.exactText(), x, y, color);
    }

    // ------------------------------------------------------------------ health state

    private enum HeartState { NORMAL, POISONED, WITHERED, FROZEN }

    private static HeartState heartState(Player player) {
        boolean statuses = BetterHPConfig.CLIENT.showStatusHealthIcons.get();
        if (statuses && player.hasEffect(MobEffects.POISON)) return HeartState.POISONED;
        if (statuses && player.hasEffect(MobEffects.WITHER)) return HeartState.WITHERED;
        if (statuses && player.isFullyFrozen()) return HeartState.FROZEN;
        if (EidolonCompat.isChilled(player)) return HeartState.FROZEN;
        return HeartState.NORMAL;
    }

    private static ResourceLocation healthIcon(HeartState state, Minecraft mc) {
        return switch (state) {
            case POISONED -> POISONED_HEALTH_ICON;
            case WITHERED -> WITHERED_HEALTH_ICON;
            case FROZEN -> FROZEN_HEALTH_ICON;
            case NORMAL -> mc.level != null && mc.level.getLevelData().isHardcore() ? HARDCORE_HEALTH_ICON : HEALTH_ICON;
        };
    }

    private static int healthColor(Player player, float health, float maxHealth, HeartState state, float time) {
        // Effect colours (poison/wither) were always shown on 1.20.1; keep that even with status icons off.
        if (state == HeartState.FROZEN) return lerpColor(0.5F + 0.5F * Mth.sin(time * 0.15F), FROZEN_COLOR, FROZEN_SHIMMER_COLOR);
        if (!BetterHPConfig.CLIENT.enableDynamicHealthColor.get()) return BetterHPConfig.CLIENT.healthColor.get();
        if (player.hasEffect(MobEffects.POISON)) return POISONED_COLOR;
        if (player.hasEffect(MobEffects.WITHER)) return WITHERED_COLOR;
        if (maxHealth <= 0F) return 0xFF0000;
        float ratio = Mth.clamp(health / maxHealth, 0F, 1F);
        if (ratio < 0.2F) return lerpColor(0.5F + 0.5F * Mth.sin(time * 0.3F), 0x800000, 0xFF0000);
        return ratio > 0.5F
                ? lerpColor((1F - ratio) * 2F, 0x00FF00, 0xFFFF00)
                : lerpColor((0.5F - ratio) * 2F, 0xFFFF00, 0xFF0000);
    }

    private static int hungerColor(int hunger) {
        float ratio = Mth.clamp(hunger / 20F, 0F, 1F);
        return ratio > 0.5F
                ? lerpColor((1F - ratio) * 2F, 0xFFFF00, 0xFFA500)
                : lerpColor((0.5F - ratio) * 2F, 0xFFA500, 0xFF4500);
    }

    @Nullable
    private static LivingEntity mountWithHealth(Player player) {
        return player.getVehicle() instanceof LivingEntity living && living.showVehicleHealth() ? living : null;
    }

    // ------------------------------------------------------------------ drawing helpers

    private static void icon(GuiGraphics g, ResourceLocation icon, int x, int y) {
        g.blit(icon, x, y, 0, 0, 16, 16, 16, 16);
    }

    private static void text(GuiGraphics g, Font font, String text, int x, int y, int color) {
        g.drawString(font, text, x, y, color, true);
    }

    private static int trailing(GuiGraphics g, Font font, String text, int x, int y, int color) {
        text(g, font, text, x, y, color);
        return x + font.width(text) + 3;
    }

    private static void outlinedText(GuiGraphics g, Font font, String text, int x, int y, int color) {
        g.drawString(font, text, x - 1, y, 0x000000, false);
        g.drawString(font, text, x + 1, y, 0x000000, false);
        g.drawString(font, text, x, y - 1, 0x000000, false);
        g.drawString(font, text, x, y + 1, 0x000000, false);
        g.drawString(font, text, x, y, color, false);
    }

    private static int flashAlpha(float time) {
        return (int) (110 + 145 * (0.5F + 0.5F * Mth.sin(time * 0.25F)));
    }

    private static int withAlpha(int rgb, int alpha) {
        return (Mth.clamp(alpha, 8, 255) << 24) | (rgb & 0xFFFFFF);
    }

    private static int lerpColor(float t, int from, int to) {
        t = Mth.clamp(t, 0F, 1F);
        int r = (int) Mth.lerp(t, (from >> 16) & 0xFF, (to >> 16) & 0xFF);
        int gr = (int) Mth.lerp(t, (from >> 8) & 0xFF, (to >> 8) & 0xFF);
        int b = (int) Mth.lerp(t, from & 0xFF, to & 0xFF);
        return (r << 16) | (gr << 8) | b;
    }

    private static String decimal(float value) {
        float rounded = Math.round(value * 10F) / 10F;
        return rounded == (int) rounded ? Integer.toString((int) rounded) : String.format(Locale.ROOT, "%.1f", rounded);
    }

    private static ResourceLocation tex(String name) {
        return new ResourceLocation(BetterHP.MOD_ID, "textures/gui/" + name + ".png");
    }
}
