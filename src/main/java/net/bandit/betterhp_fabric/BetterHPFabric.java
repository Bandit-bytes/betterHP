package net.bandit.betterhp_fabric;

import net.bandit.betterhp_fabric.config.ConfigManager;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BetterHPFabric implements ModInitializer {
    public static final String MOD_ID = "better_hp";

    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        ConfigManager.loadConfig();

        LOGGER.info("BetterHP Fabric initialized");
    }
}