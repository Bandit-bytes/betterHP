package net.bandit.better_hp.integration;

import net.bandit.better_hp.config.BetterHPConfig;
import net.bandit.better_hp.integration.irons.IronsImpl;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

/** Iron's Spells 'n Spellbooks mana. Safe to call whether or not Iron's is installed. */
public final class IronsSpellbooksCompat {
    private static final CompatState STATE = new CompatState("irons_spellbooks", "Iron's Spells 'n Spellbooks");

    private IronsSpellbooksCompat() {}

    public static boolean isLoaded() {
        return STATE.isLoaded();
    }

    /** Better HP is drawing mana in its own HUD right now. */
    public static boolean rendersMana() {
        return STATE.usable()
                && BetterHPConfig.hudActive()
                && BetterHPConfig.enableIronsManaCompat.get()
                && BetterHPConfig.showMana.get();
    }

    /** Iron's own mana bar should be suppressed (only when Better HP draws mana instead). */
    public static boolean shouldHideIronsManaBar() {
        return rendersMana() && BetterHPConfig.hideIronsManaOverlay.get();
    }

    @Nullable
    public static ManaSnapshot getMana(Player player) {
        if (!rendersMana() || player == null) return null;
        try {
            return IronsImpl.getMana(player);
        } catch (Throwable t) {
            STATE.fail(t);
            return null;
        }
    }

    public record ManaSnapshot(int mana, int maxMana) {}
}
