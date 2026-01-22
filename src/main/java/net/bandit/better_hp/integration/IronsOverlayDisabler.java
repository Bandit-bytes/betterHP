package net.bandit.better_hp.integration;

import net.bandit.better_hp.config.BetterHPConfig;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.ModList;

@Mod.EventBusSubscriber(modid = "better_hp", value = Dist.CLIENT)
public final class IronsOverlayDisabler {

    @SubscribeEvent
    public static void onOverlayPre(RenderGuiOverlayEvent.Pre event) {
        if (!ModList.get().isLoaded("irons_spellbooks")) return;
        if (!BetterHPConfig.CLIENT.enableIronsManaCompat.get()) return;
        if (!BetterHPConfig.CLIENT.hideIronsManaOverlay.get()) return;

        ResourceLocation id = event.getOverlay().id();

        if ("irons_spellbooks".equals(id.getNamespace())) {
            String path = id.getPath();
            if (path.contains("mana")) {
                event.setCanceled(true);
            }
        }
    }

    private IronsOverlayDisabler() {}
}
