package net.bandit.better_hp.event;

import net.bandit.better_hp.BetterhpMod;
import net.bandit.better_hp.config.BetterHPConfig;
import net.bandit.better_hp.integration.IronsSpellbooksCompat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import com.mojang.blaze3d.systems.RenderSystem;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
import org.jetbrains.annotations.Nullable;


@EventBusSubscriber(modid = BetterhpMod.MOD_ID, value = Dist.CLIENT)
public class HealthDisplayHandler {

    private static final ResourceLocation HEALTH_ICON = ResourceLocation.fromNamespaceAndPath("better_hp", "textures/gui/health_icon.png");
    private static final ResourceLocation HUNGER_ICON = ResourceLocation.fromNamespaceAndPath("better_hp", "textures/gui/hunger_icon.png");
    private static final ResourceLocation ARMOR_ICON = ResourceLocation.fromNamespaceAndPath("better_hp", "textures/gui/armor_icon.png");
    private static final ResourceLocation MOUNT_ICON = ResourceLocation.fromNamespaceAndPath("better_hp", "textures/gui/mount_icon.png");
    private static final ResourceLocation BREATHE_ICON = ResourceLocation.fromNamespaceAndPath("better_hp", "textures/gui/breathe_icon.png");
    private static final ResourceLocation TOUGHNESS_ICON = ResourceLocation.fromNamespaceAndPath("better_hp", "textures/gui/toughness_icon.png");
    private static final ResourceLocation HARDCORE_HEALTH_ICON = ResourceLocation.fromNamespaceAndPath("better_hp", "textures/gui/hardcore_health_icon.png");
    private static final ResourceLocation MANA_ICON = ResourceLocation.fromNamespaceAndPath("better_hp", "textures/gui/mana_icon.png");

    // Cached configuration variables
    private static boolean showVanillaHearts;
    private static boolean showVanillaArmor;
    private static boolean showVanillaMountHealth;
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
    public static boolean manaEnabled;


    private static int armorBounceTicks = 0;
    private static int toughnessBounceTicks = 0;
    private static int lastArmorValue = -1;
    private static int lastToughnessValue = -1;

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
        showVanillaMountHealth = BetterHPConfig.showVanillaMountHealth.get();
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
        int screenWidth  = Minecraft.getInstance().getWindow().getGuiScaledWidth();
        int screenHeight = Minecraft.getInstance().getWindow().getGuiScaledHeight();

        boolean showHealthIcon     = BetterHPConfig.showHealthIcon.get();
        boolean showArmorIcon      = BetterHPConfig.showArmorIcon.get();
        boolean showToughnessIcon  = BetterHPConfig.showToughnessIcon.get();
        boolean showNumericHealth  = BetterHPConfig.showNumericHealth.get();
        boolean showNumericHunger  = BetterHPConfig.showNumericHunger.get();
        boolean showBreatheIcon    = BetterHPConfig.showOxygenIcon.get();
        boolean showNumericOxygen  = BetterHPConfig.showNumericOxygen.get();
        boolean enableDynamicColor = BetterHPConfig.enableDynamicHealthColor.get();
        boolean vanillaHungerOn    = BetterHPConfig.showVanillaHunger.get();

        int healthX    = BetterHPConfig.healthDisplayX.get();
        int healthY    = BetterHPConfig.healthDisplayY.get();
        int armorX     = BetterHPConfig.armorDisplayX.get();
        int armorY     = BetterHPConfig.armorDisplayY.get();
        int toughnessX = BetterHPConfig.toughnessDisplayX.get();
        int toughnessY = BetterHPConfig.toughnessDisplayY.get();
        int hungerX    = BetterHPConfig.hungerDisplayX.get();
        int hungerY    = BetterHPConfig.hungerDisplayY.get();
        int oxygenX    = BetterHPConfig.oxygenDisplayX.get();
        int oxygenY    = BetterHPConfig.oxygenDisplayY.get();

        // Player stats
        float health       = player.getHealth();
        float maxHealth    = player.getMaxHealth();
        int absorption     = (int) player.getAbsorptionAmount();
        int armorValue     = player.getArmorValue();
        var toughAttr      = player.getAttribute(Attributes.ARMOR_TOUGHNESS);
        int toughnessValue = toughAttr != null ? (int) toughAttr.getValue() : 0;
        int hunger         = player.getFoodData().getFoodLevel();
        int saturation     = (int) player.getFoodData().getSaturationLevel();
        int air            = player.getAirSupply();
        int maxAir         = player.getMaxAirSupply();

        if (armorValue != lastArmorValue) armorBounceTicks = 10;
        lastArmorValue = armorValue;

        if (toughnessValue != lastToughnessValue) toughnessBounceTicks = 10;
        lastToughnessValue = toughnessValue;

        int healthTextColor = enableDynamicColor ? getDynamicHealthColor(player) : BetterHPConfig.healthColor.get();

        int healthUsed = drawHealth(
                guiGraphics, player, health, maxHealth, healthTextColor,
                screenWidth, screenHeight, healthX, healthY,
                showHealthIcon, showNumericHealth
        );

        drawArmor(guiGraphics, armorValue, screenWidth, screenHeight, armorX, armorY, showArmorIcon);
        drawToughness(guiGraphics, toughnessValue, screenWidth, screenHeight, toughnessX, toughnessY, showToughnessIcon);

        boolean customHungerActive = (BetterHPConfig.showHungerIcon.get() || showNumericHunger);
        boolean showSaturationTxt  = BetterHPConfig.showSaturationText.get();
        boolean hideSatWithVanilla = BetterHPConfig.hideSaturationIfVanillaHunger.get();

        if (!vanillaHungerOn && customHungerActive) {
            drawHunger(guiGraphics, hunger, screenWidth, screenHeight, hungerX, hungerY,
                    BetterHPConfig.showHungerIcon.get(), showNumericHunger);

            if (showSaturationTxt && saturation > 0) {
                drawSaturation(guiGraphics, saturation, screenWidth, screenHeight, hungerX, hungerY);
            }
        } else {
            if (!(hideSatWithVanilla && vanillaHungerOn)) {
                if (showSaturationTxt && saturation > 0) {
                    drawSaturation(guiGraphics, saturation, screenWidth, screenHeight, hungerX, hungerY);
                }
            }
        }

        drawOxygen(guiGraphics, air, maxAir, screenWidth, screenHeight, oxygenX, oxygenY, showBreatheIcon, showNumericOxygen);

        // --- Iron's mana (optional) ---
        if (BetterHPConfig.enableIronsManaCompat.get() && BetterHPConfig.showMana.get()) {
            var mana = IronsSpellbooksCompat.getMana(player);
            if (mana != null) {
                int baseX = healthX;
                int baseY = healthY + BetterHPConfig.manaOffsetY.get();

                int follow = BetterHPConfig.manaFollowHealthWidth.get() ? healthUsed : 0;
                int manaX = baseX + follow + BetterHPConfig.manaOffsetX.get();
                int manaY = baseY;

                drawMana(guiGraphics, mana.mana(), mana.maxMana(),
                        screenWidth, screenHeight, manaX, manaY,
                        BetterHPConfig.showManaIcon.get(), BetterHPConfig.showNumericMana.get());
            }
        }

        // Absorption text (follows health width and then applies offsets)
        if (BetterHPConfig.showAbsorptionText.get() && absorption > 0) {
            int baseX = (screenWidth / 2) + healthX;
            int baseY = screenHeight - healthY;

            int follow = BetterHPConfig.absorptionFollowHealthWidth.get() ? healthUsed : 0;

            int ax = baseX + follow + BetterHPConfig.absorptionOffsetX.get();
            int ay = baseY + BetterHPConfig.absorptionOffsetY.get();

            drawAbsorption(guiGraphics, absorption, ax, ay);
    }

        LivingEntity mount = getMountWithHealth(player);
        if (mount != null && BetterHPConfig.showMountIcon.get()) {
            int mountHealth    = Mth.ceil(mount.getHealth());
            int mountMaxHealth = Mth.ceil(mount.getMaxHealth());

            int mountX = (screenWidth / 2) + BetterHPConfig.mountDisplayX.get();
            int mountY = screenHeight - BetterHPConfig.mountDisplayY.get();

            drawIcon(guiGraphics, MOUNT_ICON, mountX - 24, mountY - 4, 16, 16);
            guiGraphics.drawString(Minecraft.getInstance().font, mountHealth + "/" + mountMaxHealth, mountX, mountY, 0xAA77FF);
        }
    }
    private static void drawMana(GuiGraphics guiGraphics, int mana, int maxMana,
                                 int screenWidth, int screenHeight,
                                 int x, int y, boolean showIcon, boolean showNumeric) {

        Minecraft mc = Minecraft.getInstance();
        Font font = mc.font;

        String manaText = mana + "/" + maxMana;
        int textWidth = font.width(manaText);

        int color = 0x2F7DFF;

        if (showNumeric) {
            drawShadowedText(guiGraphics, font, manaText, (screenWidth / 2) + x, screenHeight - y, color);
        }
        if (showIcon) {
            drawIcon(guiGraphics, MANA_ICON, (screenWidth / 2) + x + textWidth, screenHeight - y - 4, 16, 16);
        }
    }

    private static int drawHealth(GuiGraphics g, Player player, float health, float maxHealth,
                                  int textColor, int sw, int sh, int x, int y,
                                  boolean showIcon, boolean showNumeric) {
        Font font = Minecraft.getInstance().font;
        int used = 0;

        ResourceLocation healthIcon =
                (Minecraft.getInstance().level != null && Minecraft.getInstance().level.getLevelData().isHardcore())
                        ? HARDCORE_HEALTH_ICON
                        : HEALTH_ICON;

        if (showIcon) {
            drawIcon(g, healthIcon, (sw / 2) + x - 24, sh - y - 4, 16, 16);
            used += 16;
            if (showNumeric) used += 4;
        }

        if (showNumeric) {
            int shakeOffset = 0;
            if (maxHealth > 0 && (health / maxHealth) < 0.2f) {
                shakeOffset = (int)(Math.sin(player.tickCount * 0.6f) * 2);
            }
            String hpText = String.format("%d/%d", (int) health, (int) maxHealth);
            g.drawString(font, hpText, (sw / 2) + x + shakeOffset, sh - y, textColor);
            used += font.width(hpText);
        }

        return used;
    }

    private static void drawArmor(GuiGraphics guiGraphics, int armorValue, int screenWidth, int screenHeight, int x, int y, boolean showIcon) {
        if (showIcon && armorValue > 0) {
            float scale = 1.0f;
            if (armorBounceTicks > 0) {
                scale = 1.0f + 0.2f * (float) Math.sin((10 - armorBounceTicks) * Math.PI / 10);
                armorBounceTicks--;
            }

            float pulse = (armorValue == 20) ? (0.9f + 0.1f * (float) Math.sin(Minecraft.getInstance().player.tickCount * 0.2f)) : 1.0f;

            int iconX = (screenWidth / 2) + x - 10;
            int iconY = screenHeight - y + 2;

            guiGraphics.pose().pushPose();
            guiGraphics.pose().translate(iconX, iconY, 0);
            guiGraphics.pose().scale(scale, scale, 1.0f);
            guiGraphics.pose().translate(-iconX, -iconY, 0);
            guiGraphics.setColor(pulse, pulse, pulse, 1.0f);

            drawIcon(guiGraphics, ARMOR_ICON, iconX - 14, iconY - 6, 16, 16);
            guiGraphics.setColor(1.0f, 1.0f, 1.0f, 1.0f);
            drawShadowedText(guiGraphics, Minecraft.getInstance().font, String.valueOf(armorValue), (screenWidth / 2) + x, screenHeight - y, 0xAAAAAA);

            guiGraphics.pose().popPose();
        }
    }

    private static void drawToughness(GuiGraphics guiGraphics, int toughnessValue, int screenWidth, int screenHeight, int x, int y, boolean showIcon) {
        if (showIcon && toughnessValue > 0) {
            float scale = 1.0f;
            if (toughnessBounceTicks > 0) {
                scale = 1.0f + 0.2f * (float) Math.sin((10 - toughnessBounceTicks) * Math.PI / 10);
                toughnessBounceTicks--;
            }

            float pulse = (toughnessValue >= 5) ? (0.9f + 0.1f * (float) Math.sin(Minecraft.getInstance().player.tickCount * 0.2f)) : 1.0f;

            int iconX = (screenWidth / 2) + x - 10;
            int iconY = screenHeight - y + 2;

            guiGraphics.pose().pushPose();
            guiGraphics.pose().translate(iconX, iconY, 0);
            guiGraphics.pose().scale(scale, scale, 1.0f);
            guiGraphics.pose().translate(-iconX, -iconY, 0);
            guiGraphics.setColor(pulse, pulse, pulse, 1.0f);

            drawIcon(guiGraphics, TOUGHNESS_ICON, iconX - 14, iconY - 6, 16, 16);
            guiGraphics.setColor(1.0f, 1.0f, 1.0f, 1.0f);
            drawShadowedText(guiGraphics, Minecraft.getInstance().font, String.valueOf(toughnessValue), (screenWidth / 2) + x, screenHeight - y, 0xADD8E6);

            guiGraphics.pose().popPose();
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

    private static void drawAbsorption(GuiGraphics g, int absorption, int absX, int absY) {
        if (absorption > 0) {
            drawShadowedText(g, Minecraft.getInstance().font, "+" + absorption, absX, absY, 0xFFFF00);
        }
    }


    private static void drawSaturation(GuiGraphics guiGraphics, int saturation, int screenWidth, int screenHeight, int x, int y) {
        if (saturation > 0) {
            drawShadowedText(guiGraphics, Minecraft.getInstance().font, "+" + saturation, (screenWidth / 2) + x + 46, screenHeight - y, 0xFFD700);
        }
    }



    private static int getDynamicHealthColor(Player player) {
        float health = player.getHealth();
        float maxHealth = player.getMaxHealth();
        float ratio = health / maxHealth;

        if (ratio < 0.2f) {
            // Pulse from dark red to bright red
            float pulse = (float)(Math.sin(player.tickCount * 0.3f) * 0.5f + 0.5f);
            return interpolateColor(pulse, 0x800000, 0xFF0000);
        }

        if (ratio > 0.5f) {
            // Green → Yellow
            return interpolateColor((1.0f - ratio) * 2f, 0x00FF00, 0xFFFF00);
        } else {
            // Yellow → Red
            return interpolateColor((0.5f - ratio) * 2f, 0xFFFF00, 0xFF0000);
        }
    }
    private static int interpolateColor(float ratio, int colorStart, int colorEnd) {
        int r1 = (colorStart >> 16) & 0xFF;
        int g1 = (colorStart >> 8) & 0xFF;
        int b1 = colorStart & 0xFF;

        int r2 = (colorEnd >> 16) & 0xFF;
        int g2 = (colorEnd >> 8) & 0xFF;
        int b2 = colorEnd & 0xFF;

        int r = (int)(r1 + (r2 - r1) * ratio);
        int g = (int)(g1 + (g2 - g1) * ratio);
        int b = (int)(b1 + (b2 - b1) * ratio);

        return (r << 16) | (g << 8) | b;
    }

    private static void drawIcon(GuiGraphics guiGraphics, ResourceLocation icon, int x, int y, int width, int height) {
        RenderSystem.setShaderTexture(0, icon);
        guiGraphics.blit(icon, x, y, 0, 0, width, height, width, height);
    }

    private static void drawShadowedText(GuiGraphics guiGraphics, Font font, String text, int x, int y, int color) {
        guiGraphics.drawString(font, text, x, y, color, true);
    }
    @Nullable
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
