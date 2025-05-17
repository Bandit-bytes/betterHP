package net.bandit.betterhp_fabric.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.bandit.betterhp_fabric.config.ConfigManager;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;

public class HealthDisplayHandler implements HudRenderCallback {

    private static final ResourceLocation HEALTH_ICON = ResourceLocation.fromNamespaceAndPath("betterhp_fabric", "textures/gui/health_icon.png");
    private static final ResourceLocation HUNGER_ICON = ResourceLocation.fromNamespaceAndPath("betterhp_fabric", "textures/gui/hunger_icon.png");
    private static final ResourceLocation NO_HUNGER_ICON = ResourceLocation.fromNamespaceAndPath("betterhp_fabric", "textures/gui/no_hunger_icon.png");
    private static final ResourceLocation ARMOR_ICON = ResourceLocation.fromNamespaceAndPath("betterhp_fabric", "textures/gui/armor_icon.png");
    private static final ResourceLocation BREATHE_ICON = ResourceLocation.fromNamespaceAndPath("betterhp_fabric", "textures/gui/breathe_icon.png");
    private static final ResourceLocation TOUGHNESS_ICON = ResourceLocation.fromNamespaceAndPath("betterhp_fabric", "textures/gui/toughness_icon.png");
    private static final ResourceLocation HARDCORE_HEALTH_ICON = ResourceLocation.fromNamespaceAndPath("betterhp_fabric", "textures/gui/hardcore_health_icon.png");
    private static final ResourceLocation MOUNT_ICON = ResourceLocation.fromNamespaceAndPath("betterhp_fabric", "textures/gui/mount_icon.png");

    @Override
    public void onHudRender(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;

        if (minecraft.options.hideGui) {
            return;
        }

        if (player == null || minecraft.gameMode.getPlayerMode() == GameType.CREATIVE || minecraft.gameMode.getPlayerMode() == GameType.SPECTATOR) {
            return;
        }

        boolean isHardcore = minecraft.level != null && minecraft.level.getLevelData().isHardcore();

        // Gather player stats
        int health = Mth.ceil(player.getHealth());
        int maxHealth = Mth.ceil(player.getMaxHealth());
        int absorption = Mth.ceil(player.getAbsorptionAmount());
        int armorValue = player.getArmorValue();
        int hunger = player.getFoodData().getFoodLevel();
        int saturation = Mth.ceil(player.getFoodData().getSaturationLevel());
        int air = player.getAirSupply();
        int maxAir = player.getMaxAirSupply();

        int healthColor = determineHealthColor(player);
        int hungerColor = determineHungerColor( hunger, 20);
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


        minecraft.getProfiler().push("betterhp_healthIcon");
        if (ConfigManager.showHealthIcon()) {
            ResourceLocation icon = isHardcore ? HARDCORE_HEALTH_ICON : HEALTH_ICON;
            renderIcon(guiGraphics, icon, healthPosX - 18, healthPosY - 4);
            int shakeOffset = 0;
            if ((float) health / maxHealth < 0.2f) {
                shakeOffset = (int) (Math.sin(player.tickCount * 0.6f) * 2);
            }

            drawShadowedText(guiGraphics, minecraft, health + "/" + maxHealth, healthPosX + shakeOffset, healthPosY, healthColor);

            if (absorption > 0) {
                int healthTextWidth = minecraft.font.width(health + "/" + maxHealth);
                drawShadowedText(guiGraphics, minecraft, "+" + absorption, healthPosX + healthTextWidth + 3, healthPosY, 0xFFFF00);
            }
        }
        minecraft.getProfiler().pop();

        minecraft.getProfiler().push("betterhp_hungerIcon");
        if (ConfigManager.showHungerIcon()) {
            boolean hasHungerEffect = player.hasEffect(MobEffects.HUNGER);
            String hungerText = hunger + "/20";

            if (ConfigManager.getConfigData().showNumericHunger) {
                if (hasHungerEffect) {
                    float shakeX = (float) (Math.sin(player.tickCount * 0.6f) * 1.5f);
                    float shakeY = (float) (Math.cos(player.tickCount * 0.5f) * 1.5f);
                    float pulseScale = 1.0f + 0.1f * (float) Math.sin(player.tickCount * 0.3f);

                    guiGraphics.pose().pushPose();
                    guiGraphics.pose().translate(hungerPosX + shakeX, hungerPosY + shakeY, 0);
                    guiGraphics.pose().scale(pulseScale, pulseScale, 1.0f);
                    drawShadowedText(guiGraphics, minecraft, hungerText, -minecraft.font.width(hungerText), 0, 0x9ACD32);
                    guiGraphics.pose().popPose();
                } else {
                    drawShadowedText(guiGraphics, minecraft, hungerText, hungerPosX - minecraft.font.width(hungerText), hungerPosY, hungerColor);
                }
            }

            ResourceLocation hungerIconToUse = hasHungerEffect ? NO_HUNGER_ICON : HUNGER_ICON;
            renderIcon(guiGraphics, hungerIconToUse, hungerPosX, hungerPosY - 4);
        }
        minecraft.getProfiler().pop();


        minecraft.getProfiler().push("betterhp_saturationText");
        if (ConfigManager.showSaturation() && saturation > 0) {
            String saturationText = saturation + "+";
            drawShadowedText(guiGraphics, minecraft, saturationText, saturationPosX, saturationPosY, 0xFFD700);
        }
        minecraft.getProfiler().pop();

        minecraft.getProfiler().push("betterhp_armorIcon");
        if (ConfigManager.showArmorIcon()&& armorValue > 0) {
            renderIcon(guiGraphics, ARMOR_ICON, armorPosX - 18, armorPosY - 4);
            drawShadowedText(guiGraphics, minecraft, String.valueOf(armorValue), armorPosX, armorPosY, 0xFFFFFF);
        }
        minecraft.getProfiler().pop();
        int toughness = Mth.ceil(player.getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.ARMOR_TOUGHNESS).getValue());
        if (ConfigManager.showToughnessIcon() && toughness > 0) {
            int toughnessPosX = armorPosX + ConfigManager.toughnessDisplayX();
            int toughnessPosY = armorPosY + ConfigManager.toughnessDisplayY();

            renderIcon(guiGraphics, TOUGHNESS_ICON, toughnessPosX - 18, toughnessPosY - 4);
            drawShadowedText(guiGraphics, minecraft, String.valueOf(toughness), toughnessPosX, toughnessPosY, 0xADD8E6);
        }

        minecraft.getProfiler().pop();


        minecraft.getProfiler().push("betterhp_breatheIcon");
        if (ConfigManager.showBreatheIcon() && (player.isUnderWater() || air < maxAir)) {
            String breatheText = (air / 20) + "/" + (maxAir / 20);
            if (ConfigManager.getConfigData().showNumericOxygen) {
                drawShadowedText(guiGraphics, minecraft, breatheText, breathePosX - minecraft.font.width(breatheText), breathePosY, breatheColor);
            }
            renderIcon(guiGraphics, BREATHE_ICON, breathePosX, breathePosY - 4);
        }
        minecraft.getProfiler().pop();

        minecraft.getProfiler().push("betterhp_mountIcon");

        if (ConfigManager.showHealthIcon()) {
            var mount = getMountWithHealth(player);
            if (mount != null) {
                int mountHealth = Mth.ceil(mount.getHealth());
                int mountMaxHealth = Mth.ceil(mount.getMaxHealth());

                int mountColor = determineHealthColor(mountHealth, mountMaxHealth);

                int mountPosX = screenWidth / 2 + ConfigManager.mountDisplayX();
                int mountPosY = screenHeight - ConfigManager.mountDisplayY();

                renderIcon(guiGraphics, MOUNT_ICON, mountPosX - 18, mountPosY - 4);
                drawShadowedText(guiGraphics, minecraft, mountHealth + "/" + mountMaxHealth, mountPosX, mountPosY, mountColor);
            }
        }

        minecraft.getProfiler().pop();

    }

    private void renderIcon(GuiGraphics guiGraphics, ResourceLocation icon, int x, int y) {
        RenderSystem.setShaderTexture(0, icon);
        guiGraphics.blit(icon, x, y, 0, 0, 16, 16, 16, 16);
    }

    private void drawShadowedText(GuiGraphics guiGraphics, Minecraft minecraft, String text, int x, int y, int color) {
        guiGraphics.drawString(minecraft.font, text, x, y, color, true);
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
        if (player.hasEffect(MobEffects.WITHER)) return 0x403030;

        float health = player.getHealth();
        float maxHealth = player.getMaxHealth();
        float ratio = health / maxHealth;

        if (ratio < 0.2f) {
            float pulse = (float)(Math.sin(player.tickCount * 0.3f) * 0.5f + 0.5f);
            return interpolateColor(pulse, 0x800000, 0xFF0000);
        }

        if (ratio > 0.5f) {
            return interpolateColor((1.0f - ratio) * 2f, 0x00FF00, 0xFFFF00); // Green -> Yellow
        } else {
            return interpolateColor((0.5f - ratio) * 2f, 0xFFFF00, 0xFF0000); // Yellow -> Red
        }
    }
    private int determineHealthColor(int current, int max) {
        float ratio = (float) current / (float) max;

        if (ratio < 0.2f) {
            float pulse = (float)(Math.sin(System.currentTimeMillis() * 0.005f) * 0.5f + 0.5f);
            return interpolateColor(pulse, 0x800000, 0xFF0000);
        }

        if (ratio > 0.5f) {
            return interpolateColor((1.0f - ratio) * 2f, 0x00FF00, 0xFFFF00); // Green -> Yellow
        } else {
            return interpolateColor((0.5f - ratio) * 2f, 0xFFFF00, 0xFF0000); // Yellow -> Red
        }
    }


    private int determineHungerColor(int hunger, int maxHunger) {
        float ratio = (float) hunger / maxHunger;

        if (ratio > 0.5f) {
            // Yellow -> Orange
            return interpolateColor((1.0f - ratio) * 2f, 0xFFFF00, 0xFFA500);
        } else {
            // Orange -> Red
            return interpolateColor((0.5f - ratio) * 2f, 0xFFA500, 0xFF4500);
        }
    }
    private static LivingEntity getMountWithHealth(Player player) {
        if (player != null) {
            var vehicle = player.getVehicle();
            if (vehicle instanceof LivingEntity living && living.showVehicleHealth()) {
                return living;
            }
        }
        return null;
    }
}