package net.bandit.betterhp_fabric.mixin;

import net.bandit.betterhp_fabric.config.ConfigManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

/**
 * Nudges the held-item name so it doesn't overlap the Better HP armor row. Only the y argument
 * of vanilla's draw call changes, so vanilla's own logic (fade, rarity colour, custom names,
 * creative offset) and other mods' changes stay intact.
 */
@Mixin(Gui.class)
public abstract class SelectedItemNameOffsetMixin {

    @Shadow @Final private Minecraft minecraft;

    @ModifyArg(
            method = "renderSelectedItemName",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/GuiGraphics;drawStringWithBackdrop(Lnet/minecraft/client/gui/Font;Lnet/minecraft/network/chat/Component;IIII)I"),
            index = 3,
            require = 0
    )
    private int betterhp$offsetSelectedItemName(int y) {
        Player player = this.minecraft.player;
        // The Better HP HUD isn't drawn in creative/spectator, so there's nothing to dodge.
        if (player == null || this.minecraft.gameMode == null || !this.minecraft.gameMode.canHurtPlayer()) return y;
        if (ConfigManager.selectedItemNameOnlyWhenArmor() && player.getArmorValue() <= 0) return y;
        return y + ConfigManager.selectedItemNameOffsetY();
    }
}
