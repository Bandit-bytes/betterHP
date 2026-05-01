package net.bandit.better_hp.mixin;

import net.bandit.better_hp.BetterhpMod;
import net.bandit.better_hp.config.BetterHPConfig;
import net.minecraft.world.entity.player.Player;
import net.neoforged.fml.ModList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(
        targets = "io.redspace.ironsspellbooks.gui.overlays.ManaBarOverlay",
        remap = false
)
public abstract class IronsManaBarOverlayMixin {

    private static boolean betterhp$loggedOnce = false;

    @Inject(
            method = "shouldShowManaBar",
            at = @At("HEAD"),
            cancellable = true,
            remap = false
    )
    private static void betterhp$hideIronsManaBar(Player player, CallbackInfoReturnable<Boolean> cir) {
        if (!ModList.get().isLoaded("irons_spellbooks")) {
            return;
        }

        boolean hideOverlay;

        try {
            hideOverlay = BetterHPConfig.hideIronsManaOverlay.get();
        } catch (Throwable t) {
            return;
        }

        if (!hideOverlay) {
            return;
        }

        if (!betterhp$loggedOnce) {
            betterhp$loggedOnce = true;
            BetterhpMod.getLogger().info("Better HP: Suppressing Iron's original mana bar via ManaBarOverlay mixin.");
        }

        cir.setReturnValue(false);
    }
}