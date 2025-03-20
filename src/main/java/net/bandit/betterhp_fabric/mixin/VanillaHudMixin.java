package net.bandit.betterhp_fabric.mixin;

import net.bandit.betterhp_fabric.config.ConfigManager;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public class VanillaHudMixin {

	@Inject(method = "renderPlayerHealth", at = @At("HEAD"), cancellable = true)
	private void disablePlayerHealthRendering(GuiGraphics guiGraphics, CallbackInfo ci) {
		if (!ConfigManager.renderVanillaHud() && ConfigManager.showHealthIcon()) {
			ci.cancel();
		}
	}

	@Inject(method = "renderArmor", at = @At("HEAD"), cancellable = true)
	private static void disableArmorRendering(GuiGraphics guiGraphics, Player player, int i, int j, int k, int l, CallbackInfo ci) {
		if (!ConfigManager.renderVanillaHud() && ConfigManager.showArmorIcon()) {
			ci.cancel();
		}
	}

	@Inject(method = "renderFood", at = @At("HEAD"), cancellable = true)
	private void disableFoodRendering(GuiGraphics guiGraphics, Player player, int i, int j, CallbackInfo ci) {
		if (!ConfigManager.renderVanillaHud() && ConfigManager.showHungerIcon()) {
			ci.cancel();
		}
	}

	@Inject(method = "renderPlayerHealth", at = @At("HEAD"), cancellable = true)
	private void disableOxygenRendering(GuiGraphics guiGraphics, CallbackInfo ci) {
		if (!ConfigManager.renderVanillaHud() && ConfigManager.showBreatheIcon()) {
			ci.cancel();
		}
	}
}