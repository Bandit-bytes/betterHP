package net.bandit.better_hp.integration.eidolon;

import alexthw.eidolon_repraised.api.capability.ISoul;
import alexthw.eidolon_repraised.registries.EidolonCapabilities;
import net.minecraft.world.entity.player.Player;

/** Only class-loaded when Eidolon: Repraised is installed. */
public final class EidolonImpl {
    private EidolonImpl() {}

    public static float etherealHealth(Player player) {
        ISoul soul = player.getCapability(EidolonCapabilities.SOUL_HEART_CAPABILITY);
        if (soul == null) return 0F;
        return Math.max(0F, soul.getEtherealHealth());
    }
}
