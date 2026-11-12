package net.bandit.better_hp.client;

import net.bandit.better_hp.config.BetterHPConfig;
import net.bandit.better_hp.integration.AppleSkinCompat;
import net.bandit.better_hp.integration.EidolonCompat;
import net.bandit.better_hp.integration.IronsSpellbooksCompat;
import net.bandit.better_hp.integration.LsoCompat;
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

    // Iron's Spells 'n Spellbooks
    private static final ResourceLocation IRONS_MANA = rl("irons_spellbooks", "mana_overlay");

    // AppleSkin
    private static final ResourceLocation APPLESKIN_HEALTH = rl("appleskin", "health_restored");
    private static final ResourceLocation APPLESKIN_HUNGER = rl("appleskin", "hunger_restored");
    private static final ResourceLocation APPLESKIN_SATURATION = rl("appleskin", "saturation_level");
    private static final ResourceLocation APPLESKIN_EXHAUSTION = rl("appleskin", "exhaustion_level");

    // Eidolon: Repraised
    private static final ResourceLocation EIDOLON_HEARTS = rl("eidolon_repraised", "hearts");

    // Legendary Survival Overhaul
    private static final ResourceLocation LSO_HEALTH = rl("legendarysurvivaloverhaul", "health_overhaul");
    private static final ResourceLocation LSO_COLD_HUNGER = rl("legendarysurvivaloverhaul", "cold_hunger");
    private static final ResourceLocation LSO_THIRST = rl("legendarysurvivaloverhaul", "thirst");
    private static final ResourceLocation LSO_TEMPERATURE = rl("legendarysurvivaloverhaul", "temperature");

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

        // ---- Iron's: ONLY the mana bar. Spell bar / cast bar / recast bar stay visible.
        if (ns.equals("irons_spellbooks")) {
            return layer.equals(IRONS_MANA) && IronsSpellbooksCompat.shouldHideIronsManaBar();
        }

        // ---- AppleSkin: its overlays are drawn on top of the vanilla bars
        if (ns.equals("appleskin")) {
            if (!AppleSkinCompat.active() || !BetterHPConfig.appleSkinHideOverlays.get()) return false;
            if (layer.equals(APPLESKIN_HEALTH)) return heartsReplaced;
            if (layer.equals(APPLESKIN_HUNGER) || layer.equals(APPLESKIN_SATURATION) || layer.equals(APPLESKIN_EXHAUSTION)) {
                return hungerReplaced;
            }
            return false;
        }

        // ---- Eidolon: ethereal + chilled hearts are drawn on top of the vanilla hearts
        if (ns.equals("eidolon_repraised")) {
            return layer.equals(EIDOLON_HEARTS)
                    && EidolonCompat.active()
                    && BetterHPConfig.eidolonHideHeartsOverlay.get()
                    && heartsReplaced;
        }

        // ---- Legendary Survival Overhaul
        if (ns.equals("legendarysurvivaloverhaul")) {
            if (!LsoCompat.active()) return false;
            if (layer.equals(LSO_HEALTH)) return heartsReplaced;        // shield / broken hearts row
            if (layer.equals(LSO_COLD_HUNGER)) return hungerReplaced;   // frozen shanks drawn over the food bar
            if (layer.equals(LSO_THIRST)) return BetterHPConfig.lsoReplaceThirstBar.get();
            if (layer.equals(LSO_TEMPERATURE)) return BetterHPConfig.lsoReplaceTemperature.get();
            return false;
        }

        return false;
    }

    private static ResourceLocation rl(String ns, String path) {
        return ResourceLocation.fromNamespaceAndPath(ns, path);
    }
}
