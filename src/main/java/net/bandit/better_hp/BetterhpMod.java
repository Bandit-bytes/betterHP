package net.bandit.better_hp;

import net.bandit.better_hp.config.BetterHPConfig;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Common entry point. Better HP is client-only in practice; everything that touches
 * client classes lives in {@link net.bandit.better_hp.client.BetterhpClient}.
 */
@Mod(BetterhpMod.MOD_ID)
public class BetterhpMod {
    public static final String MOD_ID = "better_hp";
    private static final Logger LOGGER = LoggerFactory.getLogger("BetterHP");

    public static Logger getLogger() {
        return LOGGER;
    }

    public BetterhpMod(ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.CLIENT, BetterHPConfig.CLIENT_SPEC);
    }
}
