package net.bandit.better_hp.integration;

import net.bandit.better_hp.config.BetterHPConfig;
import net.bandit.better_hp.integration.eidolon.EidolonImpl;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.player.Player;

import java.util.Optional;

/** Eidolon: Repraised. Safe to call whether or not Eidolon is installed. */
public final class EidolonCompat {
    private static final CompatState STATE = new CompatState("eidolon_repraised", "Eidolon: Repraised");

    /** Applied by Wraith melee hits, the Bonechill wand, frost spells and Frostbind incense. */
    private static final ResourceLocation CHILLED_ID = ResourceLocation.fromNamespaceAndPath("eidolon_repraised", "chilled");
    private static Optional<Holder.Reference<MobEffect>> chilled;

    private EidolonCompat() {}

    public static boolean isLoaded() {
        return STATE.isLoaded();
    }

    public static boolean active() {
        return STATE.usable() && BetterHPConfig.eidolonCompat.get();
    }

    /** The player is Chilled and frozen hearts are enabled. */
    public static boolean isChilled(Player player) {
        if (!active() || !BetterHPConfig.eidolonChilledFrozenHearts.get()) return false;
        if (chilled == null) {
            // Looked up via the registry, so no Eidolon classes are needed for this part.
            chilled = BuiltInRegistries.MOB_EFFECT.getHolder(CHILLED_ID);
        }
        return chilled.isPresent() && player.hasEffect(chilled.get());
    }

    /** Ethereal (soul) health, or 0 when there is none / it is hidden. */
    public static float etherealHealth(Player player) {
        if (!active() || !BetterHPConfig.eidolonShowEtherealHealth.get()) return 0F;
        try {
            return EidolonImpl.etherealHealth(player);
        } catch (Throwable t) {
            STATE.fail(t);
            return 0F;
        }
    }
}
