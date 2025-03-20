package net.bandit.better_hp.event;

import net.bandit.better_hp.BetterhpMod;
import net.bandit.better_hp.config.BetterHPConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attributes;
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
    private static final ResourceLocation TOUGHNESS_ICON = ResourceLocation.fromNamespaceAndPath("better_hp", "textures/gui/toughness_icon.png");
    private static final ResourceLocation HARDCORE_HEALTH_ICON = ResourceLocation.fromNamespaceAndPath("better_hp", "textures/gui/hardcore_health_icon.png");

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
    private static boolean showToughnessIcon;
    private static boolean showHungerIcon;
    private static boolean enableDynamicColor;
    private static int healthDisplayX;
    private static int healthDisplayY;
    private static int armorDisplayX;
    private static int toughnessDisplayX;
    private static int toughnessDisplayY;
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
        showToughnessIcon = BetterHPConfig.showToughnessIcon.get();
        showHungerIcon = BetterHPConfig.showHungerIcon.get();
        enableDynamicColor = BetterHPConfig.enableDynamicHealthColor.get();

        // Update position values
        healthDisplayX = BetterHPConfig.healthDisplayX.get();
        healthDisplayY = BetterHPConfig.healthDisplayY.get();
        armorDisplayX = BetterHPConfig.armorDisplayX.get();
        armorDisplayY = BetterHPConfig.armorDisplayY.get();
        toughnessDisplayX = BetterHPConfig.toughnessDisplayX.get();
        toughnessDisplayY = BetterHPConfig.toughnessDisplayY.get();
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

        if (minecraft.options.hideGui) {
            return;
        }

        if (player == null || minecraft.gameMode.getPlayerMode() == GameType.CREATIVE || minecraft.gameMode.getPlayerMode() == GameType.SPECTATOR) {
            return;
        }

        GuiGraphics guiGraphics = event.getGuiGraphics();
        renderCustomHud(guiGraphics, player);
    }

    private static void renderCustomHud(GuiGraphics guiGraphics, Player player) {
        int screenWidth = Minecraft.getInstance().getWindow().getGuiScaledWidth();
        int screenHeight = Minecraft.getInstance().getWindow().getGuiScaledHeight();

        // Fetch updated values live (instead of relying on cached variables)
        boolean showHealthIcon = BetterHPConfig.showHealthIcon.get();
        boolean showArmorIcon = BetterHPConfig.showArmorIcon.get();
        boolean showToughnessIcon = BetterHPConfig.showToughnessIcon.get();
        boolean showNumericHealth = BetterHPConfig.showNumericHealth.get();
        boolean showNumericHunger = BetterHPConfig.showNumericHunger.get();
        boolean showBreatheIcon = BetterHPConfig.showOxygenIcon.get();
        boolean showNumericOxygen = BetterHPConfig.showNumericOxygen.get();
        boolean enableDynamicColor = BetterHPConfig.enableDynamicHealthColor.get();

        int healthX = BetterHPConfig.healthDisplayX.get();
        int healthY = BetterHPConfig.healthDisplayY.get();
        int armorX = BetterHPConfig.armorDisplayX.get();
        int armorY = BetterHPConfig.armorDisplayY.get();
        int toughnessX = BetterHPConfig.toughnessDisplayX.get();
        int toughnessY = BetterHPConfig.toughnessDisplayY.get();
        int hungerX = BetterHPConfig.hungerDisplayX.get();
        int hungerY = BetterHPConfig.hungerDisplayY.get();
        int oxygenX = BetterHPConfig.oxygenDisplayX.get();
        int oxygenY = BetterHPConfig.oxygenDisplayY.get();

        float health = player.getHealth();
        float maxHealth = player.getMaxHealth();
        int absorption = (int) player.getAbsorptionAmount();
        int toughnessValue = (int) player.getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.ARMOR_TOUGHNESS).getValue();
        int armorValue = player.getArmorValue();
        int hunger = player.getFoodData().getFoodLevel();
        int saturation = (int) player.getFoodData().getSaturationLevel();
        int air = player.getAirSupply();
        int maxAir = player.getMaxAirSupply();

        int healthTextColor = enableDynamicColor ? getDynamicHealthColor(health, maxHealth) : BetterHPConfig.healthColor.get();

        // Render HUD Elements
        drawHealth(guiGraphics, health, maxHealth, healthTextColor, screenWidth, screenHeight, healthX, healthY, showHealthIcon, showNumericHealth);
        drawArmor(guiGraphics, armorValue, screenWidth, screenHeight, armorX, armorY, showArmorIcon);
        drawToughness(guiGraphics, toughnessValue, screenWidth, screenHeight, toughnessX, toughnessY, showToughnessIcon);
        drawHunger(guiGraphics, hunger, screenWidth, screenHeight, hungerX, hungerY, showHungerIcon, showNumericHunger);
        drawOxygen(guiGraphics, air, maxAir, screenWidth, screenHeight, oxygenX, oxygenY, showBreatheIcon, showNumericOxygen);
        drawAbsorption(guiGraphics, absorption, screenWidth, screenHeight, healthX, healthY);
        drawSaturation(guiGraphics, saturation, screenWidth, screenHeight, hungerX, hungerY);
    }

    // Render Methods
    private static void drawHealth(GuiGraphics guiGraphics, float health, float maxHealth, int textColor, int screenWidth, int screenHeight, int x, int y, boolean showIcon, boolean showNumeric) {
        ResourceLocation healthIcon = Minecraft.getInstance().level != null && Minecraft.getInstance().level.getLevelData().isHardcore() ? HARDCORE_HEALTH_ICON : HEALTH_ICON;

        if (showIcon) {
            drawIcon(guiGraphics, healthIcon, (screenWidth / 2) + x - 24, screenHeight - y - 4, 16, 16);
        }
        if (showNumeric) {
            guiGraphics.drawString(Minecraft.getInstance().font, String.format("%d/%d", (int) health, (int) maxHealth), (screenWidth / 2) + x, screenHeight - y, textColor);
        }
    }

    private static void drawArmor(GuiGraphics guiGraphics, int armorValue, int screenWidth, int screenHeight, int x, int y, boolean showIcon) {
        if (showIcon) {
            drawIcon(guiGraphics, ARMOR_ICON, (screenWidth / 2) + x - 24, screenHeight - y - 4, 16, 16);
            drawShadowedText(guiGraphics, Minecraft.getInstance().font, String.valueOf(armorValue), (screenWidth / 2) + x, screenHeight - y, 0xAAAAAA);
        }
    }

    private static void drawToughness(GuiGraphics guiGraphics, int toughnessValue, int screenWidth, int screenHeight, int x, int y, boolean showIcon) {
        if (showIcon) {
            drawIcon(guiGraphics, TOUGHNESS_ICON, (screenWidth / 2) + x - 24, screenHeight - y - 4, 16, 16);
            drawShadowedText(guiGraphics, Minecraft.getInstance().font, String.valueOf(toughnessValue), (screenWidth / 2) + x, screenHeight - y, 0xADD8E6);
        }
    }

    private static void drawHunger(GuiGraphics guiGraphics, int hunger, int screenWidth, int screenHeight, int x, int y, boolean showIcon, boolean showNumeric) {
        Minecraft mc = Minecraft.getInstance();
        Font font = mc.font;

        String hungerText = hunger + "/20";
        int textWidth = font.width(hungerText);

        if (showNumeric) {
            drawShadowedText(guiGraphics, font, hungerText, (screenWidth / 2) + x, screenHeight - y, 0xFF7518);
        }
        if (showIcon) {

            drawIcon(guiGraphics, HUNGER_ICON, (screenWidth / 2) + x + textWidth , screenHeight - y - 4, 16, 16);
        }
    }

    private static void drawOxygen(GuiGraphics guiGraphics, int air, int maxAir, int screenWidth, int screenHeight, int x, int y, boolean showIcon, boolean showNumeric) {
        if (showNumeric && air < maxAir) {
            drawShadowedText(guiGraphics, Minecraft.getInstance().font, (air / 20) + "/" + (maxAir / 20), (screenWidth / 2) + x - 10, screenHeight - y, 0x00BFFF);
        }
        if (showIcon && air < maxAir) {
            drawIcon(guiGraphics, BREATHE_ICON, (screenWidth / 2) + x + 18, screenHeight - y - 4, 16, 16);
        }
    }

    private static void drawAbsorption(GuiGraphics guiGraphics, int absorption, int screenWidth, int screenHeight, int x, int y) {
        if (absorption > 0) {
            drawShadowedText(guiGraphics, Minecraft.getInstance().font, "+" + absorption, (screenWidth / 2) + x + 40, screenHeight - y, 0xFFFF00);
        }
    }

    private static void drawSaturation(GuiGraphics guiGraphics, int saturation, int screenWidth, int screenHeight, int x, int y) {
        if (saturation > 0) {
            drawShadowedText(guiGraphics, Minecraft.getInstance().font, "+" + saturation, (screenWidth / 2) + x + 46, screenHeight - y, 0xFFD700);
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
