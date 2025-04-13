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
import net.minecraft.world.GameMode;

public class HealthDisplayHandler implements HudRenderCallback {

    private static final Identifier HEALTH_ICON = new Identifier("betterhp_fabric", "textures/gui/health_icon.png");
    private static final Identifier HUNGER_ICON = new Identifier("betterhp_fabric", "textures/gui/hunger_icon.png");
    private static final Identifier ARMOR_ICON = new Identifier("betterhp_fabric", "textures/gui/armor_icon.png");
    private static final Identifier BREATHE_ICON = new Identifier("betterhp_fabric", "textures/gui/breathe_icon.png");
    private static final Identifier TOUGHNESS_ICON = new Identifier("betterhp_fabric", "textures/gui/toughness_icon.png");
    private static final Identifier HARDCORE_HEALTH_ICON = new Identifier("betterhp_fabric", "textures/gui/hardcore_health_icon.png");

    @Override
    public void onHudRender(DrawContext context, float tickDelta) {
        MinecraftClient client = MinecraftClient.getInstance();
        PlayerEntity player = client.player;

        if (player == null || client.options.hudHidden || client.interactionManager.getCurrentGameMode() == GameMode.CREATIVE || client.interactionManager.getCurrentGameMode() == GameMode.SPECTATOR) {
            return;
        }

        boolean isHardcore = client.world.getLevelProperties().isHardcore();

        int health = MathHelper.ceil(player.getHealth());
        int maxHealth = MathHelper.ceil(player.getMaxHealth());
        int absorption = MathHelper.ceil(player.getAbsorptionAmount());
        int armorValue = player.getArmor();
        int hunger = player.getHungerManager().getFoodLevel();
        int air = player.getAir();
        int maxAir = player.getMaxAir();

        int healthColor = determineHealthColor(player);
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
            Identifier icon = isHardcore ? HARDCORE_HEALTH_ICON : HEALTH_ICON;
            renderIcon(context, icon, healthPosX - 18, healthPosY - 4);
            int shakeOffset = (float) health / maxHealth < 0.2f ? (int) (Math.sin(player.age * 0.6f) * 2) : 0;
            drawShadowedText(context, client, health + "/" + maxHealth, healthPosX + shakeOffset, healthPosY, healthColor);

            if (absorption > 0) {
                int width = client.textRenderer.getWidth(health + "/" + maxHealth);
                drawShadowedText(context, client, "+" + absorption, healthPosX + width + 3, healthPosY, 0xFFFF00);
            }
        }
        client.getProfiler().pop();

        client.getProfiler().push("betterhp_hungerIcon");
        if (ConfigManager.showHungerIcon()) {
            String hungerText = hunger + "/20";
            if (ConfigManager.getConfigData().showNumericHunger) {
                drawShadowedText(context, client, hungerText, hungerPosX - client.textRenderer.getWidth(hungerText), hungerPosY, hungerColor);
            }
            renderIcon(context, HUNGER_ICON, hungerPosX, hungerPosY - 4);
        }
        client.getProfiler().pop();

        client.getProfiler().push("betterhp_armorIcon");
        if (ConfigManager.showArmorIcon()) {
            renderIcon(context, ARMOR_ICON, armorPosX - 18, armorPosY - 4);
            drawShadowedText(context, client, String.valueOf(armorValue), armorPosX, armorPosY, 0xFFFFFF);
        }
        client.getProfiler().pop();

        client.getProfiler().push("betterhp_toughnessIcon");
        if (ConfigManager.showToughnessIcon()) {
            int toughness = (int) player.getAttributeValue(EntityAttributes.GENERIC_ARMOR_TOUGHNESS);
            int toughnessPosX = armorPosX + ConfigManager.toughnessDisplayX();
            int toughnessPosY = armorPosY + ConfigManager.toughnessDisplayY();

            renderIcon(context, TOUGHNESS_ICON, toughnessPosX - 18, toughnessPosY - 4);
            drawShadowedText(context, client, String.valueOf(toughness), toughnessPosX, toughnessPosY, 0xADD8E6);
        }
        client.getProfiler().pop();

        client.getProfiler().push("betterhp_breatheIcon");
        if (ConfigManager.showBreatheIcon() && (player.isSubmergedInWater() || air < maxAir)) {
            String breatheText = (air / 20) + "/" + (maxAir / 20);
            if (ConfigManager.getConfigData().showNumericOxygen) {
                drawShadowedText(context, client, breatheText, breathePosX - client.textRenderer.getWidth(breatheText), breathePosY, breatheColor);
            }
            renderIcon(context, BREATHE_ICON, breathePosX, breathePosY - 4);
        }
        client.getProfiler().pop();
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
        if (player.hasStatusEffect(StatusEffects.POISON)) return 0x9ACD32;
        if (player.hasStatusEffect(StatusEffects.WITHER)) return 0x403030;

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
        float ratio = (float) hunger / maxHunger;
        if (ratio > 0.5f) {
            return interpolateColor((1.0f - ratio) * 2f, 0xFFFF00, 0xFFA500);
        } else {
            return interpolateColor((0.5f - ratio) * 2f, 0xFFA500, 0xFF4500);
        }
    }
}
