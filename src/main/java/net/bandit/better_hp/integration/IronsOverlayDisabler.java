package net.bandit.better_hp.integration;

import net.bandit.better_hp.BetterhpMod;
import net.bandit.better_hp.config.BetterHPConfig;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;

@EventBusSubscriber(modid = BetterhpMod.MOD_ID, value = Dist.CLIENT)
public final class IronsOverlayDisabler {

    @SubscribeEvent
    public static void onRenderLayerPre(RenderGuiLayerEvent.Pre event) {
        if (!IronsSpellbooksCompat.isLoaded()) return;
        if (!BetterHPConfig.enableIronsManaCompat.get()) return;
        if (!BetterHPConfig.hideIronsManaOverlay.get()) return;

        ResourceLocation id = event.getName();

        // Cancel Iron's mana layer(s)
        if ("irons_spellbooks".equals(id.getNamespace())) {
            String path = id.getPath();
            if (path.contains("mana")) {
                event.setCanceled(true);
            }
        }
    }

    private IronsOverlayDisabler() {}
}
