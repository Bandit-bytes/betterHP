package net.bandit.better_hp.event;

import net.bandit.better_hp.config.BetterHPConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import com.mojang.blaze3d.systems.RenderSystem;

@Mod.EventBusSubscriber(modid = "better_hp", value = Dist.CLIENT)
public class HealthDisplayHandler {

    private static final ResourceLocation HEALTH_ICON = new ResourceLocation("better_hp", "textures/gui/health_icon.png");
    private static final ResourceLocation HUNGER_ICON = new ResourceLocation("better_hp", "textures/gui/hunger_icon.png");
    private static final ResourceLocation ARMOR_ICON = new ResourceLocation("better_hp", "textures/gui/armor_icon.png");
    private static final ResourceLocation BREATHE_ICON = new ResourceLocation("better_hp", "textures/gui/breathe_icon.png");

    // Caching the config values
    private static boolean showVanillaArmor;
    private static boolean showNumericHunger;
    private static boolean showBreatheIcon;
    private static boolean showNumericOxygen;
    private static boolean showHealthIcon;
    private static boolean showArmorIcon;
    private static boolean showHungerIcon;
    private static boolean showNumericHealth;
    private static boolean enableDynamicColor;

    // Initialize the config values
    public static void initializeConfigs() {
        showVanillaArmor = BetterHPConfig.CLIENT.showVanillaArmor.get();
        showNumericHunger = BetterHPConfig.CLIENT.showNumericHunger.get();
        showBreatheIcon = BetterHPConfig.CLIENT.showOxygenIcon.get();
        showNumericOxygen = BetterHPConfig.CLIENT.showNumericOxygen.get();
        showHealthIcon = BetterHPConfig.CLIENT.showHealthIcon.get();
        showArmorIcon = BetterHPConfig.CLIENT.showArmorIcon.get();
        showHungerIcon = BetterHPConfig.CLIENT.showHungerIcon.get();
        showNumericHealth = BetterHPConfig.CLIENT.showNumericHealth.get();
        enableDynamicColor = BetterHPConfig.CLIENT.enableDynamicHealthColor.get();
    }

    @SubscribeEvent
    public static void onRenderGui(RenderGuiOverlayEvent.Pre event) {
        if (event.getOverlay() == VanillaGuiOverlay.PLAYER_HEALTH.type()) {
            event.setCanceled(event.isCanceled() || !BetterHPConfig.CLIENT.showVanillaHearts.get());
        } else if (event.getOverlay() == VanillaGuiOverlay.ARMOR_LEVEL.type()) {
            event.setCanceled(event.isCanceled() || !showVanillaArmor);
        } else if (event.getOverlay() == VanillaGuiOverlay.FOOD_LEVEL.type()) {
            event.setCanceled(event.isCanceled() || !BetterHPConfig.CLIENT.showVanillaHunger.get());
        } else if (event.getOverlay() == VanillaGuiOverlay.AIR_LEVEL.type()) {
            event.setCanceled(event.isCanceled() || !BetterHPConfig.CLIENT.showVanillaOxygen.get());
        }
    }

    @SubscribeEvent
    public static void onRenderGui(RenderGuiOverlayEvent.Post event) {
        Minecraft minecraft = Minecraft.getInstance();
        Player player = minecraft.player;

        if (player == null || minecraft.gameMode.getPlayerMode() == GameType.CREATIVE || minecraft.gameMode.getPlayerMode() == GameType.SPECTATOR) {
            return;
        }

        // Ensure the config values are initialized
        initializeConfigs();

        GuiGraphics guiGraphics = event.getGuiGraphics();

        int health = (int) player.getHealth();
        int maxHealth = (int) player.getMaxHealth();
        int absorption = (int) player.getAbsorptionAmount();
        int armorValue = player.getArmorValue();
        int hunger = player.getFoodData().getFoodLevel();
        int saturation = (int) player.getFoodData().getSaturationLevel();
        int air = player.getAirSupply();
        int maxAir = player.getMaxAirSupply();

        int screenWidth = minecraft.getWindow().getGuiScaledWidth();
        int screenHeight = minecraft.getWindow().getGuiScaledHeight();

        // Define positions for each display element based on configuration
        int healthDisplayX = BetterHPConfig.CLIENT.healthDisplayX.get();
        int healthDisplayY = BetterHPConfig.CLIENT.healthDisplayY.get();
        int armorDisplayX = BetterHPConfig.CLIENT.armorDisplayX.get();
        int armorDisplayY = BetterHPConfig.CLIENT.armorDisplayY.get();
        int hungerDisplayX = BetterHPConfig.CLIENT.hungerDisplayX.get();
        int hungerDisplayY = BetterHPConfig.CLIENT.hungerDisplayY.get();
        int breatheDisplayX = BetterHPConfig.CLIENT.oxygenDisplayX.get();
        int breatheDisplayY = BetterHPConfig.CLIENT.oxygenDisplayY.get();

        int centeredHealthX = (screenWidth / 2) + healthDisplayX;
        int bottomHealthY = screenHeight - healthDisplayY;

        int centeredArmorX = (screenWidth / 2) + armorDisplayX;
        int bottomArmorY = screenHeight - armorDisplayY;

        int centeredHungerX = (screenWidth / 2) + hungerDisplayX;
        int bottomHungerY = screenHeight - hungerDisplayY;

        int centeredBreatheX = (screenWidth / 2) + breatheDisplayX;
        int bottomBreatheY = screenHeight - breatheDisplayY;

        Font font = minecraft.font;

        // Dynamically calculated health color based on config
        int textColor = enableDynamicColor ? getDynamicHealthColor(health, maxHealth) : BetterHPConfig.CLIENT.healthColor.get();
        int absorptionColor = 0xFFFF00;
        int armorColor = 0xAAAAAA;
        int hungerColor = 0xFF7518;
        int saturationColor = 0xFFD700;
        int breatheColor = 0x00BFFF;

        // Rendering health
        if (showNumericHealth) {
            String healthText = health + "/" + maxHealth;
            int healthTextWidth = font.width(healthText);
            int healthTextOffset = 15;

            drawShadowedText(guiGraphics, font, healthText, centeredHealthX - healthTextWidth / 2 + healthTextOffset, bottomHealthY, textColor);

            String absorptionText = absorption > 0 ? "+" + absorption : null;
            if (absorptionText != null) {
                drawShadowedText(guiGraphics, font, absorptionText, centeredHealthX + healthTextWidth / 2 + healthTextOffset + 5, bottomHealthY, absorptionColor);
            }
        }

        // Rendering hunger
        if (showNumericHunger) {
            String hungerText = hunger + "/" + 20;
            int hungerTextWidth = font.width(hungerText);

            drawShadowedText(guiGraphics, font, hungerText, centeredHungerX - hungerTextWidth / 2, bottomHungerY, hungerColor);

            String saturationText = saturation > 0 ? " +" + saturation : null;
            if (saturationText != null) {
                int saturationOffset = 14; // Offset for saturation text
                drawShadowedText(guiGraphics, font, saturationText, centeredHungerX + hungerTextWidth / 2 + saturationOffset, bottomHungerY, saturationColor);
            }
        }

        // Rendering oxygen
        if (showNumericOxygen && (player.isUnderWater() || air < maxAir)) {
            String breatheText = (air / 20) + "/" + (maxAir / 20);
            int breatheTextWidth = font.width(breatheText);

            drawShadowedText(guiGraphics, font, breatheText, centeredBreatheX - breatheTextWidth / 2, bottomBreatheY, breatheColor);
        }

        // Rendering health icon
        if (showHealthIcon) {
            drawIcon(guiGraphics, HEALTH_ICON, centeredHealthX - 24, bottomHealthY - 4, 16, 16);
        }

        // Rendering armor
        if (!showVanillaArmor && showArmorIcon) {
            drawIcon(guiGraphics, ARMOR_ICON, centeredArmorX - 24, bottomArmorY - 4, 16, 16);
            drawShadowedText(guiGraphics, font, String.valueOf(armorValue), centeredArmorX, bottomArmorY, armorColor);
        }

        // Rendering hunger icon
        if (showHungerIcon) {
            String hungerText = hunger + "/" + 20;
            int hungerTextWidth = font.width(hungerText);
            drawIcon(guiGraphics, HUNGER_ICON, centeredHungerX - hungerTextWidth / 2 + hungerTextWidth + 2, bottomHungerY - 4, 16, 16);
        }

        // Rendering oxygen icon
        if (showBreatheIcon && (player.isUnderWater() || air < maxAir)) {
            String breatheText = (air / 20) + "/" + (maxAir / 20);
            int breatheTextWidth = font.width(breatheText);
            drawIcon(guiGraphics, BREATHE_ICON, centeredBreatheX - breatheTextWidth / 2 + breatheTextWidth + 2, bottomBreatheY - 4, 16, 16);
        }
    }

    private static int getDynamicHealthColor(int health, int maxHealth) {
        float healthPercentage = (float) health / maxHealth;
        if (healthPercentage > 0.6f) {
            return 0x00FF00; // Green when health is full
        } else if (healthPercentage > 0.3f) {
            return 0xFFFF00; // Yellow when health is medium
        } else {
            return 0xFF0000; // Red when health is low
        }
    }

    private static void drawIcon(GuiGraphics guiGraphics, ResourceLocation icon, int x, int y, int width, int height) {
        RenderSystem.setShaderTexture(0, icon);
        guiGraphics.blit(icon, x, y, 0, 0, width, height, width, height);
    }

    private static void drawShadowedText(GuiGraphics guiGraphics, Font font, String text, int x, int y, int color) {
        guiGraphics.drawString(font, text, x, y, color, true); // Use the shadowed text option
    }
}
