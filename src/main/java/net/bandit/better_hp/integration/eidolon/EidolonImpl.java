package net.bandit.better_hp.integration.eidolon;

import elucent.eidolon.capability.ISoul;
import net.minecraft.world.entity.player.Player;

/** Only class-loaded when Eidolon: Repraised is installed. */
public final class EidolonImpl {
    private EidolonImpl() {}

    public static float etherealHealth(Player player) {
        return player.getCapability(ISoul.INSTANCE)
                .map(soul -> Math.max(0F, soul.getEtherealHealth()))
                .orElse(0F);
    }
}
