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

    private static boolean showVanillaArmor;
    private static boolean showNumericHunger;
    private static boolean showBreatheIcon;
    private static boolean showNumericOxygen;
    private static boolean showHealthIcon;
    private static boolean showArmorIcon;
    private static boolean showHungerIcon;
    private static boolean showNumericHealth;
    private static boolean enableDynamicColor;

    private static float lastHealth = -1;
    private static float lastMaxHealth = -1;
    private static int lastAbsorption = -1;
    private static int lastArmor = -1;
    private static int lastHunger = -1;
    private static int lastSaturation = -1;
    private static int lastAir = -1;
    private static int lastMaxAir = -1;


    private static int updateCounter = 0;
    private static final int UPDATE_INTERVAL = 10;  // Update every 10 ticks


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

        if (updateCounter++ % UPDATE_INTERVAL != 0) {
            return;
        }

        initializeConfigs();

        float health = player.getHealth();
        float maxHealth = player.getMaxHealth();
        int absorption = (int) player.getAbsorptionAmount();
        int armorValue = player.getArmorValue();
        int hunger = player.getFoodData().getFoodLevel();
        int saturation = (int) player.getFoodData().getSaturationLevel();
        int air = player.getAirSupply();
        int maxAir = player.getMaxAirSupply();

        GuiGraphics guiGraphics = event.getGuiGraphics();
        renderGui(guiGraphics, minecraft.font, health, maxHealth, absorption, armorValue, hunger, saturation, air, maxAir);
    }

    private static void renderGui(GuiGraphics guiGraphics, Font font, float health, float maxHealth, int absorption, int armorValue, int hunger, int saturation, int air, int maxAir) {
        int screenWidth = Minecraft.getInstance().getWindow().getGuiScaledWidth();
        int screenHeight = Minecraft.getInstance().getWindow().getGuiScaledHeight();

        int healthDisplayX = BetterHPConfig.CLIENT.healthDisplayX.get();
        int healthDisplayY = BetterHPConfig.CLIENT.healthDisplayY.get();
        int healthTextOffset = BetterHPConfig.CLIENT.healthTextOffset.get();
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


        int textColor = enableDynamicColor ? getDynamicHealthColor(health, maxHealth) : BetterHPConfig.CLIENT.healthColor.get();
        int absorptionColor = 0xFFFF00;
        int armorColor = 0xAAAAAA;
        int hungerColor = 0xFF7518;
        int saturationColor = 0xFFD700;
        int breatheColor = 0x00BFFF;

        if (showNumericHealth) {
            String healthText;
            if (BetterHPConfig.CLIENT.showDecimalHealth.get()) {
                healthText = String.format("%.1f/%.1f", health, maxHealth);
            } else {
                healthText = String.format("%d/%d", (int) health, (int) maxHealth);
            }
            int healthTextWidth = font.width(healthText);
            drawShadowedText(guiGraphics, font, healthText, centeredHealthX - healthTextWidth / 2 + healthTextOffset, bottomHealthY, textColor);

            String absorptionText = absorption > 0 ? "+" + absorption : null;
            if (absorptionText != null) {
                drawShadowedText(guiGraphics, font, absorptionText, centeredHealthX + healthTextWidth / 2 + healthTextOffset + 5, bottomHealthY, absorptionColor);
            }
        }
        if (showNumericHunger) {
            String hungerText = hunger + "/" + 20;
            int hungerTextWidth = font.width(hungerText);

            drawShadowedText(guiGraphics, font, hungerText, centeredHungerX - hungerTextWidth / 2, bottomHungerY, hungerColor);

            String saturationText = saturation > 0 ? " +" + saturation : null;
            if (saturationText != null) {
                int saturationOffset = 14;
                drawShadowedText(guiGraphics, font, saturationText, centeredHungerX + hungerTextWidth / 2 + saturationOffset, bottomHungerY, saturationColor);
            }
        }
        if (showNumericOxygen && (air < maxAir)) {
            String breatheText = (air / 20) + "/" + (maxAir / 20);
            int breatheTextWidth = font.width(breatheText);

            drawShadowedText(guiGraphics, font, breatheText, centeredBreatheX - breatheTextWidth / 2, bottomBreatheY, breatheColor);
        }
        if (showHealthIcon) {
            drawIcon(guiGraphics, HEALTH_ICON, centeredHealthX - 24, bottomHealthY - 4, 16, 16);
        }
        if (!showVanillaArmor && showArmorIcon) {
            drawIcon(guiGraphics, ARMOR_ICON, centeredArmorX - 24, bottomArmorY - 4, 16, 16);
            drawShadowedText(guiGraphics, font, String.valueOf(armorValue), centeredArmorX, bottomArmorY, armorColor);
        }
        if (showHungerIcon) {
            drawIcon(guiGraphics, HUNGER_ICON, centeredHungerX + 18, bottomHungerY - 4, 16, 16);
        }
        if (showBreatheIcon && (air < maxAir)) {
            drawIcon(guiGraphics, BREATHE_ICON, centeredBreatheX + 18, bottomBreatheY - 4, 16, 16);
        }
    }
    private static int getDynamicHealthColor(float health, float maxHealth) {
        float healthPercentage = health / maxHealth;
        if (healthPercentage > 0.6f) {
            return 0x00FF00;  // Green
        } else if (healthPercentage > 0.3f) {
            return 0xFFFF00;  // Yellow
        } else {
            return 0xFF0000;  // Red
        }
    }
    private static void drawIcon(GuiGraphics guiGraphics, ResourceLocation icon, int x, int y, int width, int height) {
        RenderSystem.setShaderTexture(0, icon);
        guiGraphics.blit(icon, x, y, 0, 0, width, height, width, height);
    }
    private static void drawShadowedText(GuiGraphics guiGraphics, Font font, String text, int x, int y, int color) {
        guiGraphics.drawString(font, text, x, y, color, true);
    }
}
