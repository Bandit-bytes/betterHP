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

        // Ensure the player is not in creative or spectator mode
        if (player == null || client.interactionManager.getCurrentGameMode() == GameMode.CREATIVE || client.interactionManager.getCurrentGameMode() == GameMode.SPECTATOR) {
            return;
        }

        // Gather player data for HUD
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
        int breatheColor = 0x00BFFF; // Light blue color for breathing

        // Get screen dimensions
        int screenWidth = client.getWindow().getScaledWidth();
        int screenHeight = client.getWindow().getScaledHeight();

        // Get display positions from config
        int healthPosX = screenWidth / 2 + ConfigManager.healthDisplayX();
        int healthPosY = screenHeight - ConfigManager.healthDisplayY();
        int hungerPosX = screenWidth / 2 + ConfigManager.hungerDisplayX();
        int hungerPosY = screenHeight - ConfigManager.hungerDisplayY();
        int armorPosX = screenWidth / 2 + ConfigManager.armorDisplayX();
        int armorPosY = screenHeight - ConfigManager.armorDisplayY();
        int breathePosX = screenWidth / 2 + ConfigManager.breatheDisplayX();
        int breathePosY = screenHeight - ConfigManager.breatheDisplayY();

        // Render health icon and values
        client.getProfiler().push("betterhp_healthIcon");
        if (ConfigManager.showHealthIcon()) {
            renderIcon(context, HEALTH_ICON, healthPosX - 18, healthPosY - 4);
            drawShadowedText(context, client, health + "/" + maxHealth, healthPosX, healthPosY, healthColor);
            if (absorption > 0) {
                int healthTextWidth = client.textRenderer.getWidth(health + "/" + maxHealth);
                drawShadowedText(context, client, "+" + absorption, healthPosX + healthTextWidth + 3, healthPosY, 0xFFFF00); // Yellow for absorption
            }
        }
        client.getProfiler().pop();

        // Render hunger icon and values
        client.getProfiler().push("betterhp_hungerIcon");
        if (ConfigManager.showHungerIcon()) {
            String hungerText = hunger + "/20";
            if (ConfigManager.getConfigData().showNumericHunger) { // Check config before rendering numeric hunger
                drawShadowedText(context, client, hungerText, hungerPosX - client.textRenderer.getWidth(hungerText), hungerPosY, hungerColor);
            }
            renderIcon(context, HUNGER_ICON, hungerPosX, hungerPosY - 4);
            if (saturation > 0) {
                int hungerTextWidth = client.textRenderer.getWidth(hungerText);
                drawShadowedText(context, client, "+" + saturation, hungerPosX + hungerTextWidth + 3, hungerPosY, 0xFFD700); // Gold for saturation
            }
        }
        client.getProfiler().pop();

        // Render armor icon and values
        client.getProfiler().push("betterhp_armorIcon");
        if (ConfigManager.showArmorIcon()) {
            renderIcon(context, ARMOR_ICON, armorPosX - 18, armorPosY - 4);
            drawShadowedText(context, client, String.valueOf(armorValue), armorPosX, armorPosY, 0xFFFFFF); // White for armor
        }
        client.getProfiler().pop();

        // Render breathing (oxygen) icon and values
        client.getProfiler().push("betterhp_breatheIcon");
        if (ConfigManager.showBreatheIcon() && (player.isSubmergedInWater() || air < maxAir)) {
            String breatheText = (air / 20) + "/" + (maxAir / 20);
            if (ConfigManager.getConfigData().showNumericOxygen) { // Check config before rendering numeric oxygen
                drawShadowedText(context, client, breatheText, breathePosX - client.textRenderer.getWidth(breatheText), breathePosY, breatheColor);
            }
            renderIcon(context, BREATHE_ICON, breathePosX, breathePosY - 4);
        }
        client.getProfiler().pop();
    }

    // Helper function to render icons
    private void renderIcon(DrawContext context, Identifier icon, int x, int y) {
        RenderSystem.setShaderTexture(0, icon);
        context.drawTexture(icon, x, y, 0, 0, 16, 16, 16, 16);
    }

    // Helper function to draw shadowed text
    private void drawShadowedText(DrawContext context, MinecraftClient client, String text, int x, int y, int color) {
        context.drawText(client.textRenderer, text, x, y, color, true); // 'true' enables shadow
    }

    // Determine health color dynamically
    private int determineHealthColor(PlayerEntity player) {
        if (player.hasStatusEffect(StatusEffects.POISON)) {
            return 0x00FF00; // Green for poison effect
        } else if (player.hasStatusEffect(StatusEffects.WITHER)) {
            return 0x707070; // Gray for wither effect
        }

        int health = MathHelper.ceil(player.getHealth());
        int maxHealth = MathHelper.ceil(player.getMaxHealth());

        if (health > maxHealth * 0.75) {
            return 0x00FF00; // Green when health is full
        } else if (health > maxHealth * 0.25) {
            return 0xFFFF00; // Yellow when health is medium
        } else {
            return 0xFF0000; // Red when health is low
        }
    }

    // Determine hunger color dynamically
    private int determineHungerColor(int hunger, int maxHunger) {
        if (hunger > maxHunger * 0.75) {
            return 0xFF8C00; // Orange for high hunger
        } else if (hunger > maxHunger * 0.25) {
            return 0xFFFF00; // Yellow for medium hunger
        } else {
            return 0xFF4500; // Dark red for low hunger
        }
    }
}
