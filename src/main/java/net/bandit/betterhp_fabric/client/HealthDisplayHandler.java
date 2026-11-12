package net.bandit.betterhp_fabric.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.bandit.betterhp_fabric.config.ConfigManager;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.bandit.betterhp_fabric.integration.AppleSkinCompat;

public class HealthDisplayHandler implements HudRenderCallback {

    private static final Identifier HEALTH_ICON = new Identifier("betterhp_fabric", "textures/gui/health_icon.png");
    private static final Identifier HUNGER_ICON = new Identifier("betterhp_fabric", "textures/gui/hunger_icon.png");
    private static final Identifier ARMOR_ICON = new Identifier("betterhp_fabric", "textures/gui/armor_icon.png");
    private static final Identifier BREATHE_ICON = new Identifier("betterhp_fabric", "textures/gui/breathe_icon.png");
    private static final Identifier TOUGHNESS_ICON = new Identifier("betterhp_fabric", "textures/gui/toughness_icon.png");
    private static final Identifier HARDCORE_HEALTH_ICON = new Identifier("betterhp_fabric", "textures/gui/hardcore_health_icon.png");
    private static final Identifier FROZEN_HEALTH_ICON = new Identifier("betterhp_fabric", "textures/gui/frozen_health_icon.png");
    private static final Identifier POISONED_HEALTH_ICON = new Identifier("betterhp_fabric", "textures/gui/poisoned_health_icon.png");
    private static final Identifier WITHERED_HEALTH_ICON = new Identifier("betterhp_fabric", "textures/gui/withered_health_icon.png");
    private static final int FROZEN_COLOR = 0x9BE7FF;

    @Override
    public void onHudRender(DrawContext context, float tickDelta) {
        MinecraftClient client = MinecraftClient.getInstance();
        PlayerEntity player = client.player;

        // Same rule vanilla uses for its bars: nothing in creative/spectator (and never NPE before the world exists).
        if (player == null || client.options.hudHidden || client.interactionManager == null
                || !client.interactionManager.hasStatusBars() || client.world == null) {
            return;
        }
        float time = player.age + tickDelta;
        AppleSkinCompat.FoodPreview food = AppleSkinCompat.foodPreview(player);

        boolean isHardcore = client.world.getLevelProperties().isHardcore();

        int health = MathHelper.ceil(player.getHealth());
        int maxHealth = MathHelper.ceil(player.getMaxHealth());
        int absorption = MathHelper.ceil(player.getAbsorptionAmount());
        int armorValue = player.getArmor();
        int hunger = player.getHungerManager().getFoodLevel();
        int air = Math.max(0, player.getAir()); // negative while drowning
        int maxAir = player.getMaxAir();

        boolean statusIcons = ConfigManager.showStatusHealthIcons();
        boolean frozen = statusIcons && player.isFrozen();
        int healthColor = frozen
                ? interpolateColor(0.5f + 0.5f * MathHelper.sin(time * 0.15f), FROZEN_COLOR, 0xE6FAFF)
                : determineHealthColor(player);
        int hungerColor = determineHungerColor(hunger, 20);
        int breatheColor = 0x00BFFF;

        int screenWidth = client.getWindow().getScaledWidth();
        int screenHeight = client.getWindow().getScaledHeight();

        int healthPosX = screenWidth / 2 + ConfigManager.healthDisplayX();
        int healthPosY = screenHeight - ConfigManager.healthDisplayY();
        int hungerPosX = screenWidth / 2 + ConfigManager.hungerDisplayX();
        int hungerPosY = screenHeight - ConfigManager.hungerDisplayY();
        int armorPosX = screenWidth / 2 + ConfigManager.armorDisplayX();
        int armorPosY = screenHeight - ConfigManager.armorDisplayY();
        int breathePosX = screenWidth / 2 + ConfigManager.breatheDisplayX();
        int breathePosY = screenHeight - ConfigManager.breatheDisplayY();

        client.getProfiler().push("betterhp_healthIcon");
        if (ConfigManager.showHealthIcon()) {
            Identifier icon = statusIcons && player.hasStatusEffect(StatusEffects.POISON) ? POISONED_HEALTH_ICON
                    : statusIcons && player.hasStatusEffect(StatusEffects.WITHER) ? WITHERED_HEALTH_ICON
                    : frozen ? FROZEN_HEALTH_ICON
                    : isHardcore ? HARDCORE_HEALTH_ICON : HEALTH_ICON;
            renderIcon(context, icon, healthPosX - 18, healthPosY - 4);
        }
        if (ConfigManager.showHealthValue()) {
            int shakeOffset = (float) health / maxHealth < 0.2f ? (int) (Math.sin(player.age * 0.6f) * 2) : 0;
            drawShadowedText(context, client, health + "/" + maxHealth, healthPosX + shakeOffset, healthPosY, healthColor);

            if (absorption > 0) {
                int width = client.textRenderer.getWidth(health + "/" + maxHealth);
                drawShadowedText(context, client, "+" + absorption, healthPosX + width + 3, healthPosY, 0xFFFF00);
            }

            // AppleSkin: health the held food will regenerate, floating just above the value.
            if (food != null && food.health() > 0F) {
                drawShadowedText(context, client, "+" + formatDecimal(food.health()), healthPosX, healthPosY - 10, withAlpha(0x55FF55, flashAlpha(time)));
            }
        }
        client.getProfiler().pop();

        client.getProfiler().push("betterhp_hungerIcon");
        if (ConfigManager.showHungerIcon()) {
            renderIcon(context, HUNGER_ICON, hungerPosX, hungerPosY - 4);
        }
        if (ConfigManager.showHungerValue()) {
            String hungerText = hunger + "/20";
            drawShadowedText(context, client, hungerText, hungerPosX - client.textRenderer.getWidth(hungerText), hungerPosY, hungerColor);

            int textWidth = client.textRenderer.getWidth(hungerText);
            int textStart = hungerPosX - textWidth;

            // AppleSkin: exhaustion bar under the number.
            float exhaustion = AppleSkinCompat.exhaustionFraction(player);
            if (exhaustion >= 0F) {
                context.fill(textStart, hungerPosY + 10, hungerPosX, hungerPosY + 11, 0x60000000);
                int filled = Math.round(textWidth * exhaustion);
                if (filled > 0) context.fill(textStart, hungerPosY + 10, textStart + filled, hungerPosY + 11, 0xC0FFFFFF);
            }

            // AppleSkin: what the held food restores, floating just above the number.
            if (food != null && (food.hunger() > 0 || food.saturation() > 0F)) {
                int alpha = flashAlpha(time);
                String preview = food.hunger() > 0 ? "+" + food.hunger() : "";
                if (!preview.isEmpty()) drawShadowedText(context, client, preview, textStart, hungerPosY - 10, withAlpha(0xFF7518, alpha));
                if (food.saturation() > 0F) {
                    int sx = textStart + (preview.isEmpty() ? 0 : client.textRenderer.getWidth(preview) + 3);
                    drawShadowedText(context, client, "+" + formatDecimal(food.saturation()), sx, hungerPosY - 10, withAlpha(0xFFD700, alpha));
                }
            }
        }
        client.getProfiler().pop();

        client.getProfiler().push("betterhp_armorIcon");
        // Only with armor on (it used to show an armor icon with "0" at all times).
        if (ConfigManager.showArmorIcon() && armorValue > 0) {
            renderIcon(context, ARMOR_ICON, armorPosX - 18, armorPosY - 4);
        }
        if (ConfigManager.showArmorValue() && armorValue > 0) {
            drawShadowedText(context, client, String.valueOf(armorValue), armorPosX, armorPosY, 0xFFFFFF);
        }
        client.getProfiler().pop();

        client.getProfiler().push("betterhp_toughnessIcon");
        int toughness = (int) player.getAttributeValue(EntityAttributes.GENERIC_ARMOR_TOUGHNESS);
        // Only with toughness (it used to show "0" at all times).
        if (toughness > 0 && (ConfigManager.showToughnessIcon() || ConfigManager.showToughnessValue())) {
            int toughnessPosX = armorPosX + ConfigManager.toughnessDisplayX();
            int toughnessPosY = armorPosY + ConfigManager.toughnessDisplayY();

            if (ConfigManager.showToughnessIcon()) {
                renderIcon(context, TOUGHNESS_ICON, toughnessPosX - 18, toughnessPosY - 4);
            }
            if (ConfigManager.showToughnessValue()) {
                drawShadowedText(context, client, String.valueOf(toughness), toughnessPosX, toughnessPosY, 0xADD8E6);
            }
        }
        client.getProfiler().pop();

        client.getProfiler().push("betterhp_breatheIcon");
        if (player.isSubmergedInWater() || air < maxAir) {
            String breatheText = (air / 20) + "/" + (maxAir / 20);

            if (ConfigManager.showBreatheIcon()) {
                renderIcon(context, BREATHE_ICON, breathePosX, breathePosY - 4);
            }
            if (ConfigManager.showOxygenValue()) {
                drawShadowedText(context, client, breatheText, breathePosX - client.textRenderer.getWidth(breatheText), breathePosY, breatheColor);
            }
        }
        client.getProfiler().pop();
    }
    /** AppleSkin-style flashing for preview values. */
    private static int flashAlpha(float time) {
        return (int) (110 + 145 * (0.5f + 0.5f * MathHelper.sin(time * 0.25f)));
    }

    private static int withAlpha(int rgb, int alpha) {
        // The font renderer treats alpha < 4 as fully opaque, so never go that low.
        return (MathHelper.clamp(alpha, 8, 255) << 24) | (rgb & 0xFFFFFF);
    }

    /** "3", "2.5": one decimal, dropped when whole. */
    private static String formatDecimal(float value) {
        float rounded = Math.round(value * 10f) / 10f;
        if (rounded == (int) rounded) return Integer.toString((int) rounded);
        return String.format(java.util.Locale.ROOT, "%.1f", rounded);
    }

    private void renderIcon(DrawContext context, Identifier icon, int x, int y) {
        RenderSystem.setShaderTexture(0, icon);
        context.drawTexture(icon, x, y, 0, 0, 16, 16, 16, 16);
    }

    private void drawShadowedText(DrawContext context, MinecraftClient client, String text, int x, int y, int color) {
        context.drawText(client.textRenderer, text, x, y, color, true);
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

    private int determineHealthColor(PlayerEntity player) {
        if (ConfigManager.useStaticHealthColor()) {
            return ConfigManager.staticHealthColor();
        }

        if (player.hasStatusEffect(StatusEffects.POISON)) return 0x9ACD32;
        if (player.hasStatusEffect(StatusEffects.WITHER)) return 0x8C8484; // readable on dark backgrounds

        float health = player.getHealth();
        float maxHealth = player.getMaxHealth();
        float ratio = health / maxHealth;

        if (ratio < 0.2f) {
            float pulse = (float)(Math.sin(player.age * 0.3f) * 0.5f + 0.5f);
            return interpolateColor(pulse, 0x800000, 0xFF0000);
        }

        if (ratio > 0.5f) {
            return interpolateColor((1.0f - ratio) * 2f, 0x00FF00, 0xFFFF00);
        } else {
            return interpolateColor((0.5f - ratio) * 2f, 0xFFFF00, 0xFF0000);
        }
    }
    private int determineHungerColor(int hunger, int maxHunger) {
        if (ConfigManager.useStaticHungerColor()) {
            return ConfigManager.staticHungerColor();
        }

        float ratio = (float) hunger / maxHunger;

        if (ratio > 0.5f) {
            return interpolateColor((1.0f - ratio) * 2f, 0xFFFF00, 0xFFA500);
        } else {
            return interpolateColor((0.5f - ratio) * 2f, 0xFFA500, 0xFF4500);
        }
    }

}
