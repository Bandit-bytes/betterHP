package net.bandit.betterhp_fabric;

import net.bandit.betterhp_fabric.client.HealthDisplayHandler;
import net.bandit.betterhp_fabric.config.ConfigManager;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BetterHPFabric implements ModInitializer {
	public static final String MOD_ID = "better_hp";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		ConfigManager.loadConfig();
		HudRenderCallback.EVENT.register(new HealthDisplayHandler());

		ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
			ConfigManager.saveConfig();
			ConfigManager.loadConfig();
		});

		System.out.println("BetterHPFabric Mod Initialized");
	}

}