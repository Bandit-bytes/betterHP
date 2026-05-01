package net.bandit.better_hp.mixin;

import net.bandit.better_hp.config.BetterHPConfig;
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
    private void onExtractHearts(
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
        if (!BetterHPConfig.showVanillaHearts.get()) {
            ci.cancel();
        }
    }

    @Inject(method = "extractFoodLevel", at = @At("HEAD"), cancellable = true)
    private void onExtractFoodLevel(GuiGraphicsExtractor guiGraphics, CallbackInfo ci) {
        if (!BetterHPConfig.showVanillaHunger.get()) {
            ci.cancel();
        }
    }

    @Inject(method = "extractArmorLevel", at = @At("HEAD"), cancellable = true)
    private void onExtractArmorLevel(GuiGraphicsExtractor guiGraphics, CallbackInfo ci) {
        if (!BetterHPConfig.showVanillaArmor.get()) {
            ci.cancel();
        }
    }

    @Inject(method = "extractAirLevel", at = @At("HEAD"), cancellable = true)
    private void onExtractAirLevel(GuiGraphicsExtractor guiGraphics, CallbackInfo ci) {
        if (!BetterHPConfig.showVanillaOxygen.get()) {
            ci.cancel();
        }
    }

    @Inject(method = "extractVehicleHealth", at = @At("HEAD"), cancellable = true)
    private void onExtractVehicleHealth(GuiGraphicsExtractor guiGraphics, CallbackInfo ci) {
        if (!BetterHPConfig.showVanillaMountHealth.get()) {
            ci.cancel();
        }
    }
}