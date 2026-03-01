package net.bandit.better_hp.mixin;

import net.bandit.better_hp.config.BetterHPConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.component.DataComponents;
import net.minecraft.ChatFormatting;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions.FontContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public abstract class SelectedItemNameOffsetMixin {

    @Shadow private int toolHighlightTimer;
    @Shadow private ItemStack lastToolHighlight;

    @Inject(
            method = "renderSelectedItemName(Lnet/minecraft/client/gui/GuiGraphics;I)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private void betterhp$offsetSelectedItemName(GuiGraphics guiGraphics, int yShift, CallbackInfo ci) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (mc.options.hideGui || player == null) return;

        if (BetterHPConfig.selectedItemNameOnlyWhenArmor && player.getArmorValue() <= 0) return;

        if (this.toolHighlightTimer <= 0 || this.lastToolHighlight == null || this.lastToolHighlight.isEmpty()) return;

        ci.cancel();

        MutableComponent base = Component.empty()
                .append(this.lastToolHighlight.getHoverName())
                .withStyle(this.lastToolHighlight.getRarity().getStyleModifier());

        if (this.lastToolHighlight.has(DataComponents.CUSTOM_NAME)) {
            base.withStyle(ChatFormatting.ITALIC);
        }

        Component highlightTip = this.lastToolHighlight.getHighlightTip(base);


        int sw = guiGraphics.guiWidth();
        int sh = guiGraphics.guiHeight();
        int textWidth = mc.font.width(highlightTip);
        int x = (sw - textWidth) / 2;

        int y = sh - Math.max(yShift, 59);

        y += BetterHPConfig.selectedItemNameOffsetY.get();

        int alpha = (int)((float)this.toolHighlightTimer * 256.0F / 10.0F);
        alpha = Mth.clamp(alpha, 0, 255);
        if (alpha <= 0) return;

        var font = IClientItemExtensions.of(this.lastToolHighlight).getFont(this.lastToolHighlight, FontContext.SELECTED_ITEM_NAME);
        if (font == null) font = mc.font;

        int argb = FastColor.ARGB32.color(alpha, 255, 255, 255);

        guiGraphics.drawStringWithBackdrop(font, highlightTip, x, y, textWidth, argb);
    }
}