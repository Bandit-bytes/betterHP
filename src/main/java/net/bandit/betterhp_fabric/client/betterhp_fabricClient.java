package net.bandit.betterhp_fabric.client;

import net.bandit.betterhp_fabric.BetterHPFabric;
import net.bandit.betterhp_fabric.config.ConfigManager;
import net.bandit.betterhp_fabric.integration.AppleSkinCompat;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;

public class betterhp_fabricClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        // The HUD itself is drawn by GuiMixin (once per frame, after the vanilla HUD).
        ConfigManager.loadConfig();
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> ConfigManager.saveConfig());
        if (AppleSkinCompat.isLoaded()) {
            BetterHPFabric.LOGGER.info("AppleSkin detected - compat enabled.");
        }
    }
}
