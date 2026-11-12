package net.bandit.better_hp.mixin;

import net.bandit.better_hp.integration.IronsSpellbooksCompat;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Tells Iron's its mana bar is hidden while Better HP draws mana instead.
 *
 * <p>Hiding the layer alone is not enough: when Iron's mana bar is anchored to the XP bar,
 * Iron's hides the XP bar based on this method, so it must return false or the XP bar
 * would disappear with nothing in its place.
 *
 * <p>{@code @Pseudo} + {@code require = 0}: does nothing without Iron's, and a future
 * Iron's rename can never crash the game.
 */
@Pseudo
@Mixin(targets = "io.redspace.ironsspellbooks.gui.overlays.ManaBarOverlay", remap = false)
public abstract class IronsManaBarOverlayMixin {

    @Inject(method = "shouldShowManaBar", at = @At("HEAD"), cancellable = true, remap = false, require = 0)
    private static void betterhp$hideIronsManaBar(Player player, CallbackInfoReturnable<Boolean> cir) {
        if (IronsSpellbooksCompat.shouldHideIronsManaBar()) {
            cir.setReturnValue(false);
        }
    }
}
