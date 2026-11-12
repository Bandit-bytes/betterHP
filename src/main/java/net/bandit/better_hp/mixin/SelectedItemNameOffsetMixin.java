package net.bandit.better_hp.mixin;

import net.bandit.better_hp.config.BetterHPConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

/**
 * Nudges the held-item name ("Splash Potion of Healing") so it doesn't overlap the Better HP
 * armor/toughness row.
 *
 * <p>Only the y argument of the draw call is changed, so vanilla's own logic (fade, custom
 * item fonts, creative offset, NeoForge highlight tips) and other mods' changes stay intact.
 */
@Mixin(Gui.class)
public abstract class SelectedItemNameOffsetMixin {

    @Shadow @Final private Minecraft minecraft;

    @ModifyArg(
            method = "renderSelectedItemName(Lnet/minecraft/client/gui/GuiGraphics;I)V",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/GuiGraphics;drawStringWithBackdrop(Lnet/minecraft/client/gui/Font;Lnet/minecraft/network/chat/Component;IIII)V"),
            index = 3,
            require = 0
    )
    private int betterhp$offsetSelectedItemName(int y) {
        if (!BetterHPConfig.hudActive()) return y;

        Player player = this.minecraft.player;
        // The Better HP HUD isn't drawn in creative/spectator, so there's nothing to dodge.
        if (player == null || this.minecraft.gameMode == null || !this.minecraft.gameMode.canHurtPlayer()) return y;
        if (BetterHPConfig.moveSelectedItemNameOnlyWhenArmor.get() && player.getArmorValue() <= 0) return y;

        return y + BetterHPConfig.selectedItemNameOffsetY.get();
    }
}
