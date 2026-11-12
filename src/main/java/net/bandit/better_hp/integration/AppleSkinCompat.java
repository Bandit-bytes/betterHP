package net.bandit.better_hp.integration;

import net.bandit.better_hp.config.BetterHPConfig;
import net.bandit.better_hp.integration.appleskin.AppleSkinImpl;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

/** AppleSkin food/health/exhaustion previews. Safe to call whether or not AppleSkin is installed. */
public final class AppleSkinCompat {
    private static final CompatState STATE = new CompatState("appleskin", "AppleSkin");

    private AppleSkinCompat() {}

    public static boolean isLoaded() {
        return STATE.isLoaded();
    }

    public static boolean active() {
        return STATE.usable() && BetterHPConfig.CLIENT.appleSkinCompat.get();
    }

    @Nullable
    public static FoodPreview foodPreview(Player player) {
        if (!active()) return null;
        boolean food = BetterHPConfig.CLIENT.appleSkinFoodPreview.get();
        boolean health = BetterHPConfig.CLIENT.appleSkinHealthPreview.get();
        if (!food && !health) return null;
        try {
            return AppleSkinImpl.foodPreview(player, food, health);
        } catch (Throwable t) {
            STATE.fail(t);
            return null;
        }
    }

    /** Exhaustion as 0..1, or -1 when hidden. */
    public static float exhaustionFraction(Player player) {
        if (!active() || !BetterHPConfig.CLIENT.appleSkinExhaustionBar.get()) return -1F;
        try {
            return AppleSkinImpl.exhaustionFraction(player);
        } catch (Throwable t) {
            STATE.fail(t);
            return -1F;
        }
    }

    public record FoodPreview(int hunger, float saturation, float health) {}
}
