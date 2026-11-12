package net.bandit.better_hp.integration;

import net.bandit.better_hp.config.BetterHPConfig;
import net.bandit.better_hp.integration.lso.LsoImpl;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

/** Legendary Survival Overhaul. Safe to call whether or not LSO is installed. */
public final class LsoCompat {
    private static final CompatState STATE = new CompatState("legendarysurvivaloverhaul", "Legendary Survival Overhaul");

    private static final ResourceLocation COLD_HUNGER_ID = new ResourceLocation("legendarysurvivaloverhaul", "cold_hunger");
    private static boolean coldHungerLookedUp;
    @Nullable private static MobEffect coldHunger;

    private LsoCompat() {}

    public static boolean isLoaded() {
        return STATE.isLoaded();
    }

    public static boolean active() {
        return STATE.usable() && BetterHPConfig.CLIENT.lsoCompat.get();
    }

    @Nullable
    public static Thirst thirst(Player player) {
        if (!active() || !BetterHPConfig.CLIENT.lsoReplaceThirstBar.get()) return null;
        try {
            return LsoImpl.thirst(player);
        } catch (Throwable t) {
            STATE.fail(t);
            return null;
        }
    }

    @Nullable
    public static Temperature temperature(Player player) {
        if (!active() || !BetterHPConfig.CLIENT.lsoReplaceTemperature.get()) return null;
        try {
            return LsoImpl.temperature(player);
        } catch (Throwable t) {
            STATE.fail(t);
            return null;
        }
    }

    @Nullable
    public static HealthExtras healthExtras(Player player) {
        if (!active()) return null;
        boolean shield = BetterHPConfig.CLIENT.lsoShowShieldHealth.get();
        boolean broken = BetterHPConfig.CLIENT.lsoShowBrokenHearts.get();
        if (!shield && !broken) return null;
        try {
            return LsoImpl.healthExtras(player, shield, broken);
        } catch (Throwable t) {
            STATE.fail(t);
            return null;
        }
    }

    public static boolean hasColdHunger(Player player) {
        if (!active() || !BetterHPConfig.CLIENT.lsoColdHungerFrozen.get()) return false;
        if (!coldHungerLookedUp) {
            coldHungerLookedUp = true;
            coldHunger = ForgeRegistries.MOB_EFFECTS.getValue(COLD_HUNGER_ID);
        }
        return coldHunger != null && player.hasEffect(coldHunger);
    }

    public record Thirst(int hydration, int maxHydration, float saturation) {}

    public record Temperature(float fraction, State state, @Nullable String exactText) {}

    public enum State { FROSTBITE, COLD, NORMAL, HOT, HEAT_STROKE }

    /** @param shield shield health points; @param broken number of broken hearts */
    public record HealthExtras(float shield, int broken) {}
}
