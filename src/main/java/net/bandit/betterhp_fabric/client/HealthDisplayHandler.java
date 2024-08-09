package net.bandit.betterhp_fabric.client;

import net.bandit.betterhp_fabric.config.ConfigManager;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.world.GameMode;

public class HealthDisplayHandler implements HudRenderCallback {

    private static final Identifier HEALTH_ICON = new Identifier("betterhp_fabric", "textures/gui/health_icon.png");
    private static final Identifier HUNGER_ICON = new Identifier("betterhp_fabric", "textures/gui/hunger_icon.png");
    private static final Identifier ARMOR_ICON = new Identifier("betterhp_fabric", "textures/gui/armor_icon.png");
    private static final Identifier BREATHE_ICON = new Identifier("betterhp_fabric", "textures/gui/breathe_icon.png");

    @Override
    public void onHudRender(DrawContext context, float tickDelta) {
        MinecraftClient client = MinecraftClient.getInstance();
        PlayerEntity player = client.player;

        if (player == null || client.interactionManager.getCurrentGameMode() == GameMode.CREATIVE) {
            return;
        }

        int health = MathHelper.ceil(player.getHealth());
        int maxHealth = MathHelper.ceil(player.getMaxHealth());
        int absorption = MathHelper.ceil(player.getAbsorptionAmount());
        int armorValue = player.getArmor();
        int hunger = player.getHungerManager().getFoodLevel();
        int saturation = MathHelper.ceil(player.getHungerManager().getSaturationLevel());
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

        MinecraftClient.getInstance().getProfiler().push("betterhp_healthIcon");
        if (ConfigManager.showHealthIcon()) {
            renderIcon(context, HEALTH_ICON, healthPosX - 18, healthPosY - 4);
            drawShadowedText(context, client, health + "/" + maxHealth, healthPosX, healthPosY, healthColor);
            if (absorption > 0) {
                int healthTextWidth = client.textRenderer.getWidth(health + "/" + maxHealth);
                drawShadowedText(context, client, "+" + absorption, healthPosX + healthTextWidth + 3, healthPosY, 0xFFFF00);
            }
        }
        MinecraftClient.getInstance().getProfiler().pop();

        MinecraftClient.getInstance().getProfiler().push("betterhp_hungerIcon");
        if (ConfigManager.showHungerIcon()) {
            drawShadowedText(context, client, hunger + "/20", hungerPosX - client.textRenderer.getWidth(hunger + "/20"), hungerPosY, hungerColor);
            renderIcon(context, HUNGER_ICON, hungerPosX, hungerPosY - 4);
            if (saturation > 0) {
                int hungerTextWidth = client.textRenderer.getWidth(hunger + "/20");
                drawShadowedText(context, client, "+" + saturation, hungerPosX + hungerTextWidth + 3, hungerPosY, 0xFFD700);
            }
        }
        MinecraftClient.getInstance().getProfiler().pop();

        MinecraftClient.getInstance().getProfiler().push("betterhp_armorIcon");
        if (ConfigManager.showArmorIcon()) {
            renderIcon(context, ARMOR_ICON, armorPosX - 18, armorPosY - 4);
            drawShadowedText(context, client, String.valueOf(armorValue), armorPosX, armorPosY, 0xFFFFFF);
        }
        MinecraftClient.getInstance().getProfiler().pop();

        MinecraftClient.getInstance().getProfiler().push("betterhp_breatheIcon");
        if (ConfigManager.showBreatheIcon() && player.isSubmergedInWater()) {
            drawShadowedText(context, client, (air / 20) + "/" + (maxAir / 20), breathePosX - client.textRenderer.getWidth((air / 20) + "/" + (maxAir / 20)), breathePosY, breatheColor);
            renderIcon(context, BREATHE_ICON, breathePosX, breathePosY - 4);
        }
        MinecraftClient.getInstance().getProfiler().pop();
    }

    private void renderIcon(DrawContext context, Identifier icon, int x, int y) {
        RenderSystem.setShaderTexture(0, icon);
        context.drawTexture(icon, x, y, 0, 0, 16, 16, 16, 16);
    }

    private void drawShadowedText(DrawContext context, MinecraftClient client, String text, int x, int y, int color) {
        context.drawText(client.textRenderer, text, x, y, color, true); // 'true' enables the shadow
    }

    private int determineHealthColor(PlayerEntity player) {
        if (player.hasStatusEffect(StatusEffects.POISON)) {
            return 0x00FF00;
        } else if (player.hasStatusEffect(StatusEffects.WITHER)) {
            return 0x707070;
        }

        int health = MathHelper.ceil(player.getHealth());
        int maxHealth = MathHelper.ceil(player.getMaxHealth());

        if (health > maxHealth * 0.75) {
            return 0x00FF00;
        } else if (health > maxHealth * 0.25) {
            return 0xFFFF00;
        } else {
            return 0xFF0000;
        }
    }

    private int determineHungerColor(int hunger, int maxHunger) {
        if (hunger > maxHunger * 0.75) {
            return 0xFF8C00;
        } else if (hunger > maxHunger * 0.25) {
            return 0xFFFF00;
        } else {
            return 0xFF4500;
        }
    }
}
