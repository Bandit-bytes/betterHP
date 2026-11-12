package net.bandit.better_hp.integration;

import net.bandit.better_hp.config.BetterHPConfig;
import net.bandit.better_hp.integration.eidolon.EidolonImpl;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

/** Eidolon: Repraised (mod id "eidolon" on 1.20.1). Safe to call whether or not Eidolon is installed. */
public final class EidolonCompat {
    private static final CompatState STATE = new CompatState("eidolon", "Eidolon: Repraised");

    /** Applied by Wraith melee hits, the Bonechill wand, frost spells and Frostbind incense. */
    private static final ResourceLocation CHILLED_ID = new ResourceLocation("eidolon", "chilled");
    private static boolean chilledLookedUp;
    @Nullable private static MobEffect chilled;

    private EidolonCompat() {}

    public static boolean isLoaded() {
        return STATE.isLoaded();
    }

    public static boolean active() {
        return STATE.usable() && BetterHPConfig.CLIENT.eidolonCompat.get();
    }

    public static boolean isChilled(Player player) {
        if (!active() || !BetterHPConfig.CLIENT.eidolonChilledFrozenHearts.get()) return false;
        if (!chilledLookedUp) {
            chilledLookedUp = true;
            chilled = ForgeRegistries.MOB_EFFECTS.getValue(CHILLED_ID);
        }
        return chilled != null && player.hasEffect(chilled);
    }

    public static float etherealHealth(Player player) {
        if (!active() || !BetterHPConfig.CLIENT.eidolonShowEtherealHealth.get()) return 0F;
        try {
            return EidolonImpl.etherealHealth(player);
        } catch (Throwable t) {
            STATE.fail(t);
            return 0F;
        }
    }
}
