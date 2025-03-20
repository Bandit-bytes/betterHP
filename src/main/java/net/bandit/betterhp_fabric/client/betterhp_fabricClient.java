package net.bandit.betterhp_fabric.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;

public class betterhp_fabricClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        // Register the HealthDisplayHandler to the HudRenderCallback event
        HudRenderCallback.EVENT.register(new HealthDisplayHandler());
    }
}
