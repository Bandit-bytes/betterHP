package net.bandit.better_hp.client;

import net.bandit.better_hp.config.BetterHPConfig;
import net.bandit.better_hp.integration.AppleSkinCompat;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;

/**
 * Decides which vanilla / other-mod HUD layers to hide while Better HP replaces them.
 *
 * <p>Other mods' layers are matched by exact id. A layer is only hidden when Better HP is
 * actually drawing the equivalent information, so turning a Better HP feature off always
 * brings the original back.
 */
final class LayerVisibilityHandler {

    // AppleSkin
    private static final ResourceLocation APPLESKIN_HEALTH = rl("appleskin", "health_restored");
    private static final ResourceLocation APPLESKIN_HUNGER = rl("appleskin", "hunger_restored");
    private static final ResourceLocation APPLESKIN_SATURATION = rl("appleskin", "saturation_level");
    private static final ResourceLocation APPLESKIN_EXHAUSTION = rl("appleskin", "exhaustion_level");

    private LayerVisibilityHandler() {}

    static void onLayerPre(RenderGuiLayerEvent.Pre event) {
        if (!BetterHPConfig.hudActive()) return;
        if (shouldHide(event.getName())) {
            event.setCanceled(true);
        }
    }

    private static boolean shouldHide(ResourceLocation layer) {
        boolean heartsReplaced = !BetterHPConfig.showVanillaHearts.get();
        boolean hungerReplaced = !BetterHPConfig.showVanillaHunger.get();

        // ---- vanilla
        if (layer.equals(VanillaGuiLayers.PLAYER_HEALTH)) return heartsReplaced;
        if (layer.equals(VanillaGuiLayers.FOOD_LEVEL)) return hungerReplaced;
        if (layer.equals(VanillaGuiLayers.ARMOR_LEVEL)) return !BetterHPConfig.showVanillaArmor.get();
        if (layer.equals(VanillaGuiLayers.AIR_LEVEL)) return !BetterHPConfig.showVanillaOxygen.get();
        if (layer.equals(VanillaGuiLayers.VEHICLE_HEALTH)) return !BetterHPConfig.showVanillaMountHealth.get();

        String ns = layer.getNamespace();

        // ---- AppleSkin: its overlays are drawn on top of the vanilla bars
        if (ns.equals("appleskin")) {
            if (!AppleSkinCompat.active() || !BetterHPConfig.appleSkinHideOverlays.get()) return false;
            if (layer.equals(APPLESKIN_HEALTH)) return heartsReplaced;
            if (layer.equals(APPLESKIN_HUNGER) || layer.equals(APPLESKIN_SATURATION) || layer.equals(APPLESKIN_EXHAUSTION)) {
                return hungerReplaced;
            }
            return false;
        }

        return false;
    }

    private static ResourceLocation rl(String ns, String path) {
        return ResourceLocation.fromNamespaceAndPath(ns, path);
    }
}
