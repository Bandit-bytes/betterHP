package net.bandit.betterhp_fabric;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Common entrypoint. Better HP is client-only: everything that touches client classes lives in
 * {@link net.bandit.betterhp_fabric.client.betterhp_fabricClient} and the client mixins, so a
 * dedicated server that happens to have the jar installed never loads them.
 */
public class BetterHPFabric implements ModInitializer {
	public static final String MOD_ID = "better_hp";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
	}
}
