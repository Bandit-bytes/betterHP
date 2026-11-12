package net.bandit.betterhp_fabric.integration;

import net.bandit.betterhp_fabric.BetterHPFabric;
import net.bandit.betterhp_fabric.config.ConfigManager;
import net.bandit.betterhp_fabric.integration.appleskin.AppleSkinImpl;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.player.PlayerEntity;
import org.jetbrains.annotations.Nullable;

/**
 * AppleSkin food/health/exhaustion previews. Safe to call whether or not AppleSkin is installed:
 * AppleSkin classes are only touched from {@link AppleSkinImpl}, and if AppleSkin's API ever
 * changes the compat switches itself off once instead of crashing.
 */
public final class AppleSkinCompat {
    private static Boolean loaded;
    private static boolean broken;

    private AppleSkinCompat() {}

    public static boolean isLoaded() {
        if (loaded == null) loaded = FabricLoader.getInstance().isModLoaded("appleskin");
        return loaded;
    }

    public static boolean active() {
        return !broken && isLoaded() && ConfigManager.appleSkinCompat();
    }

    @Nullable
    public static FoodPreview foodPreview(PlayerEntity player) {
        if (!active()) return null;
        boolean food = ConfigManager.appleSkinFoodPreview();
        boolean health = ConfigManager.appleSkinHealthPreview();
        if (!food && !health) return null;
        try {
            return AppleSkinImpl.foodPreview(player, food, health);
        } catch (Throwable t) {
            fail(t);
            return null;
        }
    }

    /** Exhaustion as 0..1, or -1 when hidden. */
    public static float exhaustionFraction(PlayerEntity player) {
        if (!active() || !ConfigManager.appleSkinExhaustionBar()) return -1F;
        try {
            return AppleSkinImpl.exhaustionFraction(player);
        } catch (Throwable t) {
            fail(t);
            return -1F;
        }
    }

    private static void fail(Throwable t) {
        if (broken) return;
        broken = true;
        BetterHPFabric.LOGGER.error("Better HP: AppleSkin compat failed and has been disabled for this session. Please report it.", t);
    }

    public record FoodPreview(int hunger, float saturation, float health) {}
}
