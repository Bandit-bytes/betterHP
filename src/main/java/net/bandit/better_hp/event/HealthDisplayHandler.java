package net.bandit.better_hp.event;

import net.bandit.better_hp.config.BetterHPConfig;
import net.bandit.better_hp.integration.IronsSpellbooksCompat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;


@Mod.EventBusSubscriber(modid = "better_hp", value = Dist.CLIENT)
public class HealthDisplayHandler {

    private static final ResourceLocation HEALTH_ICON = new ResourceLocation("better_hp", "textures/gui/health_icon.png");
    private static final ResourceLocation HUNGER_ICON = new ResourceLocation("better_hp", "textures/gui/hunger_icon.png");
    private static final ResourceLocation ARMOR_ICON = new ResourceLocation("better_hp", "textures/gui/armor_icon.png");
    private static final ResourceLocation BREATHE_ICON = new ResourceLocation("better_hp", "textures/gui/breathe_icon.png");
    private static final ResourceLocation HARDCORE_HEALTH_ICON = new ResourceLocation("better_hp", "textures/gui/hardcore_health_icon.png");
    private static final ResourceLocation TOUGHNESS_ICON = new ResourceLocation("better_hp", "textures/gui/toughness_icon.png");
    private static final ResourceLocation MOUNT_ICON = new ResourceLocation("better_hp", "textures/gui/mount_icon.png");
    private static final ResourceLocation MANA_ICON = new ResourceLocation("better_hp", "textures/gui/mana_icon.png");




    private static boolean showVanillaArmor;
    private static boolean showNumericHunger;
    private static boolean showBreatheIcon;
    private static boolean showNumericOxygen;
    private static boolean showHealthIcon;
    private static boolean showArmorIcon;
    private static boolean showHungerIcon;
    private static boolean showNumericHealth;
    private static boolean enableDynamicColor;
    private static boolean showHealthOutline;

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
        showHealthOutline = BetterHPConfig.CLIENT.showHealthOutline.get();
    }

    @SubscribeEvent
    public static void onRenderGui(RenderGuiOverlayEvent.Pre event) {
        initializeConfigs();
        if (event.getOverlay() == VanillaGuiOverlay.PLAYER_HEALTH.type()) {
            event.setCanceled(event.isCanceled() || !BetterHPConfig.CLIENT.showVanillaHearts.get());
        } else if (event.getOverlay() == VanillaGuiOverlay.ARMOR_LEVEL.type()) {
            event.setCanceled(event.isCanceled() || !showVanillaArmor);
        } else if (event.getOverlay() == VanillaGuiOverlay.FOOD_LEVEL.type()) {
            event.setCanceled(event.isCanceled() || !BetterHPConfig.CLIENT.showVanillaHunger.get());
        } else if (event.getOverlay() == VanillaGuiOverlay.AIR_LEVEL.type()) {
            event.setCanceled(event.isCanceled() || !BetterHPConfig.CLIENT.showVanillaOxygen.get());
        }
        else if (event.getOverlay() == VanillaGuiOverlay.MOUNT_HEALTH.type()) {
            event.setCanceled(event.isCanceled() || !BetterHPConfig.CLIENT.showVanillaMountHealth.get());
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


        int textColor = enableDynamicColor ? determineHealthColor(Minecraft.getInstance().player) : BetterHPConfig.CLIENT.healthColor.get();
        int absorptionColor = 0xFFFF00;
        int armorColor = 0xAAAAAA;
        int hungerColor = enableDynamicColor ? determineHungerColor(hunger, 20) : 0xFF7518;
        int saturationColor = 0xFFD700;
        int breatheColor = 0x00BFFF;

        // ---- Irons mana
        if (BetterHPConfig.CLIENT.enableIronsManaCompat.get() && BetterHPConfig.CLIENT.showMana.get()) {
            var mana = IronsSpellbooksCompat.getMana(Minecraft.getInstance().player);
            if (mana != null) {
                int anchorX;
                int anchorY;

                if (BetterHPConfig.CLIENT.manaFollowHealthWidth.get() && showNumericHealth) {
                    String healthTextTmp = BetterHPConfig.CLIENT.showDecimalHealth.get()
                            ? String.format("%.1f/%.1f", health, maxHealth)
                            : String.format("%d/%d", (int) health, (int) maxHealth);

                    int healthTextWidthTmp = font.width(healthTextTmp);
                    int rightEdgeOfHealthText = (centeredHealthX - healthTextWidthTmp / 2)
                            + healthTextWidthTmp
                            + BetterHPConfig.CLIENT.healthTextOffset.get();

                    anchorX = rightEdgeOfHealthText;
                    anchorY = bottomHealthY;
                } else {
                    anchorX = centeredHealthX;
                    anchorY = bottomHealthY;
                }

                anchorX += BetterHPConfig.CLIENT.manaOffsetX.get();
                anchorY += BetterHPConfig.CLIENT.manaOffsetY.get();

                drawMana(guiGraphics, font,
                        mana.mana(), mana.maxMana(),
                        anchorX, anchorY,
                        BetterHPConfig.CLIENT.showManaIcon.get(),
                        BetterHPConfig.CLIENT.showNumericMana.get());
            }
        }


        if (showNumericHealth) {
            String healthText;
            if (BetterHPConfig.CLIENT.showDecimalHealth.get()) {
                healthText = String.format("%.1f/%.1f", health, maxHealth);
            } else {
                healthText = String.format("%d/%d", (int) health, (int) maxHealth);
            }
            int healthTextWidth = font.width(healthText);


            if (showHealthOutline) {
                drawOutlinedText(guiGraphics, font, healthText, centeredHealthX - healthTextWidth / 2 + healthTextOffset, bottomHealthY, textColor);
            } else {
                drawShadowedText(guiGraphics, font, healthText, centeredHealthX - healthTextWidth / 2 + healthTextOffset, bottomHealthY, textColor);
            }

            if (BetterHPConfig.CLIENT.showAbsorptionText.get() && absorption > 0) {
                int healthTextStartX = centeredHealthX - healthTextWidth / 2 + healthTextOffset;
                int healthTextRightX = healthTextStartX + healthTextWidth;

                int ax = BetterHPConfig.CLIENT.absorptionFollowHealthWidth.get()
                        ? (healthTextRightX + 5)
                        : (healthTextStartX + BetterHPConfig.CLIENT.absorptionOffsetX.get());

                int ay = bottomHealthY + BetterHPConfig.CLIENT.absorptionOffsetY.get();

                drawShadowedText(guiGraphics, font, "+" + absorption, ax, ay, absorptionColor);
            }

        }
        if (showNumericHunger) {
            String hungerText = hunger + "/20";
            int hungerTextWidth = font.width(hungerText);

            int hungerTextStartX = centeredHungerX - hungerTextWidth / 2;
            int hungerTextRightX = hungerTextStartX + hungerTextWidth;

            drawShadowedText(guiGraphics, font, hungerText,
                    hungerTextStartX, bottomHungerY, hungerColor);

            boolean vanillaHungerOn = BetterHPConfig.CLIENT.showVanillaHunger.get();
            boolean showSat = BetterHPConfig.CLIENT.showSaturationText.get();
            boolean hideSatWithVanilla = BetterHPConfig.CLIENT.hideSaturationIfVanillaHunger.get();

            if (showSat && saturation > 0 && !(hideSatWithVanilla && vanillaHungerOn)) {
                int saturationOffset = 14;
                drawShadowedText(guiGraphics, font, " +" + saturation,
                        hungerTextRightX + saturationOffset, bottomHungerY, saturationColor);
            }
        }

        if (showNumericOxygen && (air < maxAir)) {
            String breatheText = (air / 20) + "/" + (maxAir / 20);
            int breatheTextWidth = font.width(breatheText);

            drawShadowedText(guiGraphics, font, breatheText, centeredBreatheX - breatheTextWidth / 2, bottomBreatheY, breatheColor);
        }
        if (showHealthIcon) {
            Minecraft minecraft = Minecraft.getInstance();
            Player player = minecraft.player;

            boolean isHardcore = minecraft.level.getLevelData().isHardcore();
            ResourceLocation healthIcon = isHardcore ? HARDCORE_HEALTH_ICON : HEALTH_ICON;

            int shakeOffset = 0;
            if ((player.getHealth() / player.getMaxHealth()) < 0.2f) {
                shakeOffset = (int) (Math.sin(player.tickCount * 0.6f) * 2); // Subtle shake
            }

            drawIcon(guiGraphics, healthIcon, centeredHealthX - 24 + shakeOffset, bottomHealthY - 4, 16, 16);
        }

        if (!showVanillaArmor && showArmorIcon) {
            int toughness = (int) Minecraft.getInstance().player
                    .getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.ARMOR_TOUGHNESS)
                    .getValue();

            boolean hasArmorStuff = armorValue > 0 || toughness > 0;
            if (hasArmorStuff) {
                drawIcon(guiGraphics, ARMOR_ICON, centeredArmorX - 24, bottomArmorY - 4, 16, 16);
                drawShadowedText(guiGraphics, font, String.valueOf(armorValue), centeredArmorX, bottomArmorY, armorColor);

                if (toughness > 0) {
                    drawIcon(guiGraphics, TOUGHNESS_ICON, centeredArmorX + 20, bottomArmorY - 4, 16, 16);
                    drawShadowedText(guiGraphics, font, String.valueOf(toughness), centeredArmorX + 38, bottomArmorY, armorColor);
                }
            }
        }


        if (showHungerIcon) {
            drawIcon(guiGraphics, HUNGER_ICON, centeredHungerX + 18, bottomHungerY - 4, 16, 16);
        }
        if (showBreatheIcon && (air < maxAir)) {
            drawIcon(guiGraphics, BREATHE_ICON, centeredBreatheX + 18, bottomBreatheY - 4, 16, 16);
        }
        Player player = Minecraft.getInstance().player;
        LivingEntity mount = getMountWithHealth(player);

        if (mount != null && BetterHPConfig.CLIENT.showMountIcon.get()) {
            int mountHealth = Mth.ceil(mount.getHealth());
            int mountMaxHealth = Mth.ceil(mount.getMaxHealth());

            int mountX = (screenWidth / 2) + BetterHPConfig.CLIENT.mountDisplayX.get();
            int mountY = screenHeight - BetterHPConfig.CLIENT.mountDisplayY.get();

            drawIcon(guiGraphics, MOUNT_ICON, mountX - 24, mountY - 4, 16, 16);
            drawShadowedText(guiGraphics, font, mountHealth + "/" + mountMaxHealth, mountX, mountY, 0xAA77FF);
        }

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

    private static int interpolateColor(float ratio, int colorStart, int colorEnd) {
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
    private static void drawMana(GuiGraphics guiGraphics, Font font,
                                 int mana, int maxMana,
                                 int x, int y,
                                 boolean showIcon, boolean showNumeric) {

        String manaText = mana + "/" + maxMana;
        int textWidth = font.width(manaText);
        int color = 0x2F7DFF;

        if (showNumeric) {
            drawShadowedText(guiGraphics, font, manaText, x, y, color);
        }
        if (showIcon) {
            drawIcon(guiGraphics, MANA_ICON, x + textWidth, y - 4, 16, 16);
        }
    }


    private static int determineHealthColor(Player player) {
        if (player.hasEffect(MobEffects.POISON)) return 0x9ACD32; // Poison green
        if (player.hasEffect(MobEffects.WITHER)) return 0x403030; // Wither dark gray

        float health = player.getHealth();
        float maxHealth = player.getMaxHealth();
        float ratio = health / maxHealth;

        if (ratio < 0.2f) {
            float pulse = (float)(Math.sin(player.tickCount * 0.3f) * 0.5f + 0.5f);
            return interpolateColor(pulse, 0x800000, 0xFF0000);
        }

        if (ratio > 0.5f) {
            return interpolateColor((1.0f - ratio) * 2f, 0x00FF00, 0xFFFF00); // Green → Yellow
        } else {
            return interpolateColor((0.5f - ratio) * 2f, 0xFFFF00, 0xFF0000); // Yellow → Red
        }
    }

    private static int determineHungerColor(int hunger, int maxHunger) {
        float ratio = (float) hunger / maxHunger;

        if (ratio > 0.5f) {
            return interpolateColor((1.0f - ratio) * 2f, 0xFFFF00, 0xFFA500); // Yellow → Orange
        } else {
            return interpolateColor((0.5f - ratio) * 2f, 0xFFA500, 0xFF4500); // Orange → Red
        }
    }

    private static void drawIcon(GuiGraphics guiGraphics, ResourceLocation icon, int x, int y, int width, int height) {
        RenderSystem.setShaderTexture(0, icon);
        guiGraphics.blit(icon, x, y, 0, 0, width, height, width, height);
    }
    private static void drawShadowedText(GuiGraphics guiGraphics, Font font, String text, int x, int y, int color) {
        guiGraphics.drawString(font, text, x, y, color, true);
    }

private static void drawOutlinedText(GuiGraphics guiGraphics, Font font, String text, int x, int y, int color) {
    guiGraphics.drawString(font, text, x - 1, y, 0x000000, false);
    guiGraphics.drawString(font, text, x + 1, y, 0x000000, false);
    guiGraphics.drawString(font, text, x, y - 1, 0x000000, false);
    guiGraphics.drawString(font, text, x, y + 1, 0x000000, false);


    guiGraphics.drawString(font, text, x, y, color, false);
}

}
