package net.bandit.betterhp_fabric.mixin;

import net.bandit.betterhp_fabric.client.HealthDisplayHandler;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public class GuiMixin {

    @Unique
    private static final HealthDisplayHandler BETTERHP$HUD = new HealthDisplayHandler();

    @Inject(
            method = "render",
            at = @At("TAIL")
    )
    private void betterhp$renderAfterGui(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        BETTERHP$HUD.renderHud(guiGraphics, deltaTracker);
    }
}