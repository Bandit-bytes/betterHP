package net.bandit.better_hp.integration.lso;

import net.bandit.better_hp.integration.LsoCompat.HealthExtras;
import net.bandit.better_hp.integration.LsoCompat.State;
import net.bandit.better_hp.integration.LsoCompat.Temperature;
import net.bandit.better_hp.integration.LsoCompat.Thirst;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;
import sfiomn.legendarysurvivaloverhaul.LegendarySurvivalOverhaul;
import sfiomn.legendarysurvivaloverhaul.api.health.HealthUtil;
import sfiomn.legendarysurvivaloverhaul.api.temperature.TemperatureEnum;
import sfiomn.legendarysurvivaloverhaul.api.temperature.TemperatureUtil;
import sfiomn.legendarysurvivaloverhaul.api.thirst.ThirstUtil;
import sfiomn.legendarysurvivaloverhaul.common.capabilities.health.HealthCapability;
import sfiomn.legendarysurvivaloverhaul.common.capabilities.temperature.TemperatureCapability;
import sfiomn.legendarysurvivaloverhaul.common.capabilities.thirst.ThirstCapability;
import sfiomn.legendarysurvivaloverhaul.common.integration.curios.CuriosUtil;
import sfiomn.legendarysurvivaloverhaul.config.Config;
import sfiomn.legendarysurvivaloverhaul.util.CapabilityUtil;
import sfiomn.legendarysurvivaloverhaul.util.EnumUtil;
import sfiomn.legendarysurvivaloverhaul.util.WorldUtil;

import java.util.Locale;

/** Only class-loaded when Legendary Survival Overhaul is installed. */
public final class LsoImpl {
    private LsoImpl() {}

    @Nullable
    public static Thirst thirst(Player player) {
        if (!Config.Baked.thirstEnabled || !ThirstUtil.isThirstActive(player)) return null;
        ThirstCapability thirst = CapabilityUtil.getThirstCapability(player);
        if (thirst == null) return null;
        int max = ThirstCapability.MAX_HYDRATION > 0 ? ThirstCapability.MAX_HYDRATION : 20;
        return new Thirst(Mth.clamp(thirst.getHydrationLevel(), 0, max), max, Math.max(0F, thirst.getSaturationLevel()));
    }

    @Nullable
    public static Temperature temperature(Player player) {
        if (!Config.Baked.temperatureEnabled) return null;
        boolean thermometer = LegendarySurvivalOverhaul.curiosLoaded && CuriosUtil.isThermometerEquipped;
        boolean symbol = Config.Baked.temperatureDisplayMode == EnumUtil.temperatureDisplayMode.SYMBOL;
        if (!symbol && !thermometer) return null;

        TemperatureCapability temp = CapabilityUtil.getTempCapability(player);
        if (temp == null) return null;

        float level = TemperatureUtil.clampTemperature(temp.getTemperatureLevel());
        float min = TemperatureEnum.getMin();
        float max = TemperatureEnum.getMax();
        float fraction = max > min ? Mth.clamp((level - min) / (max - min), 0F, 1F) : 0.5F;

        State state = switch (temp.getTemperatureEnum()) {
            case FROSTBITE -> State.FROSTBITE;
            case COLD -> State.COLD;
            case HOT -> State.HOT;
            case HEAT_STROKE -> State.HEAT_STROKE;
            default -> State.NORMAL;
        };

        String exact = null;
        if (thermometer) {
            exact = Config.Baked.renderTemperatureInFahrenheit
                    ? String.format(Locale.ROOT, "%.1f°F", Math.min(WorldUtil.toFahrenheit(level), 99.9F))
                    : String.format(Locale.ROOT, "%.1f°C", level);
        }
        return new Temperature(fraction, state, exact);
    }

    @Nullable
    public static HealthExtras healthExtras(Player player, boolean wantShield, boolean wantBroken) {
        if (!Config.Baked.healthOverhaulEnabled) return null;
        float shield = 0F;
        if (wantShield) {
            HealthCapability health = CapabilityUtil.getHealthCapability(player);
            if (health != null) shield = Math.max(0F, health.getShieldHealth());
        }
        int broken = wantBroken ? Math.max(0, HealthUtil.getEffectiveBrokenHearts(player)) : 0;
        if (shield <= 0F && broken <= 0) return null;
        return new HealthExtras(shield, broken);
    }
}
