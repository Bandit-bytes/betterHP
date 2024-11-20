package net.bandit.better_hp.event;

import net.bandit.better_hp.BetterhpMod;
import net.bandit.better_hp.config.BetterHPConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import com.mojang.blaze3d.systems.RenderSystem;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiEvent;

@EventBusSubscriber(modid = BetterhpMod.MOD_ID, value = Dist.CLIENT)
public class HealthDisplayHandler {

    private static final ResourceLocation HEALTH_ICON = ResourceLocation.fromNamespaceAndPath("better_hp", "textures/gui/health_icon.png");
    private static final ResourceLocation HUNGER_ICON = ResourceLocation.fromNamespaceAndPath("better_hp", "textures/gui/hunger_icon.png");
    private static final ResourceLocation ARMOR_ICON = ResourceLocation.fromNamespaceAndPath("better_hp", "textures/gui/armor_icon.png");
    private static final ResourceLocation BREATHE_ICON = ResourceLocation.fromNamespaceAndPath("better_hp", "textures/gui/breathe_icon.png");

    // Cached configuration variables
    private static boolean showVanillaHearts;
    private static boolean showVanillaArmor;
    private static boolean showVanillaHunger;
    private static boolean showVanillaOxygen;
    private static boolean showNumericHealth;
    private static boolean showNumericHunger;
    private static boolean showBreatheIcon;
    private static boolean showNumericOxygen;
    private static boolean showHealthIcon;
    private static boolean showArmorIcon;
    private static boolean showHungerIcon;
    private static boolean enableDynamicColor;
    private static int healthDisplayX;
    private static int healthDisplayY;
    private static int armorDisplayX;
    private static int armorDisplayY;
    private static int hungerDisplayX;
    private static int hungerDisplayY;
    private static int oxygenDisplayX;
    private static int oxygenDisplayY;
    private static int healthColor;


    public static void loadCachedConfigValues() {
        showVanillaHearts = BetterHPConfig.showVanillaHearts.get();
        showVanillaArmor = BetterHPConfig.showVanillaArmor.get();
        showVanillaHunger = BetterHPConfig.showVanillaHunger.get();
        showVanillaOxygen = BetterHPConfig.showVanillaOxygen.get();
        showNumericHealth = BetterHPConfig.showNumericHealth.get();
        showNumericHunger = BetterHPConfig.showNumericHunger.get();
        showBreatheIcon = BetterHPConfig.showOxygenIcon.get();
        showNumericOxygen = BetterHPConfig.showNumericOxygen.get();
        showHealthIcon = BetterHPConfig.showHealthIcon.get();
        showArmorIcon = BetterHPConfig.showArmorIcon.get();
        showHungerIcon = BetterHPConfig.showHungerIcon.get();
        enableDynamicColor = BetterHPConfig.enableDynamicHealthColor.get();
        healthDisplayX = BetterHPConfig.healthDisplayX.get();
        healthDisplayY = BetterHPConfig.healthDisplayY.get();
        armorDisplayX = BetterHPConfig.armorDisplayX.get();
        armorDisplayY = BetterHPConfig.armorDisplayY.get();
        hungerDisplayX = BetterHPConfig.hungerDisplayX.get();
        hungerDisplayY = BetterHPConfig.hungerDisplayY.get();
        oxygenDisplayX = BetterHPConfig.oxygenDisplayX.get();
        oxygenDisplayY = BetterHPConfig.oxygenDisplayY.get();
        healthColor = BetterHPConfig.healthColor.get();
    }

    // Render the custom HUD
    @SubscribeEvent
    public static void onRenderGuiPost(RenderGuiEvent.Post event) {
        Minecraft minecraft = Minecraft.getInstance();
        Player player = minecraft.player;

        if (player == null || minecraft.gameMode.getPlayerMode() == GameType.CREATIVE || minecraft.gameMode.getPlayerMode() == GameType.SPECTATOR) {
            return;
        }

        GuiGraphics guiGraphics = event.getGuiGraphics();
        renderCustomHud(guiGraphics, player);
    }

    private static void renderCustomHud(GuiGraphics guiGraphics, Player player) {
        int screenWidth = Minecraft.getInstance().getWindow().getGuiScaledWidth();
        int screenHeight = Minecraft.getInstance().getWindow().getGuiScaledHeight();

        float health = player.getHealth();
        float maxHealth = player.getMaxHealth();
        int absorption = (int) player.getAbsorptionAmount();  // Absorption value
        int saturation = (int) player.getFoodData().getSaturationLevel();  // Saturation level
        int armorValue = player.getArmorValue();
        int hunger = player.getFoodData().getFoodLevel();
        int air = player.getAirSupply();
        int maxAir = player.getMaxAirSupply();

        int healthTextColor = enableDynamicColor ? getDynamicHealthColor(health, maxHealth) : healthColor;

        // Render Health
        drawHealth(guiGraphics, health, maxHealth, healthTextColor, screenWidth, screenHeight);
        drawArmor(guiGraphics, armorValue, screenWidth, screenHeight);
        drawHunger(guiGraphics, hunger, screenWidth, screenHeight);
        drawOxygen(guiGraphics, air, maxAir, screenWidth, screenHeight);
        drawAbsorption(guiGraphics, absorption, screenWidth, screenHeight);
        drawSaturation(guiGraphics, saturation, screenWidth, screenHeight);
    }


    private static void drawHealth(GuiGraphics guiGraphics, float health, float maxHealth, int textColor, int screenWidth, int screenHeight) {
        if (showNumericHealth) {
            String healthText = String.format("%d/%d", (int) health, (int) maxHealth);
            guiGraphics.drawString(Minecraft.getInstance().font, healthText, (screenWidth / 2) + healthDisplayX, screenHeight - healthDisplayY, textColor);
        }
        if (showHealthIcon) {
            drawIcon(guiGraphics, HEALTH_ICON, (screenWidth / 2) + healthDisplayX - 24, screenHeight - healthDisplayY - 4, 16, 16);
        }
    }

    private static void drawArmor(GuiGraphics guiGraphics, int armorValue, int screenWidth, int screenHeight) {
        if (showArmorIcon) {
            drawIcon(guiGraphics, ARMOR_ICON, (screenWidth / 2) + armorDisplayX - 24, screenHeight - armorDisplayY - 4, 16, 16);
            drawShadowedText(guiGraphics, Minecraft.getInstance().font, String.valueOf(armorValue), (screenWidth / 2) + armorDisplayX, screenHeight - armorDisplayY, 0xAAAAAA);
        }
    }

    private static void drawHunger(GuiGraphics guiGraphics, int hunger, int screenWidth, int screenHeight) {
        if (showNumericHunger) {
            drawShadowedText(guiGraphics, Minecraft.getInstance().font, hunger + "/20", (screenWidth / 2) + hungerDisplayX - 10, screenHeight - hungerDisplayY, 0xFF7518);
        }
        if (showHungerIcon) {
            drawIcon(guiGraphics, HUNGER_ICON, (screenWidth / 2) + hungerDisplayX + 18, screenHeight - hungerDisplayY - 4, 16, 16);
        }
    }

    private static void drawOxygen(GuiGraphics guiGraphics, int air, int maxAir, int screenWidth, int screenHeight) {
        if (showNumericOxygen && air < maxAir) {
            drawShadowedText(guiGraphics, Minecraft.getInstance().font, (air / 20) + "/" + (maxAir / 20), (screenWidth / 2) + oxygenDisplayX - 10, screenHeight - oxygenDisplayY, 0x00BFFF);
        }
        if (showBreatheIcon && air < maxAir) {
            drawIcon(guiGraphics, BREATHE_ICON, (screenWidth / 2) + oxygenDisplayX + 18, screenHeight - oxygenDisplayY - 4, 16, 16);
        }
    }

    private static void drawAbsorption(GuiGraphics guiGraphics, int absorption, int screenWidth, int screenHeight) {
        if (absorption > 0) {
            String absorptionText = "+" + absorption;
            // Shift absorption display to the right
            drawShadowedText(guiGraphics, Minecraft.getInstance().font, absorptionText, (screenWidth / 2) + healthDisplayX + 40, screenHeight - healthDisplayY, 0xFFFF00);
        }
    }

    private static void drawSaturation(GuiGraphics guiGraphics, int saturation, int screenWidth, int screenHeight) {
        if (saturation > 0) {
            String saturationText = "+" + saturation;
            // Shift saturation display to the right
            drawShadowedText(guiGraphics, Minecraft.getInstance().font, saturationText, (screenWidth / 2) + hungerDisplayX + 40, screenHeight - hungerDisplayY, 0xFFD700);
        }
    }

    private static int getDynamicHealthColor(float health, float maxHealth) {
        float healthPercentage = health / maxHealth;
        return (healthPercentage > 0.6f) ? 0x00FF00 : (healthPercentage > 0.3f) ? 0xFFFF00 : 0xFF0000;
    }

    private static void drawIcon(GuiGraphics guiGraphics, ResourceLocation icon, int x, int y, int width, int height) {
        RenderSystem.setShaderTexture(0, icon);
        guiGraphics.blit(icon, x, y, 0, 0, width, height, width, height);
    }

    private static void drawShadowedText(GuiGraphics guiGraphics, Font font, String text, int x, int y, int color) {
        guiGraphics.drawString(font, text, x, y, color, true);
    }
}
