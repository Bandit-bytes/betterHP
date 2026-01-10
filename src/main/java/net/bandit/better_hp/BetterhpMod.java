package net.bandit.better_hp;

import net.bandit.better_hp.config.BetterHPConfig;
import net.bandit.better_hp.event.HealthDisplayHandler;
import net.bandit.better_hp.integration.IronsSpellbooksCompat;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(BetterhpMod.MOD_ID)
public class BetterhpMod {
    public static final String MOD_ID = "better_hp";
    private static final Logger LOGGER = LoggerFactory.getLogger(BetterhpMod.class);

    public static Logger getLogger() {
        return LOGGER;
    }

    public BetterhpMod(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(BetterHPConfig::onConfigLoad);
        modContainer.registerConfig(ModConfig.Type.CLIENT, BetterHPConfig.CLIENT_SPEC);
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {}

    private void commonSetup(final FMLCommonSetupEvent event) {
        LOGGER.info("Common setup complete for Better HP mod");
    }

    @EventBusSubscriber(modid = BetterhpMod.MOD_ID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            event.enqueueWork(() -> {
                HealthDisplayHandler.loadCachedConfigValues();

                if (ModList.get().isLoaded("irons_spellbooks")) {
                    LOGGER.info("Iron's Spells & Spellbooks detected - enabling mana compat.");
                    IronsSpellbooksCompat.initClient();
                } else {
                    LOGGER.info("Iron's Spells & Spellbooks not detected - mana compat disabled.");
                }
            });
        }
    }
}
