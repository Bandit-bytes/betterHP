package net.bandit.betterhp_fabric.mixin;

import net.bandit.betterhp_fabric.config.ConfigManager;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.FastColor.ARGB32;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public abstract class SelectedItemNameOffsetMixin {

    @Shadow private int toolHighlightTimer;
    @Shadow private ItemStack lastToolHighlight;

    @Shadow @Final private Minecraft minecraft;

    @Shadow public abstract Font getFont();

    @Inject(method = "renderSelectedItemName(Lnet/minecraft/client/gui/GuiGraphics;)V",
            at = @At("HEAD"),
            cancellable = true)
    private void betterhp$offsetSelectedItemName(GuiGraphics guiGraphics, CallbackInfo ci) {
        Player player = this.minecraft.player;
        if (player == null) return;
        if (this.minecraft.options.hideGui) return;

        if (ConfigManager.selectedItemNameOnlyWhenArmor() && player.getArmorValue() <= 0) return;

        if (this.toolHighlightTimer <= 0 || this.lastToolHighlight == null || this.lastToolHighlight.isEmpty()) return;

        ci.cancel();

        MutableComponent text = Component.empty()
                .append(this.lastToolHighlight.getHoverName())
                .withStyle(this.lastToolHighlight.getRarity().color());

        if (this.lastToolHighlight.has(DataComponents.CUSTOM_NAME)) {
            text.withStyle(ChatFormatting.ITALIC);
        }

        int width = this.getFont().width(text);
        int x = (guiGraphics.guiWidth() - width) / 2;

        int y = guiGraphics.guiHeight() - 59;
        if (!this.minecraft.gameMode.canHurtPlayer()) {
            y += 14;
        }

        y += ConfigManager.selectedItemNameOffsetY();

        int alpha = (int)((float)this.toolHighlightTimer * 256.0F / 10.0F);
        if (alpha > 255) alpha = 255;

        if (alpha > 0) {
            guiGraphics.drawStringWithBackdrop(
                    this.getFont(),
                    text,
                    x,
                    y,
                    width,
                    ARGB32.color(alpha, -1)
            );
        }
    }
}