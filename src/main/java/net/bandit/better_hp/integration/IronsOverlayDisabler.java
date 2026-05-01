package net.bandit.better_hp.integration;

import net.bandit.better_hp.BetterhpMod;
import net.bandit.better_hp.config.BetterHPConfig;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;

@EventBusSubscriber(modid = BetterhpMod.MOD_ID, value = Dist.CLIENT)
public final class IronsOverlayDisabler {

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onRenderLayerPre(RenderGuiLayerEvent.Pre event) {
        if (!IronsSpellbooksCompat.isLoaded()) return;
        if (!BetterHPConfig.hideIronsManaOverlay.get()) return;
        IronsSpellbooksCompat.forceDisableIronsManaBarConfig();

        ResourceLocation id = event.getName();

        if (!"irons_spellbooks".equals(id.getNamespace())) return;

        String path = id.getPath().toLowerCase();

        if (path.contains("mana")
                || path.contains("magic")
                || path.contains("hud")
                || path.contains("overlay")
                || path.contains("bar")) {
            event.setCanceled(true);
        }
    }

    private IronsOverlayDisabler() {}
}