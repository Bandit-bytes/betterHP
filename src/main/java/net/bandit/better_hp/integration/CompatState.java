package net.bandit.better_hp.integration;

import net.bandit.better_hp.BetterhpMod;
import net.neoforged.fml.ModList;

/**
 * Tracks whether an optional mod is present, and switches its compat off for the rest of
 * the session (instead of crashing or spamming the log every frame) if the other mod's
 * API ever changes underneath us.
 */
final class CompatState {
    private final String modId;
    private final String displayName;
    private Boolean loaded;
    private boolean broken;

    CompatState(String modId, String displayName) {
        this.modId = modId;
        this.displayName = displayName;
    }

    boolean isLoaded() {
        if (loaded == null) {
            ModList list = ModList.get();
            if (list == null) return false; // too early; ask again later
            loaded = list.isLoaded(modId);
        }
        return loaded;
    }

    /** Loaded and has not failed. */
    boolean usable() {
        return !broken && isLoaded();
    }

    void fail(Throwable t) {
        if (broken) return;
        broken = true;
        BetterhpMod.getLogger().error(
                "Better HP: {} compat failed and has been disabled for this session. "
                        + "This usually means {} changed its API - please report it.",
                displayName, displayName, t);
    }
}
