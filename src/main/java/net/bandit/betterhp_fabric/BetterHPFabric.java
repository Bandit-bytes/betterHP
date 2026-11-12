package net.bandit.betterhp_fabric;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Common entrypoint. Better HP is client-only: the HUD and config events are registered from
 * {@link net.bandit.betterhp_fabric.client.betterhp_fabricClient}, so a dedicated server that has
 * the jar installed never loads client classes.
 */
public class BetterHPFabric implements ModInitializer {
    public static final String MOD_ID = "better_hp";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
    }
}
