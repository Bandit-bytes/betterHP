package net.bandit.better_hp;

import net.bandit.better_hp.config.BetterHPConfig;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;

@Mod(BetterHP.MOD_ID)
public class BetterHP {
    public static final String MOD_ID = "better_hp";

    public BetterHP() {
        // Client-only mod: config + HUD wiring live in net.bandit.better_hp.client (Dist.CLIENT subscribers).
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, BetterHPConfig.CLIENT_SPEC);
    }
}
