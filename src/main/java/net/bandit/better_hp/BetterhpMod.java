package net.bandit.better_hp;

import com.mojang.logging.LogUtils;
import net.bandit.better_hp.config.BetterHPConfig;
import net.bandit.better_hp.event.HealthDisplayHandler;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
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
        // Register common setup
        modEventBus.addListener(this::commonSetup);

        // Register the configuration handler to the mod event bus
        modEventBus.addListener(BetterHPConfig::onConfigLoad);

        // Register the configuration file
        modContainer.registerConfig(ModConfig.Type.CLIENT, BetterHPConfig.CLIENT_SPEC);
    }
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        LOGGER.info("Common setup complete for Better HP mod");
    }
    @EventBusSubscriber(modid = BetterhpMod.MOD_ID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            // Load the configuration values after the client setup event
            HealthDisplayHandler.loadCachedConfigValues();
        }
    }
}
