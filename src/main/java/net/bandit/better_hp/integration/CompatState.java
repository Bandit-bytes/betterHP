package net.bandit.better_hp.integration;

import net.minecraftforge.fml.ModList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Tracks whether an optional mod is present, and switches its compat off for the rest of
 * the session (instead of crashing or spamming the log every frame) if the other mod's
 * API ever changes underneath us.
 */
final class CompatState {
    private static final Logger LOGGER = LoggerFactory.getLogger("BetterHP");

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
            if (list == null) return false;
            loaded = list.isLoaded(modId);
        }
        return loaded;
    }

    boolean usable() {
        return !broken && isLoaded();
    }

    void fail(Throwable t) {
        if (broken) return;
        broken = true;
        LOGGER.error("Better HP: {} compat failed and has been disabled for this session. "
                + "This usually means {} changed its API - please report it.", displayName, displayName, t);
    }
}
