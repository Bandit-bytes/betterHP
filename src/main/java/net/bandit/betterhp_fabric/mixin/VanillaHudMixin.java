package net.bandit.betterhp_fabric.mixin;

import net.bandit.betterhp_fabric.config.ConfigManager;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public class VanillaHudMixin {

    @Inject(method = "extractHearts", at = @At("HEAD"), cancellable = true)
    private void betterhp$cancelHearts(
            GuiGraphicsExtractor guiGraphics,
            Player player,
            int x,
            int y,
            int height,
            int offsetHeartIndex,
            float maxHealth,
            int currentHealth,
            int displayHealth,
            int absorptionAmount,
            boolean renderHighlight,
            CallbackInfo ci
    ) {
        if (!ConfigManager.renderVanillaHud() && ConfigManager.showHealthIcon()) {
            ci.cancel();
        }
    }

    @Inject(method = "extractArmor", at = @At("HEAD"), cancellable = true)
    private static void betterhp$cancelArmor(
            GuiGraphicsExtractor guiGraphics,
            Player player,
            int height,
            int rowCount,
            int rowHeight,
            int left,
            CallbackInfo ci
    ) {
        if (!ConfigManager.renderVanillaHud() && ConfigManager.showArmorIcon()) {
            ci.cancel();
        }
    }

    @Inject(method = "extractFood", at = @At("HEAD"), cancellable = true)
    private void betterhp$cancelFood(
            GuiGraphicsExtractor guiGraphics,
            Player player,
            int top,
            int right,
            CallbackInfo ci
    ) {
        if (!ConfigManager.renderVanillaHud() && ConfigManager.showHungerIcon()) {
            ci.cancel();
        }
    }

    @Inject(method = "extractAirBubbles", at = @At("HEAD"), cancellable = true)
    private void betterhp$cancelAirBubbles(
            GuiGraphicsExtractor graphics,
            Player player,
            int vehicleHearts,
            int yLineAir,
            int xRight,
            CallbackInfo ci
    ) {
        if (!ConfigManager.renderVanillaHud() && ConfigManager.showBreatheIcon()) {
            ci.cancel();
        }
    }

    @Inject(method = "extractVehicleHealth", at = @At("HEAD"), cancellable = true)
    private void betterhp$cancelVehicleHealth(
            GuiGraphicsExtractor guiGraphics,
            CallbackInfo ci
    ) {
        if (!ConfigManager.renderVanillaHud() && ConfigManager.showHealthIcon()) {
            ci.cancel();
        }
    }
}