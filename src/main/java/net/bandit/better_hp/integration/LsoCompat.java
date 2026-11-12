package net.bandit.better_hp.integration;

import net.bandit.better_hp.config.BetterHPConfig;
import net.bandit.better_hp.integration.lso.LsoImpl;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/** Legendary Survival Overhaul. Safe to call whether or not LSO is installed. */
public final class LsoCompat {
    private static final CompatState STATE = new CompatState("legendarysurvivaloverhaul", "Legendary Survival Overhaul");

    private static final ResourceLocation COLD_HUNGER_ID =
            ResourceLocation.fromNamespaceAndPath("legendarysurvivaloverhaul", "cold_hunger");
    private static Optional<Holder.Reference<MobEffect>> coldHunger;

    private LsoCompat() {}

    public static boolean isLoaded() {
        return STATE.isLoaded();
    }

    public static boolean active() {
        return STATE.usable() && BetterHPConfig.lsoCompat.get();
    }

    @Nullable
    public static Thirst thirst(Player player) {
        if (!active() || !BetterHPConfig.lsoReplaceThirstBar.get()) return null;
        try {
            return LsoImpl.thirst(player);
        } catch (Throwable t) {
            STATE.fail(t);
            return null;
        }
    }

    @Nullable
    public static Temperature temperature(Player player) {
        if (!active() || !BetterHPConfig.lsoReplaceTemperature.get()) return null;
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
        boolean shield = BetterHPConfig.lsoShowShieldHealth.get();
        boolean broken = BetterHPConfig.lsoShowBrokenHearts.get();
        if (!shield && !broken) return null;
        try {
            return LsoImpl.healthExtras(player, shield, broken);
        } catch (Throwable t) {
            STATE.fail(t);
            return null;
        }
    }

    public static boolean hasColdHunger(Player player) {
        if (!active() || !BetterHPConfig.lsoColdHungerFrozen.get()) return false;
        if (coldHunger == null) {
            coldHunger = BuiltInRegistries.MOB_EFFECT.getHolder(COLD_HUNGER_ID);
        }
        return coldHunger.isPresent() && player.hasEffect(coldHunger.get());
    }

    public record Thirst(int hydration, int maxHydration, float saturation) {}

    /**
     * @param fraction 0..1 position between LSO's min and max temperature (drives the thermometer fill)
     * @param state    LSO temperature band, used for colour
     * @param exactText formatted body temperature, or null when LSO wouldn't reveal it (no thermometer equipped)
     */
    public record Temperature(float fraction, State state, @Nullable String exactText) {}

    public enum State { FROSTBITE, COLD, NORMAL, HOT, HEAT_STROKE }

    /** @param shield shield health points; @param broken number of broken hearts */
    public record HealthExtras(float shield, int broken) {}
}
