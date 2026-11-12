package net.bandit.better_hp.integration;

import net.bandit.better_hp.config.BetterHPConfig;
import net.bandit.better_hp.integration.irons.IronsImpl;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

/** Iron's Spells 'n Spellbooks mana. Safe to call whether or not Iron's is installed. */
public final class IronsSpellbooksCompat {
    private static final CompatState STATE = new CompatState("irons_spellbooks", "Iron's Spells 'n Spellbooks");

    public record ManaInfo(int mana, int maxMana) {}

    private IronsSpellbooksCompat() {}

    public static boolean isLoaded() {
        return STATE.isLoaded();
    }

    /** Better HP is drawing mana right now. */
    public static boolean rendersMana() {
        return STATE.usable()
                && BetterHPConfig.CLIENT.enableIronsManaCompat.get()
                && BetterHPConfig.CLIENT.showMana.get();
    }

    /** Iron's own mana bar should be hidden (only while Better HP draws mana instead). */
    public static boolean shouldHideIronsManaBar() {
        return rendersMana() && BetterHPConfig.CLIENT.hideIronsManaOverlay.get();
    }

    @Nullable
    public static ManaInfo getMana(@Nullable Player player) {
        if (player == null || !rendersMana()) return null;
        try {
            return IronsImpl.getMana(player);
        } catch (Throwable t) {
            STATE.fail(t);
            return null;
        }
    }
}
