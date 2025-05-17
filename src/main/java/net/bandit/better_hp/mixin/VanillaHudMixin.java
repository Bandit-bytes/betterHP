package net.bandit.better_hp.mixin;

import net.bandit.better_hp.config.BetterHPConfig;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;
import net.minecraft.client.gui.Gui;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public class VanillaHudMixin {

    @Inject(method = "renderHearts", at = @At("HEAD"), cancellable = true)
    private void onRenderHearts(GuiGraphics guiGraphics, Player player, int x, int y, int height, int offsetHeartIndex, float maxHealth, int currentHealth, int displayHealth, int absorptionAmount, boolean renderHighlight, CallbackInfo ci) {
        if (!BetterHPConfig.showVanillaHearts.get()) {
            // Cancel the vanilla health rendering if the config disables it
            ci.cancel();
        }
    }

    @Inject(method = "renderFoodLevel", at = @At("HEAD"), cancellable = true)
    private void onRenderFood(GuiGraphics p_283143_, CallbackInfo ci) {
        if (!BetterHPConfig.showVanillaHunger.get()) {
            // Cancel the vanilla hunger rendering if the config disables it
            ci.cancel();
        }
    }

    @Inject(method = "renderArmorLevel", at = @At("HEAD"), cancellable = true)
    private void onRenderArmor(GuiGraphics p_283143_, CallbackInfo ci) {
        if (!BetterHPConfig.showVanillaArmor.get()) {
            // Cancel the vanilla armor rendering if the config disables it
            ci.cancel();
        }
    }

    @Inject(method = "renderAirLevel", at = @At("HEAD"), cancellable = true)
    private void onRenderOxygen(GuiGraphics p_283143_, CallbackInfo ci) {
        if (!BetterHPConfig.showVanillaOxygen.get()) {
            // Cancel the vanilla oxygen rendering if the config disables it
            ci.cancel();
        }
    }
    @Inject(method = "renderVehicleHealth", at = @At("HEAD"), cancellable = true)
    private void onRenderMountHealth(GuiGraphics guiGraphics, CallbackInfo ci) {
        if (!BetterHPConfig.showVanillaMountHealth.get()) {
            ci.cancel(); // Cancel vanilla horse hearts if config says so
        }
    }
}
