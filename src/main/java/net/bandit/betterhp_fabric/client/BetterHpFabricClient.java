package net.bandit.betterhp_fabric.client;

import net.bandit.betterhp_fabric.BetterHPFabric;
import net.bandit.betterhp_fabric.config.ConfigManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.resources.Identifier;

public class BetterHpFabricClient implements ClientModInitializer {

    private static final Identifier BETTER_HP_HUD =
            Identifier.fromNamespaceAndPath(BetterHPFabric.MOD_ID, "better_hp_hud");

    private static final HealthDisplayHandler HEALTH_DISPLAY_HANDLER = new HealthDisplayHandler();

    @Override
    public void onInitializeClient() {
        HudElementRegistry.attachElementBefore(
                VanillaHudElements.CHAT,
                BETTER_HP_HUD,
                HEALTH_DISPLAY_HANDLER::extract
        );

        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            ConfigManager.saveConfig();
            ConfigManager.loadConfig();
        });

        BetterHPFabric.LOGGER.info("BetterHP Fabric client initialized");
    }
}