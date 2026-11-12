package net.bandit.better_hp.client;

import net.bandit.better_hp.BetterhpMod;
import net.bandit.better_hp.config.BetterHPConfig;
import net.bandit.better_hp.integration.AppleSkinCompat;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.client.gui.GuiLayer;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

/**
 * The Better HP HUD, registered as a GUI layer above vanilla's air bar.
 *
 * <p>Coordinates follow the original config semantics: X is relative to the screen centre,
 * Y is measured upwards from the bottom of the screen to the top of the text.
 */
final class HudLayer implements GuiLayer {

    private static final ResourceLocation HEALTH_ICON = tex("health_icon");
    private static final ResourceLocation HARDCORE_HEALTH_ICON = tex("hardcore_health_icon");
    private static final ResourceLocation FROZEN_HEALTH_ICON = tex("frozen_health_icon");
    private static final ResourceLocation POISONED_HEALTH_ICON = tex("poisoned_health_icon");
    private static final ResourceLocation WITHERED_HEALTH_ICON = tex("withered_health_icon");
    private static final ResourceLocation HUNGER_ICON = tex("hunger_icon");
    private static final ResourceLocation ARMOR_ICON = tex("armor_icon");
    private static final ResourceLocation TOUGHNESS_ICON = tex("toughness_icon");
    private static final ResourceLocation MOUNT_ICON = tex("mount_icon");
    private static final ResourceLocation BREATHE_ICON = tex("breathe_icon");

    private static final int ICON = 16;
    private static final int GAP = 3;
    private static final int WHITE = 0xFFFFFFFF;

    private static final int ARMOR_COLOR = 0xAAAAAA;
    private static final int TOUGHNESS_COLOR = 0xADD8E6;
    private static final int HUNGER_COLOR = 0xFF7518;
    private static final int SATURATION_COLOR = 0xFFD700;
    private static final int OXYGEN_COLOR = 0x00BFFF;
    private static final int ABSORPTION_COLOR = 0xFFFF00;
    private static final int MOUNT_COLOR = 0xAA77FF;
    private static final int FROZEN_COLOR = 0x9BE7FF;
    private static final int FROZEN_SHIMMER_COLOR = 0xE6FAFF;
    private static final int POISONED_COLOR = 0x9CC644;
    private static final int WITHERED_COLOR = 0x8C8484;
    private static final int HEAL_PREVIEW_COLOR = 0x55FF55;

    /** Length of the armor/toughness "bounce" when the value changes, in game ticks. */
    private static final float BOUNCE_TICKS = 10F;

    private final Bounce armorBounce = new Bounce();
    private final Bounce toughnessBounce = new Bounce();
    private boolean loggedRenderError;

    @Override
    public void render(GuiGraphics g, DeltaTracker deltaTracker) {
        if (!BetterHPConfig.hudActive()) return;

        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (mc.options.hideGui || player == null || mc.gameMode == null) return;
        // Same rule vanilla uses for its health/food bars: nothing in creative or spectator.
        if (!mc.gameMode.canHurtPlayer()) return;

        try {
            float time = player.tickCount + deltaTracker.getGameTimeDeltaPartialTick(true);
            Ctx ctx = new Ctx(g, mc.font, g.guiWidth() / 2, g.guiHeight(),
                    (float) (double) BetterHPConfig.hudIconScale.get(),
                    (float) (double) BetterHPConfig.hudTextScale.get(), time);
            renderHud(ctx, player, mc);
        } catch (RuntimeException e) {
            // A HUD bug should never take the game down; log once and keep going.
            if (!loggedRenderError) {
                loggedRenderError = true;
                BetterhpMod.getLogger().error("Better HP: error while rendering the HUD", e);
            }
        }
    }

    private void renderHud(Ctx c, LocalPlayer player, Minecraft mc) {
        AppleSkinCompat.FoodPreview food = AppleSkinCompat.foodPreview(player);

        drawHealth(c, player, mc, food);

        int armor = player.getArmorValue();
        drawArmorLike(c, armorBounce, armor, armor >= 20, ARMOR_ICON, ARMOR_COLOR,
                BetterHPConfig.showArmorIcon.get(), BetterHPConfig.armorDisplayX.get(), BetterHPConfig.armorDisplayY.get());

        int toughness = (int) player.getAttributeValue(Attributes.ARMOR_TOUGHNESS);
        drawArmorLike(c, toughnessBounce, toughness, toughness >= 5, TOUGHNESS_ICON, TOUGHNESS_COLOR,
                BetterHPConfig.showToughnessIcon.get(), BetterHPConfig.toughnessDisplayX.get(), BetterHPConfig.toughnessDisplayY.get());

        drawHunger(c, player, food);
        drawOxygen(c, player);
        drawMount(c, player);
    }

    // =====================================================================================
    // Health
    // =====================================================================================

    private enum HeartState { NORMAL, POISONED, WITHERED, FROZEN }

    private void drawHealth(Ctx c, Player player, Minecraft mc, @Nullable AppleSkinCompat.FoodPreview food) {
        int x = c.cx + BetterHPConfig.healthDisplayX.get();
        int y = c.sh - BetterHPConfig.healthDisplayY.get();
        boolean showIcon = BetterHPConfig.showHealthIcon.get();
        boolean showNumeric = BetterHPConfig.showNumericHealth.get();

        float health = player.getHealth();
        float maxHealth = player.getMaxHealth();
        HeartState state = heartState(player);

        int used = 0;
        if (showIcon) {
            drawIcon(c, healthIcon(state, mc), x - 24, y - 4, WHITE);
            used += scaledIcon(c);
            if (showNumeric) used += 4;
        }

        if (showNumeric) {
            // Mth.ceil matches how vanilla counts half-hearts, so 0.5 HP never shows as "0".
            String text = Mth.ceil(health) + "/" + Mth.ceil(maxHealth);
            int shake = 0;
            if (maxHealth > 0 && health / maxHealth < 0.2F) {
                shake = (int) (Math.sin(c.time * 0.6F) * 2);
            }
            drawText(c, text, x + shake, y, healthColor(health, maxHealth, state, c.time));
            used += c.textWidth(text);
        }

        int absorption = Mth.ceil(player.getAbsorptionAmount());
        if (BetterHPConfig.showAbsorptionText.get() && absorption > 0) {
            int follow = BetterHPConfig.absorptionFollowHealthWidth.get() ? used : 0;
            drawText(c, "+" + absorption, x + follow + BetterHPConfig.absorptionOffsetX.get(),
                    y + BetterHPConfig.absorptionOffsetY.get(), ABSORPTION_COLOR);
        }

        // AppleSkin: health the held food will regenerate, floating just above the value.
        if (food != null && food.health() > 0F && showNumeric) {
            drawText(c, "+" + formatDecimal(food.health()), x, y - lineHeight(c), withAlpha(HEAL_PREVIEW_COLOR, flashAlpha(c.time)));
        }
    }

    private static HeartState heartState(Player player) {
        if (!BetterHPConfig.showStatusHealthIcons.get()) return HeartState.NORMAL;
        // Same priority as vanilla's Gui.HeartType: poison, then wither, then frozen.
        if (player.hasEffect(MobEffects.POISON)) return HeartState.POISONED;
        if (player.hasEffect(MobEffects.WITHER)) return HeartState.WITHERED;
        if (player.isFullyFrozen()) return HeartState.FROZEN;
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

    private static int healthColor(float health, float maxHealth, HeartState state, float time) {
        switch (state) {
            case FROZEN:
                return lerpColor(0.5F + 0.5F * Mth.sin(time * 0.15F), FROZEN_COLOR, FROZEN_SHIMMER_COLOR);
            case POISONED:
                return POISONED_COLOR;
            case WITHERED:
                return WITHERED_COLOR;
            default:
                break;
        }
        if (!BetterHPConfig.enableDynamicHealthColor.get()) return BetterHPConfig.healthColor.get();
        if (maxHealth <= 0F) return 0xFF0000;

        float ratio = Mth.clamp(health / maxHealth, 0F, 1F);
        if (ratio < 0.2F) {
            return lerpColor(0.5F + 0.5F * Mth.sin(time * 0.3F), 0x800000, 0xFF0000);
        }
        return ratio > 0.5F
                ? lerpColor((1F - ratio) * 2F, 0x00FF00, 0xFFFF00)
                : lerpColor((0.5F - ratio) * 2F, 0xFFFF00, 0xFF0000);
    }

    // =====================================================================================
    // Armor / toughness
    // =====================================================================================

    private void drawArmorLike(Ctx c, Bounce bounce, int value, boolean pulseWhenHigh, ResourceLocation icon,
                               int color, boolean enabled, int cfgX, int cfgY) {
        float scale = bounce.update(value, c.time);
        if (!enabled || value <= 0) return;

        float pulse = pulseWhenHigh ? 0.9F + 0.1F * Mth.sin(c.time * 0.2F) : 1F;
        int shade = Mth.clamp(Math.round(pulse * 255), 0, 255);
        int tint = 0xFF000000 | (shade << 16) | (shade << 8) | shade;

        int textX = c.cx + cfgX;
        int textY = c.sh - cfgY;
        float pivotX = textX - 10;
        float pivotY = textY + 2;

        var pose = c.g.pose();
        pose.pushMatrix();
        pose.translate(pivotX, pivotY);
        pose.scale(scale, scale);
        pose.translate(-pivotX, -pivotY);
        drawIcon(c, icon, textX - 24, textY - 4, tint);
        drawText(c, String.valueOf(value), textX, textY, color);
        pose.popMatrix();
    }

    /** Frame-rate independent "pop" animation when a value changes. */
    private static final class Bounce {
        private int lastValue = Integer.MIN_VALUE;
        private float start = -BOUNCE_TICKS;

        float update(int value, float now) {
            if (lastValue == Integer.MIN_VALUE) {
                lastValue = value; // first frame: no bounce on join
            } else if (value != lastValue) {
                lastValue = value;
                start = now;
            }
            float progress = (now - start) / BOUNCE_TICKS;
            if (progress < 0F || progress >= 1F) return 1F;
            return 1F + 0.2F * Mth.sin(progress * Mth.PI);
        }
    }

    // =====================================================================================
    // Hunger / oxygen / mount
    // =====================================================================================

    private static void drawHunger(Ctx c, Player player, @Nullable AppleSkinCompat.FoodPreview food) {
        boolean vanillaHunger = BetterHPConfig.showVanillaHunger.get();
        boolean showIcon = BetterHPConfig.showHungerIcon.get();
        boolean showNumeric = BetterHPConfig.showNumericHunger.get();

        int x = c.cx + BetterHPConfig.hungerDisplayX.get();
        int y = c.sh - BetterHPConfig.hungerDisplayY.get();

        int hunger = player.getFoodData().getFoodLevel();
        float saturation = player.getFoodData().getSaturationLevel();
        boolean showSaturation = BetterHPConfig.showSaturationText.get() && saturation > 0F;

        if (vanillaHunger || (!showIcon && !showNumeric)) {
            if (showSaturation && !(vanillaHunger && BetterHPConfig.hideSaturationIfVanillaHunger.get())) {
                drawText(c, "+" + formatDecimal(saturation), x + 46, y, SATURATION_COLOR);
            }
            return;
        }

        String text = hunger + "/20";
        int textWidth = showNumeric ? c.textWidth(text) : 0;
        int cursor = x;

        if (showNumeric) {
            drawText(c, text, x, y, HUNGER_COLOR);
            cursor += textWidth;

            float exhaustion = AppleSkinCompat.exhaustionFraction(player);
            if (exhaustion >= 0F) {
                int barY = y + lineHeight(c);
                c.g.fill(x, barY, x + textWidth, barY + 1, 0x60000000);
                int filled = Math.round(textWidth * exhaustion);
                if (filled > 0) c.g.fill(x, barY, x + filled, barY + 1, 0xC0FFFFFF);
            }
        }
        if (showIcon) {
            drawIcon(c, HUNGER_ICON, cursor, y - 4, WHITE);
            cursor += scaledIcon(c);
        }
        if (showSaturation) {
            drawText(c, "+" + formatDecimal(saturation), cursor + 1, y, SATURATION_COLOR);
        }

        // AppleSkin: what the held food restores, floating above the hunger icon.
        if (food != null && (food.hunger() > 0 || food.saturation() > 0F)) {
            int alpha = flashAlpha(c.time);
            int px = x + textWidth;
            int py = y - lineHeight(c);
            if (food.hunger() > 0) {
                String t = "+" + food.hunger();
                drawText(c, t, px, py, withAlpha(HUNGER_COLOR, alpha));
                px += c.textWidth(t) + GAP;
            }
            if (food.saturation() > 0F) {
                drawText(c, "+" + formatDecimal(food.saturation()), px, py, withAlpha(SATURATION_COLOR, alpha));
            }
        }
    }

    private static void drawOxygen(Ctx c, Player player) {
        int maxAir = player.getMaxAirSupply();
        int air = Math.max(0, player.getAirSupply()); // negative while drowning
        if (maxAir <= 0 || air >= maxAir) return;

        int x = c.cx + BetterHPConfig.oxygenDisplayX.get();
        int y = c.sh - BetterHPConfig.oxygenDisplayY.get();
        if (BetterHPConfig.showNumericOxygen.get()) {
            drawText(c, (air / 20) + "/" + (maxAir / 20), x - 10, y, OXYGEN_COLOR);
        }
        if (BetterHPConfig.showOxygenIcon.get()) {
            drawIcon(c, BREATHE_ICON, x + 18, y - 4, WHITE);
        }
    }

    private static void drawMount(Ctx c, Player player) {
        if (!BetterHPConfig.showMountIcon.get()) return;
        if (!(player.getVehicle() instanceof LivingEntity mount) || !mount.showVehicleHealth()) return;

        int x = c.cx + BetterHPConfig.mountDisplayX.get();
        int y = c.sh - BetterHPConfig.mountDisplayY.get();
        drawIcon(c, MOUNT_ICON, x - 24, y - 4, WHITE);
        drawText(c, Mth.ceil(mount.getHealth()) + "/" + Mth.ceil(mount.getMaxHealth()), x, y, MOUNT_COLOR);
    }

    // =====================================================================================
    // Drawing helpers
    // =====================================================================================

    private record Ctx(GuiGraphics g, Font font, int cx, int sh, float iconScale, float textScale, float time) {
        int textWidth(String text) {
            return Math.round(font.width(text) * textScale);
        }
    }

    private static int scaledIcon(Ctx c) {
        return Math.max(1, Math.round(ICON * c.iconScale));
    }

    private static int lineHeight(Ctx c) {
        return Math.round((c.font.lineHeight + 1) * c.textScale);
    }

    /** Draws a 16x16 icon scaled around its own centre (the original Better HP behaviour). */
    private static void drawIcon(Ctx c, ResourceLocation icon, int x, int y, int argb) {
        float offset = (ICON * c.iconScale - ICON) / 2F;
        var pose = c.g.pose();
        pose.pushMatrix();
        pose.translate(x - offset, y - offset);
        pose.scale(c.iconScale, c.iconScale);
        c.g.blit(RenderPipelines.GUI_TEXTURED, icon, 0, 0, 0, 0, ICON, ICON, ICON, ICON, argb);
        pose.popMatrix();
    }

    private static void drawText(Ctx c, String text, int x, int y, int color) {
        // Since 1.21.6 text with alpha 0 is invisible, so plain RGB colours get full alpha.
        int argb = (color >>> 24) == 0 ? 0xFF000000 | color : color;
        var pose = c.g.pose();
        pose.pushMatrix();
        pose.translate(x, y);
        pose.scale(c.textScale, c.textScale);
        c.g.drawString(c.font, text, 0, 0, argb, true);
        pose.popMatrix();
    }

    /** AppleSkin-style flashing for preview values. */
    private static int flashAlpha(float time) {
        return (int) (110 + 145 * (0.5F + 0.5F * Mth.sin(time * 0.25F)));
    }

    private static int withAlpha(int rgb, int alpha) {
        return (Mth.clamp(alpha, 8, 255) << 24) | (rgb & 0xFFFFFF);
    }

    private static int lerpColor(float t, int from, int to) {
        t = Mth.clamp(t, 0F, 1F);
        int r = (int) Mth.lerp(t, (from >> 16) & 0xFF, (to >> 16) & 0xFF);
        int g = (int) Mth.lerp(t, (from >> 8) & 0xFF, (to >> 8) & 0xFF);
        int b = (int) Mth.lerp(t, from & 0xFF, to & 0xFF);
        return (r << 16) | (g << 8) | b;
    }

    /** "3", "2.5": one decimal, dropped when whole. */
    private static String formatDecimal(float value) {
        float rounded = Math.round(value * 10F) / 10F;
        if (rounded == (int) rounded) return Integer.toString((int) rounded);
        return String.format(Locale.ROOT, "%.1f", rounded);
    }

    private static ResourceLocation tex(String name) {
        return ResourceLocation.fromNamespaceAndPath(BetterhpMod.MOD_ID, "textures/gui/" + name + ".png");
    }
}
