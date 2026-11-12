package net.bandit.betterhp_fabric.client;

import net.bandit.betterhp_fabric.config.ConfigManager;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.bandit.betterhp_fabric.integration.AppleSkinCompat;
import net.minecraft.client.renderer.RenderPipelines;
import com.mojang.blaze3d.systems.RenderSystem;

public class HealthDisplayHandler implements HudRenderCallback {

    private static final ResourceLocation HEALTH_ICON =
            ResourceLocation.fromNamespaceAndPath("betterhp_fabric", "textures/gui/health_icon.png");
    private static final ResourceLocation HUNGER_ICON =
            ResourceLocation.fromNamespaceAndPath("betterhp_fabric", "textures/gui/hunger_icon.png");
    private static final ResourceLocation NO_HUNGER_ICON =
            ResourceLocation.fromNamespaceAndPath("betterhp_fabric", "textures/gui/no_hunger_icon.png");
    private static final ResourceLocation ARMOR_ICON =
            ResourceLocation.fromNamespaceAndPath("betterhp_fabric", "textures/gui/armor_icon.png");
    private static final ResourceLocation BREATHE_ICON =
            ResourceLocation.fromNamespaceAndPath("betterhp_fabric", "textures/gui/breathe_icon.png");
    private static final ResourceLocation TOUGHNESS_ICON =
            ResourceLocation.fromNamespaceAndPath("betterhp_fabric", "textures/gui/toughness_icon.png");
    private static final ResourceLocation HARDCORE_HEALTH_ICON =
            ResourceLocation.fromNamespaceAndPath("betterhp_fabric", "textures/gui/hardcore_health_icon.png");
    private static final ResourceLocation MOUNT_ICON =
            ResourceLocation.fromNamespaceAndPath("betterhp_fabric", "textures/gui/mount_icon.png");
    private static final ResourceLocation FROZEN_HEALTH_ICON =
            ResourceLocation.fromNamespaceAndPath("betterhp_fabric", "textures/gui/frozen_health_icon.png");
    private static final ResourceLocation POISONED_HEALTH_ICON =
            ResourceLocation.fromNamespaceAndPath("betterhp_fabric", "textures/gui/poisoned_health_icon.png");
    private static final ResourceLocation WITHERED_HEALTH_ICON =
            ResourceLocation.fromNamespaceAndPath("betterhp_fabric", "textures/gui/withered_health_icon.png");
    private static final int FROZEN_COLOR = 0x9BE7FF;
    private static final float BOUNCE_TICKS = 10F;

    // Frame-rate independent "pop" when armor/toughness change (start time in ticks).
    private float armorBounceStart = -BOUNCE_TICKS;
    private float toughnessBounceStart = -BOUNCE_TICKS;
    private int lastRenderedArmorValue = Integer.MIN_VALUE; // MIN_VALUE = no bounce on join
    private int lastRenderedToughness = Integer.MIN_VALUE;

    @Override
    public void onHudRender(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;

        if (minecraft.options.hideGui) return;

        // Same rule vanilla uses for its bars: nothing in creative/spectator.
        if (player == null || minecraft.gameMode == null || !minecraft.gameMode.canHurtPlayer()) {
            return;
        }
        float time = player.tickCount + deltaTracker.getGameTimeDeltaPartialTick(true);
        AppleSkinCompat.FoodPreview food = AppleSkinCompat.foodPreview(player);
        guiGraphics.nextStratum();

        boolean isHardcore = minecraft.level != null && minecraft.level.getLevelData().isHardcore();

        // Stats
        int health = Mth.ceil(player.getHealth());
        int maxHealth = Mth.ceil(player.getMaxHealth());
        int absorption = Mth.ceil(player.getAbsorptionAmount());
        int armorValue = player.getArmorValue();
        int hunger = player.getFoodData().getFoodLevel();
        int saturation = Mth.ceil(player.getFoodData().getSaturationLevel());
        int air = Math.max(0, player.getAirSupply()); // negative while drowning
        int maxAir = player.getMaxAirSupply();

        boolean statusIcons = ConfigManager.showStatusHealthIcons();
        boolean frozen = statusIcons && player.isFullyFrozen();
        int healthColor = frozen
                ? interpolateColor(0.5f + 0.5f * Mth.sin(time * 0.15f), FROZEN_COLOR, 0xE6FAFF)
                : determineHealthColor(player);
        int hungerColor = determineHungerColor(hunger, 20);
        int breatheColor = 0x00BFFF;

        int screenWidth = guiGraphics.guiWidth();
        int screenHeight = guiGraphics.guiHeight();

        int healthPosX = screenWidth / 2 + ConfigManager.healthDisplayX();
        int healthPosY = screenHeight - ConfigManager.healthDisplayY();

        int hungerPosX = screenWidth / 2 + ConfigManager.hungerDisplayX();
        int hungerPosY = screenHeight - ConfigManager.hungerDisplayY();

        int armorPosX = screenWidth / 2 + ConfigManager.armorDisplayX();
        int armorPosY = screenHeight - ConfigManager.armorDisplayY();

        int breathePosX = screenWidth / 2 + ConfigManager.breatheDisplayX();
        int breathePosY = screenHeight - ConfigManager.breatheDisplayY();

        int saturationPosX = screenWidth / 2 + ConfigManager.saturationDisplayX();
        int saturationPosY = screenHeight - ConfigManager.saturationDisplayY();

        // 1.21.10 (Mojang mappings): Profiler.get() -> ProfilerFiller
        ProfilerFiller profiler = Profiler.get();

        // --- Health ---
        profiler.push("betterhp_healthIcon");
        try {
            if (ConfigManager.showHealthIcon()) {
                ResourceLocation icon = statusIcons && player.hasEffect(MobEffects.POISON) ? POISONED_HEALTH_ICON
                        : statusIcons && player.hasEffect(MobEffects.WITHER) ? WITHERED_HEALTH_ICON
                        : frozen ? FROZEN_HEALTH_ICON
                        : isHardcore ? HARDCORE_HEALTH_ICON : HEALTH_ICON;
                renderIcon(guiGraphics, icon, healthPosX - 18, healthPosY - 4);

                int shakeOffset = 0;
                if (maxHealth > 0 && (float) health / (float) maxHealth < 0.2f) {
                    shakeOffset = (int) (Math.sin(player.tickCount * 0.6f) * 2);
                }

                drawShadowedText(guiGraphics, minecraft,
                        health + "/" + maxHealth, healthPosX + shakeOffset, healthPosY, healthColor);

                if (absorption > 0) {
                    int healthTextWidth = minecraft.font.width(health + "/" + maxHealth);
                    drawShadowedText(guiGraphics, minecraft,
                            "+" + absorption, healthPosX + healthTextWidth + 3, healthPosY, 0xFFFF00);
                }

                // AppleSkin: health the held food will regenerate, floating just above the value.
                if (food != null && food.health() > 0F) {
                    drawArgbText(guiGraphics, minecraft, "+" + formatDecimal(food.health()), healthPosX, healthPosY - 10,
                            withAlpha(0x55FF55, flashAlpha(time)));
                }
            }
        } finally {
            profiler.pop();
        }

        // --- Hunger ---
        profiler.push("betterhp_hungerIcon");
        try {
            if (ConfigManager.showHungerIcon()) {
                boolean hasHungerEffect = player.hasEffect(MobEffects.HUNGER);
                String hungerText = hunger + "/20";

                if (ConfigManager.getConfigData().showNumericHunger) {
                    if (hasHungerEffect) {
                        float shakeX = (float) (Math.sin(player.tickCount * 0.6f) * 1.5f);
                        float shakeY = (float) (Math.cos(player.tickCount * 0.5f) * 1.5f);
                        float pulseScale = 1.0f + 0.1f * (float) Math.sin(player.tickCount * 0.3f);

                        guiGraphics.pose().pushMatrix();
                        guiGraphics.pose().translate(hungerPosX + shakeX, hungerPosY + shakeY);
                        guiGraphics.pose().scale(pulseScale, pulseScale);
                        drawShadowedText(guiGraphics, minecraft, hungerText,
                                -minecraft.font.width(hungerText), 0, 0x9ACD32);
                        guiGraphics.pose().popMatrix();

                    } else {
                        drawShadowedText(guiGraphics, minecraft, hungerText,
                                hungerPosX - minecraft.font.width(hungerText), hungerPosY, hungerColor);
                    }

                    int textWidth = minecraft.font.width(hungerText);
                    int textStart = hungerPosX - textWidth;

                    // AppleSkin: exhaustion bar under the number.
                    float exhaustion = AppleSkinCompat.exhaustionFraction(player);
                    if (exhaustion >= 0F) {
                        guiGraphics.fill(textStart, hungerPosY + 10, hungerPosX, hungerPosY + 11, 0x60000000);
                        int filled = Math.round(textWidth * exhaustion);
                        if (filled > 0) guiGraphics.fill(textStart, hungerPosY + 10, textStart + filled, hungerPosY + 11, 0xC0FFFFFF);
                    }

                    // AppleSkin: what the held food restores, floating just above the number.
                    if (food != null && (food.hunger() > 0 || food.saturation() > 0F)) {
                        int alpha = flashAlpha(time);
                        String preview = food.hunger() > 0 ? "+" + food.hunger() : "";
                        if (!preview.isEmpty()) drawArgbText(guiGraphics, minecraft, preview, textStart, hungerPosY - 10, withAlpha(0xFF7518, alpha));
                        if (food.saturation() > 0F) {
                            int sx = textStart + (preview.isEmpty() ? 0 : minecraft.font.width(preview) + 3);
                            drawArgbText(guiGraphics, minecraft, "+" + formatDecimal(food.saturation()), sx, hungerPosY - 10, withAlpha(0xFFD700, alpha));
                        }
                    }
                }

                ResourceLocation hungerIconToUse = hasHungerEffect ? NO_HUNGER_ICON : HUNGER_ICON;
                renderIcon(guiGraphics, hungerIconToUse, hungerPosX, hungerPosY - 4);
            }
        } finally {
            profiler.pop();
        }

        // --- Saturation ---
        profiler.push("betterhp_saturationText");
        try {
            if (ConfigManager.showSaturation() && saturation > 0) {
                String saturationText = saturation + "+";
                drawShadowedText(guiGraphics, minecraft, saturationText, saturationPosX, saturationPosY, 0xFFD700);
            }
        } finally {
            profiler.pop();
        }

        // --- Armor & Toughness ---
        profiler.push("betterhp_armorIcon");
        try {
            int toughness = 0;
            var toughnessAttr = player.getAttribute(Attributes.ARMOR_TOUGHNESS);
            if (toughnessAttr != null) {
                toughness = Mth.ceil(toughnessAttr.getValue());
            }

            if (lastRenderedArmorValue != Integer.MIN_VALUE && armorValue != lastRenderedArmorValue) armorBounceStart = time;
            lastRenderedArmorValue = armorValue;

            if (lastRenderedToughness != Integer.MIN_VALUE && toughness != lastRenderedToughness) toughnessBounceStart = time;
            lastRenderedToughness = toughness;

            // Armor
            if (ConfigManager.showArmorIcon() && armorValue > 0) {
                float scale = bounceScale(armorBounceStart, time);

                float pulse = (armorValue == 20)
                        ? (0.9f + 0.1f * (float) Math.sin(player.tickCount * 0.2f))
                        : 1.0f;

                guiGraphics.pose().pushMatrix();
                guiGraphics.pose().translate(armorPosX - 10, armorPosY + 2);
                guiGraphics.pose().scale(scale, scale);
                guiGraphics.pose().translate(-(armorPosX - 10), -(armorPosY + 2));

//                guiGraphics.setColor(pulse, pulse, pulse, 1.0f);
                renderIcon(guiGraphics, ARMOR_ICON, armorPosX - 18, armorPosY - 4);
//                guiGraphics.setColor(1f, 1f, 1f, 1f);

                drawShadowedText(guiGraphics, minecraft,
                        String.valueOf(armorValue), armorPosX, armorPosY, 0xFFFFFF);

                guiGraphics.pose().popMatrix();
            }

            // Toughness
            if (ConfigManager.showToughnessIcon() && toughness > 0) {
                int toughnessPosX = armorPosX + ConfigManager.toughnessDisplayX();
                int toughnessPosY = armorPosY + ConfigManager.toughnessDisplayY();

                float scale = bounceScale(toughnessBounceStart, time);

                float pulse = (toughness >= 5)
                        ? (0.9f + 0.1f * (float) Math.sin(player.tickCount * 0.2f))
                        : 1.0f;

                guiGraphics.pose().pushMatrix();
                guiGraphics.pose().translate(toughnessPosX - 10, toughnessPosY + 2);
                guiGraphics.pose().scale(scale, scale);
                guiGraphics.pose().translate(-(toughnessPosX - 10), -(toughnessPosY + 2));

//                guiGraphics.setColor(pulse, pulse, pulse, 1.0f);
                renderIcon(guiGraphics, TOUGHNESS_ICON, toughnessPosX - 18, toughnessPosY - 4);
//                guiGraphics.setColor(1f, 1f, 1f, 1f);

                drawShadowedText(guiGraphics, minecraft,
                        String.valueOf(toughness), toughnessPosX, toughnessPosY, 0xADD8E6);

                guiGraphics.pose().popMatrix();
            }
        } finally {
            profiler.pop();
        }
        profiler.push("betterhp_breatheIcon");
        try {
            if (ConfigManager.showBreatheIcon() && (player.isUnderWater() || air < maxAir)) {
                String breatheText = (air / 20) + "/" + (maxAir / 20);

                if (ConfigManager.getConfigData().showNumericOxygen) {
                    drawShadowedText(guiGraphics, minecraft, breatheText,
                            breathePosX - minecraft.font.width(breatheText), breathePosY, 0x00BFFF);
                }

                renderIcon(guiGraphics, BREATHE_ICON, breathePosX, breathePosY - 4);
            }
        } finally {
            profiler.pop();
        }

        // --- Mount ---
        profiler.push("betterhp_mountIcon");
        try {
            if (ConfigManager.showHealthIcon()) {
                LivingEntity mount = getMountWithHealth(player);
                if (mount != null) {
                    int mountHealth = Mth.ceil(mount.getHealth());
                    int mountMaxHealth = Mth.ceil(mount.getMaxHealth());

                    int mountColor = determineHealthColor(mountHealth, mountMaxHealth);

                    int mountPosX = screenWidth / 2 + ConfigManager.mountDisplayX();
                    int mountPosY = screenHeight - ConfigManager.mountDisplayY();

                    renderIcon(guiGraphics, MOUNT_ICON, mountPosX - 18, mountPosY - 4);
                    drawShadowedText(guiGraphics, minecraft,
                            mountHealth + "/" + mountMaxHealth, mountPosX, mountPosY, mountColor);
                }
            }
        } finally {
            profiler.pop();
        }
    }

    private static float bounceScale(float start, float now) {
        float progress = (now - start) / BOUNCE_TICKS;
        // progress < 0 happens when tickCount resets (respawn / dimension change).
        if (progress < 0f || progress >= 1f) return 1f;
        return 1f + 0.2f * Mth.sin(progress * Mth.PI);
    }

    /** AppleSkin-style flashing for preview values. */
    private static int flashAlpha(float time) {
        return (int) (110 + 145 * (0.5f + 0.5f * Mth.sin(time * 0.25f)));
    }

    private static int withAlpha(int rgb, int alpha) {
        return (Mth.clamp(alpha, 8, 255) << 24) | (rgb & 0xFFFFFF);
    }

    /** "3", "2.5": one decimal, dropped when whole. */
    private static String formatDecimal(float value) {
        float rounded = Math.round(value * 10f) / 10f;
        if (rounded == (int) rounded) return Integer.toString((int) rounded);
        return String.format(java.util.Locale.ROOT, "%.1f", rounded);
    }

    /** Like drawShadowedText but keeps the given alpha (used for flashing previews). */
    private void drawArgbText(GuiGraphics guiGraphics, Minecraft minecraft, String text, int x, int y, int argb) {
        guiGraphics.drawString(minecraft.font, text, x, y, argb, true);
    }

    private void renderIcon(GuiGraphics gg, ResourceLocation icon, int x, int y) {
        gg.blit(RenderPipelines.GUI_TEXTURED, icon, x, y, 0, 0, 16, 16, 16, 16);
    }

    private void drawShadowedText(GuiGraphics guiGraphics, Minecraft minecraft, String text, int x, int y, int rgb) {
        int argb = 0xFF000000 | (rgb & 0x00FFFFFF);
        guiGraphics.drawString(minecraft.font, text, x, y, argb, true);
    }


    private int interpolateColor(float ratio, int colorStart, int colorEnd) {
        int r1 = (colorStart >> 16) & 0xFF;
        int g1 = (colorStart >> 8) & 0xFF;
        int b1 = colorStart & 0xFF;

        int r2 = (colorEnd >> 16) & 0xFF;
        int g2 = (colorEnd >> 8) & 0xFF;
        int b2 = colorEnd & 0xFF;

        int r = (int) (r1 + (r2 - r1) * ratio);
        int g = (int) (g1 + (g2 - g1) * ratio);
        int b = (int) (b1 + (b2 - b1) * ratio);

        return (r << 16) | (g << 8) | b;
    }

    private int determineHealthColor(Player player) {
        if (player.hasEffect(MobEffects.POISON)) return 0x9ACD32;
        if (player.hasEffect(MobEffects.WITHER)) return 0x8C8484; // readable on dark backgrounds

        float health = player.getHealth();
        float maxHealth = player.getMaxHealth();
        float ratio = maxHealth <= 0 ? 0 : (health / maxHealth);

        if (ratio < 0.2f) {
            float pulse = (float) (Math.sin(player.tickCount * 0.3f) * 0.5f + 0.5f);
            return interpolateColor(pulse, 0x800000, 0xFF0000);
        }

        if (ratio > 0.5f) {
            return interpolateColor((1.0f - ratio) * 2f, 0x00FF00, 0xFFFF00);
        } else {
            return interpolateColor((0.5f - ratio) * 2f, 0xFFFF00, 0xFF0000);
        }
    }

    private int determineHealthColor(int current, int max) {
        float ratio = max <= 0 ? 0 : (float) current / (float) max;

        if (ratio < 0.2f) {
            float pulse = (float) (Math.sin(System.currentTimeMillis() * 0.005f) * 0.5f + 0.5f);
            return interpolateColor(pulse, 0x800000, 0xFF0000);
        }

        if (ratio > 0.5f) {
            return interpolateColor((1.0f - ratio) * 2f, 0x00FF00, 0xFFFF00);
        } else {
            return interpolateColor((0.5f - ratio) * 2f, 0xFFFF00, 0xFF0000);
        }
    }

    private int determineHungerColor(int hunger, int maxHunger) {
        float ratio = (float) hunger / (float) maxHunger;

        if (ratio > 0.5f) {
            return interpolateColor((1.0f - ratio) * 2f, 0xFFFF00, 0xFFA500);
        } else {
            return interpolateColor((0.5f - ratio) * 2f, 0xFFA500, 0xFF4500);
        }
    }

    private static LivingEntity getMountWithHealth(Player player) {
        if (player == null) return null;
        var vehicle = player.getVehicle();
        if (vehicle instanceof LivingEntity living && living.showVehicleHealth()) {
            return living;
        }
        return null;
    }

}
