package net.bandit.better_hp.event;

import net.bandit.better_hp.config.BetterHPConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
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

    @SubscribeEvent
    public static void onRenderGui(RenderGuiOverlayEvent.Pre event) {
        if (event.getOverlay() == VanillaGuiOverlay.PLAYER_HEALTH.type()) {
            event.setCanceled(event.isCanceled() || !BetterHPConfig.CLIENT.showVanillaHearts.get());
        } else if (event.getOverlay() == VanillaGuiOverlay.ARMOR_LEVEL.type()) {
            event.setCanceled(event.isCanceled() || !BetterHPConfig.CLIENT.showVanillaArmor.get());
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

        if (player == null || minecraft.gameMode.getPlayerMode() == GameType.CREATIVE) {
            return;
        }

        boolean showVanillaArmor = BetterHPConfig.CLIENT.showVanillaArmor.get();
        boolean showNumericHunger = BetterHPConfig.CLIENT.showNumericHunger.get();
        boolean showBreatheIcon = BetterHPConfig.CLIENT.showOxygenIcon.get();
        boolean showNumericOxygen = BetterHPConfig.CLIENT.showNumericOxygen.get();
        boolean showHealthIcon = BetterHPConfig.CLIENT.showHealthIcon.get();
        boolean showArmorIcon = BetterHPConfig.CLIENT.showArmorIcon.get();
        boolean showHungerIcon = BetterHPConfig.CLIENT.showHungerIcon.get();

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

        // Define numeric display texts
        String healthText = health + "/" + maxHealth;
        String absorptionText = absorption > 0 ? "+" + absorption : null;
        String armorText = armorValue + "";
        String hungerText = hunger + "/" + 20;
        String saturationText = saturation > 0 ? " +" + saturation : null;
        String breatheText = (air / 20) + "/" + (maxAir / 20);

        Font font = minecraft.font;

        int textColor = determineHealthColor(player);
        int absorptionColor = 0xFFFF00;
        int armorColor = 0xAAAAAA;
        int hungerColor = 0xFF7518;
        int saturationColor = 0xFFD700;
        int breatheColor = 0x00BFFF;

        // Draw the numeric values (always visible)
        drawShadowedText(guiGraphics, font, healthText, centeredHealthX, bottomHealthY, textColor);

        if (absorptionText != null) {
            drawShadowedText(guiGraphics, font, absorptionText, centeredHealthX + font.width(healthText) + 5, bottomHealthY, absorptionColor);
        }

        if (showNumericHunger) {
            drawShadowedText(guiGraphics, font, hungerText, centeredHungerX - font.width(hungerText), bottomHungerY, hungerColor);
            if (saturationText != null) {
                drawShadowedText(guiGraphics, font, saturationText, centeredHungerX + font.width(hungerText) + 5, bottomHungerY, saturationColor);
            }
        }

        if (showNumericOxygen && player.isUnderWater()) {
            drawShadowedText(guiGraphics, font, breatheText, centeredBreatheX - font.width(breatheText), bottomBreatheY, breatheColor);
        }

        // Draw the icons based on config settings
        if (showHealthIcon) {
            drawIcon(guiGraphics, HEALTH_ICON, centeredHealthX - 18, bottomHealthY - 4, 16, 16);
        }

        if (!showVanillaArmor && showArmorIcon) {
            drawIcon(guiGraphics, ARMOR_ICON, centeredArmorX - 18, bottomArmorY - 4, 16, 16);
            drawShadowedText(guiGraphics, font, armorText, centeredArmorX, bottomArmorY, armorColor);
        }

        if (showHungerIcon) {
            drawIcon(guiGraphics, HUNGER_ICON, centeredHungerX - font.width(hungerText) + font.width(hungerText) + 2, bottomHungerY - 4, 16, 16);
        }

        if (showBreatheIcon && player.isUnderWater()) {
            drawIcon(guiGraphics, BREATHE_ICON, centeredBreatheX - font.width(breatheText) + font.width(breatheText) + 2, bottomBreatheY - 4, 16, 16);
        }
    }

    private static int determineHealthColor(Player player) {
        MobEffectInstance witherEffect = player.getEffect(MobEffects.WITHER);
        if (witherEffect != null) {
            return 0x800080; // Dark Purple color for Wither effect
        }

        MobEffectInstance poisonEffect = player.getEffect(MobEffects.POISON);
        if (poisonEffect != null) {
            return 0x00FF00; // Bright Green color for Poison effect
        }

        return 0xFF0000; // Default red color for health
    }

    private static void drawIcon(GuiGraphics guiGraphics, ResourceLocation icon, int x, int y, int width, int height) {
        RenderSystem.setShaderTexture(0, icon);
        guiGraphics.blit(icon, x, y, 0, 0, width, height, width, height);
    }

    private static void drawShadowedText(GuiGraphics guiGraphics, Font font, String text, int x, int y, int color) {
        guiGraphics.drawString(font, text, x, y, color, true); // Use the shadowed text option
    }
}
