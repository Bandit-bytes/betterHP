package net.bandit.betterhp_fabric.client;

import net.bandit.betterhp_fabric.BetterHPFabric;
import net.bandit.betterhp_fabric.config.ConfigManager;
import net.bandit.betterhp_fabric.integration.AppleSkinCompat;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;

public class betterhp_fabricClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ConfigManager.loadConfig();
        // Registered once (it used to be registered from both entrypoints, drawing the HUD twice per frame).
        HudRenderCallback.EVENT.register(new HealthDisplayHandler());
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> ConfigManager.saveConfig());
        if (AppleSkinCompat.isLoaded()) {
            BetterHPFabric.LOGGER.info("AppleSkin detected - compat enabled.");
        }
    }
}
