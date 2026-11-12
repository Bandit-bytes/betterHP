package net.bandit.better_hp.integration.appleskin;

import net.bandit.better_hp.integration.AppleSkinCompat.FoodPreview;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import squeek.appleskin.ModConfig;
import squeek.appleskin.api.food.FoodValues;
import squeek.appleskin.helpers.FoodHelper;
import squeek.appleskin.helpers.HungerHelper;

/** Only class-loaded when AppleSkin is installed. */
public final class AppleSkinImpl {
    private static final int MAX_FOOD = 20;

    private record Held(ItemStack stack, FoodValues values) {}

    private static int cachedGuiTick = Integer.MIN_VALUE;
    @Nullable private static Held cached;

    private AppleSkinImpl() {}

    @Nullable
    public static FoodPreview foodPreview(Player player, boolean wantFood, boolean wantHealth) {
        boolean showFood = wantFood && ModConfig.SHOW_FOOD_VALUES_OVERLAY.get();
        boolean showHealth = wantHealth && ModConfig.SHOW_FOOD_HEALTH_HUD_OVERLAY.get();
        if (!showFood && !showHealth) return null;

        Held held = heldFood(player);
        if (held == null) return null;

        int hunger = 0;
        float saturation = 0F;
        if (showFood) {
            FoodData data = player.getFoodData();
            int newFood = Mth.clamp(data.getFoodLevel() + held.values().hunger, 0, MAX_FOOD);
            float newSaturation = Mth.clamp(data.getSaturationLevel() + held.values().getSaturationIncrement(), 0F, newFood);
            hunger = held.values().hunger;
            saturation = Math.max(0F, newSaturation - data.getSaturationLevel());
        }
        float health = showHealth ? Math.max(0F, FoodHelper.getEstimatedHealthIncrement(held.stack(), held.values(), player)) : 0F;

        if (hunger <= 0 && saturation <= 0F && health <= 0F) return null;
        return new FoodPreview(hunger, saturation, health);
    }

    public static float exhaustionFraction(Player player) {
        if (!ModConfig.SHOW_FOOD_EXHAUSTION_UNDERLAY.get()) return -1F;
        float max = HungerHelper.getMaxExhaustion(player);
        if (max <= 0F) return -1F;
        return Mth.clamp(player.getFoodData().getExhaustionLevel() / max, 0F, 1F);
    }

    @Nullable
    private static Held heldFood(Player player) {
        int tick = Minecraft.getInstance().gui.getGuiTicks();
        if (tick != cachedGuiTick) {
            cachedGuiTick = tick;
            Held main = consumable(player.getMainHandItem(), player);
            cached = main != null ? main
                    : ModConfig.SHOW_FOOD_VALUES_OVERLAY_WHEN_OFFHAND.get() ? consumable(player.getOffhandItem(), player) : null;
        }
        return cached;
    }

    @Nullable
    private static Held consumable(ItemStack stack, Player player) {
        if (stack.isEmpty() || !FoodHelper.isFood(stack, player) || !FoodHelper.canConsume(stack, player)) return null;
        FoodValues values = FoodHelper.getModifiedFoodValues(stack, player);
        return values == null ? null : new Held(stack, values);
    }
}
