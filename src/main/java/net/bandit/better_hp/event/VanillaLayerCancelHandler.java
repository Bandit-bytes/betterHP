package net.bandit.better_hp.event;

import net.bandit.better_hp.BetterhpMod;
import net.bandit.better_hp.config.BetterHPConfig;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;


@EventBusSubscriber(modid = BetterhpMod.MOD_ID, value = Dist.CLIENT)
public class VanillaLayerCancelHandler {

    private static ResourceLocation id(Object o) {
        if (o instanceof ResourceLocation rl) return rl;
        try {
            return (ResourceLocation) o.getClass().getMethod("id").invoke(o);
        } catch (Exception ignored) {}
        try {
            return (ResourceLocation) o.getClass().getMethod("location").invoke(o);
        } catch (Exception ignored) {}
        throw new IllegalStateException("Unknown layer id type: " + o);
    }

    @SubscribeEvent
    public static void onLayerPre(RenderGuiLayerEvent.Pre event) {
        ResourceLocation layer = event.getName();

        if (!BetterHPConfig.showVanillaHearts.get()
                && layer.equals(id(VanillaGuiLayers.PLAYER_HEALTH))) event.setCanceled(true);

        if (!BetterHPConfig.showVanillaHunger.get()
                && layer.equals(id(VanillaGuiLayers.FOOD_LEVEL))) event.setCanceled(true);

        if (!BetterHPConfig.showVanillaArmor.get()
                && layer.equals(id(VanillaGuiLayers.ARMOR_LEVEL))) event.setCanceled(true);

        if (!BetterHPConfig.showVanillaOxygen.get()
                && layer.equals(id(VanillaGuiLayers.AIR_LEVEL))) event.setCanceled(true);

        if (!BetterHPConfig.showVanillaMountHealth.get()
                && layer.equals(id(VanillaGuiLayers.VEHICLE_HEALTH))) event.setCanceled(true);
    }
}