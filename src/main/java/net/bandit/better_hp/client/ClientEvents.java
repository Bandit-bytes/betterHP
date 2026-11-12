package net.bandit.better_hp.client;

import net.bandit.better_hp.BetterHP;
import net.bandit.better_hp.config.BetterHPConfig;
import net.bandit.better_hp.integration.AppleSkinCompat;
import net.bandit.better_hp.integration.EidolonCompat;
import net.bandit.better_hp.integration.IronsSpellbooksCompat;
import net.bandit.better_hp.integration.LsoCompat;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/** Client-only event wiring. */
public final class ClientEvents {
    private ClientEvents() {}

    @Mod.EventBusSubscriber(modid = BetterHP.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static final class ModBus {
        @SubscribeEvent
        public static void registerOverlays(RegisterGuiOverlaysEvent event) {
            // A real overlay: drawn once per frame, under chat, hidden by F1.
            event.registerAbove(VanillaGuiOverlay.AIR_LEVEL.id(), "hud", new HudOverlay());
        }

        @SubscribeEvent
        public static void clientSetup(net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent event) {
            logCompat();
        }
    }

    @Mod.EventBusSubscriber(modid = BetterHP.MOD_ID, value = Dist.CLIENT)
    public static final class ForgeBus {
        // Iron's
        private static final ResourceLocation IRONS_MANA = rl("irons_spellbooks", "mana_overlay");
        // Eidolon
        private static final ResourceLocation EIDOLON_HEARTS = rl("eidolon", "hearts");
        // Legendary Survival Overhaul
        private static final ResourceLocation LSO_HEALTH = rl("legendarysurvivaloverhaul", "health_overhaul");
        private static final ResourceLocation LSO_COLD_HUNGER = rl("legendarysurvivaloverhaul", "cold_hunger");
        private static final ResourceLocation LSO_THIRST = rl("legendarysurvivaloverhaul", "thirst");
        private static final ResourceLocation LSO_TEMPERATURE = rl("legendarysurvivaloverhaul", "temperature");

        /** Hide vanilla / other-mod HUD pieces only while Better HP shows the same information. */
        @SubscribeEvent
        public static void onOverlayPre(RenderGuiOverlayEvent.Pre event) {
            if (!BetterHPConfig.isLoaded()) return;
            if (shouldHide(event.getOverlay().id())) event.setCanceled(true);
        }

        private static boolean shouldHide(ResourceLocation id) {
            BetterHPConfig.Client cfg = BetterHPConfig.CLIENT;
            boolean heartsReplaced = !cfg.showVanillaHearts.get();
            boolean hungerReplaced = !cfg.showVanillaHunger.get();

            if (id.equals(VanillaGuiOverlay.PLAYER_HEALTH.id())) return heartsReplaced;
            if (id.equals(VanillaGuiOverlay.FOOD_LEVEL.id())) return hungerReplaced;
            if (id.equals(VanillaGuiOverlay.ARMOR_LEVEL.id())) return !cfg.showVanillaArmor.get();
            if (id.equals(VanillaGuiOverlay.AIR_LEVEL.id())) return !cfg.showVanillaOxygen.get();
            if (id.equals(VanillaGuiOverlay.MOUNT_HEALTH.id())) return !cfg.showVanillaMountHealth.get();

            switch (id.getNamespace()) {
                case "irons_spellbooks":
                    // ONLY the mana bar: spell bar, cast bar and recast bar stay visible.
                    return id.equals(IRONS_MANA) && IronsSpellbooksCompat.shouldHideIronsManaBar();
                case "eidolon":
                    return id.equals(EIDOLON_HEARTS) && EidolonCompat.active()
                            && cfg.eidolonHideHeartsOverlay.get() && heartsReplaced;
                case "legendarysurvivaloverhaul":
                    if (!LsoCompat.active()) return false;
                    if (id.equals(LSO_HEALTH)) return heartsReplaced;
                    if (id.equals(LSO_COLD_HUNGER)) return hungerReplaced;
                    if (id.equals(LSO_THIRST)) return cfg.lsoReplaceThirstBar.get();
                    if (id.equals(LSO_TEMPERATURE)) return cfg.lsoReplaceTemperature.get();
                    return false;
                default:
                    // AppleSkin 1.20.1 draws from the vanilla bars' Post events, so it is hidden
                    // automatically together with the vanilla bar it decorates.
                    return false;
            }
        }

        private static ResourceLocation rl(String ns, String path) {
            return new ResourceLocation(ns, path);
        }
    }

    static void logCompat() {
        log("Iron's Spells 'n Spellbooks", IronsSpellbooksCompat.isLoaded());
        log("AppleSkin", AppleSkinCompat.isLoaded());
        log("Eidolon: Repraised", EidolonCompat.isLoaded());
        log("Legendary Survival Overhaul", LsoCompat.isLoaded());
    }

    private static void log(String name, boolean loaded) {
        if (loaded) org.slf4j.LoggerFactory.getLogger("BetterHP").info("{} detected - compat enabled.", name);
    }
}
