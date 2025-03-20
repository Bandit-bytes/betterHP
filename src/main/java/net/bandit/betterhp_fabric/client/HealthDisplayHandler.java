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
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;

public class HealthDisplayHandler implements HudRenderCallback {

    private static final ResourceLocation HEALTH_ICON = ResourceLocation.fromNamespaceAndPath("betterhp_fabric", "textures/gui/health_icon.png");
    private static final ResourceLocation HUNGER_ICON = ResourceLocation.fromNamespaceAndPath("betterhp_fabric", "textures/gui/hunger_icon.png");
    private static final ResourceLocation ARMOR_ICON = ResourceLocation.fromNamespaceAndPath("betterhp_fabric", "textures/gui/armor_icon.png");
    private static final ResourceLocation BREATHE_ICON = ResourceLocation.fromNamespaceAndPath("betterhp_fabric", "textures/gui/breathe_icon.png");
    private static final ResourceLocation TOUGHNESS_ICON = ResourceLocation.fromNamespaceAndPath("betterhp_fabric", "textures/gui/toughness_icon.png");
    private static final ResourceLocation HARDCORE_HEALTH_ICON = ResourceLocation.fromNamespaceAndPath("betterhp_fabric", "textures/gui/hardcore_health_icon.png");

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
        int hungerColor = determineHungerColor(hunger, 20);
        int breatheColor = 0x00BFFF; // Light blue for oxygen

        // Get screen dimensions
        int screenWidth = guiGraphics.guiWidth();
        int screenHeight = guiGraphics.guiHeight();

        // Get positions from config
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

// Render saturation
        minecraft.getProfiler().push("betterhp_saturationIcon");
        if (ConfigManager.showSaturation() && saturation > 0) {
            String saturationText = "+" + saturation;
            drawShadowedText(guiGraphics, minecraft, saturationText, saturationPosX, saturationPosY, 0xFFD700);
        }
        minecraft.getProfiler().pop();



        // Render health
        minecraft.getProfiler().push("betterhp_healthIcon");
        if (ConfigManager.showHealthIcon()) {
            ResourceLocation icon = isHardcore ? HARDCORE_HEALTH_ICON : HEALTH_ICON;
            renderIcon(guiGraphics, icon, healthPosX - 18, healthPosY - 4);
            drawShadowedText(guiGraphics, minecraft, health + "/" + maxHealth, healthPosX, healthPosY, healthColor);
            if (absorption > 0) {
                int healthTextWidth = minecraft.font.width(health + "/" + maxHealth);
                drawShadowedText(guiGraphics, minecraft, "+" + absorption, healthPosX + healthTextWidth + 3, healthPosY, 0xFFFF00);
            }
        }
        minecraft.getProfiler().pop();

        // Render hunger
        minecraft.getProfiler().push("betterhp_hungerIcon");
        if (ConfigManager.showHungerIcon()) {
            String hungerText = hunger + "/20";
            if (ConfigManager.getConfigData().showNumericHunger) {
                drawShadowedText(guiGraphics, minecraft, hungerText, hungerPosX - minecraft.font.width(hungerText), hungerPosY, hungerColor);
            }
            renderIcon(guiGraphics, HUNGER_ICON, hungerPosX, hungerPosY - 4);
        }
        minecraft.getProfiler().pop();

        minecraft.getProfiler().push("betterhp_saturationText");
        if (ConfigManager.showSaturation() && saturation > 0) {
            String saturationText = "+" + saturation;
            drawShadowedText(guiGraphics, minecraft, saturationText, saturationPosX, saturationPosY, 0xFFD700);
        }
        minecraft.getProfiler().pop();

        // Render armor
        minecraft.getProfiler().push("betterhp_armorIcon");
        if (ConfigManager.showArmorIcon()) {
            renderIcon(guiGraphics, ARMOR_ICON, armorPosX - 18, armorPosY - 4);
            drawShadowedText(guiGraphics, minecraft, String.valueOf(armorValue), armorPosX, armorPosY, 0xFFFFFF);
        }
        minecraft.getProfiler().pop();

        // Render toughness
        minecraft.getProfiler().push("betterhp_toughnessIcon");
        if (ConfigManager.showToughnessIcon()) {
            int toughness = Mth.ceil(player.getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.ARMOR_TOUGHNESS).getValue());
            int toughnessPosX = armorPosX + ConfigManager.toughnessDisplayX(); // Position next to armor
            int toughnessPosY = armorPosY + ConfigManager.toughnessDisplayY();

            renderIcon(guiGraphics, TOUGHNESS_ICON, toughnessPosX - 18, toughnessPosY - 4);
            drawShadowedText(guiGraphics, minecraft, String.valueOf(toughness), toughnessPosX, toughnessPosY, 0xADD8E6); // Light Blue
        }
        minecraft.getProfiler().pop();


        // Render breathing (oxygen)
        minecraft.getProfiler().push("betterhp_breatheIcon");
        if (ConfigManager.showBreatheIcon() && (player.isUnderWater() || air < maxAir)) {
            String breatheText = (air / 20) + "/" + (maxAir / 20);
            if (ConfigManager.getConfigData().showNumericOxygen) {
                drawShadowedText(guiGraphics, minecraft, breatheText, breathePosX - minecraft.font.width(breatheText), breathePosY, breatheColor);
            }
            renderIcon(guiGraphics, BREATHE_ICON, breathePosX, breathePosY - 4);
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

    private int determineHealthColor(Player player) {
        if (player.hasEffect(MobEffects.POISON)) return 0x00FF00;
        if (player.hasEffect(MobEffects.WITHER)) return 0x707070;

        int health = Mth.ceil(player.getHealth());
        int maxHealth = Mth.ceil(player.getMaxHealth());

        if (health > maxHealth * 0.75) return 0x00FF00;
        if (health > maxHealth * 0.25) return 0xFFFF00;
        return 0xFF0000;
    }

    private int determineHungerColor(int hunger, int maxHunger) {
        if (hunger > maxHunger * 0.75) return 0xFF8C00;
        if (hunger > maxHunger * 0.25) return 0xFFFF00;
        return 0xFF4500;
    }
}