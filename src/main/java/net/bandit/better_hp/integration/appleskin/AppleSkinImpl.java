package net.bandit.better_hp.integration.appleskin;

import net.bandit.better_hp.integration.AppleSkinCompat.FoodPreview;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import squeek.appleskin.ModConfig;
import squeek.appleskin.helpers.FoodHelper;
import squeek.appleskin.helpers.HungerHelper;

/** Only class-loaded when AppleSkin is installed. */
public final class AppleSkinImpl {
    private static final int MAX_FOOD = 20;

    // FoodHelper.query fires AppleSkin's FoodValuesEvent; like AppleSkin, only query once per GUI tick.
    private static int cachedGuiTick = Integer.MIN_VALUE;
    @Nullable private static FoodProperties cachedFood;

    private AppleSkinImpl() {}

    @Nullable
    public static FoodPreview foodPreview(Player player, boolean wantFood, boolean wantHealth) {
        boolean showFood = wantFood && ModConfig.SHOW_FOOD_VALUES_OVERLAY.get();
        boolean showHealth = wantHealth && ModConfig.SHOW_FOOD_HEALTH_HUD_OVERLAY.get();
        if (!showFood && !showHealth) return null;

        FoodProperties food = heldFood(player);
        if (food == null) return null;

        int hunger = 0;
        float saturation = 0F;
        if (showFood) {
            FoodData data = player.getFoodData();
            int newFood = Mth.clamp(data.getFoodLevel() + food.nutrition(), 0, MAX_FOOD);
            // Same cap vanilla applies in FoodData.add: saturation can't exceed the food level.
            float newSaturation = Mth.clamp(data.getSaturationLevel() + food.saturation(), 0F, newFood);
            hunger = food.nutrition();
            saturation = Math.max(0F, newSaturation - data.getSaturationLevel());
        }

        float health = 0F;
        if (showHealth) {
            health = Math.max(0F, FoodHelper.getEstimatedHealthIncrement(player, food));
        }

        if (hunger <= 0 && saturation <= 0F && health <= 0F) return null;
        return new FoodPreview(hunger, saturation, health);
    }

    public static float exhaustionFraction(Player player) {
        if (!ModConfig.SHOW_FOOD_EXHAUSTION_UNDERLAY.get()) return -1F;
        float max = HungerHelper.getMaxExhaustion(player);
        if (max <= 0F) return -1F;
        // AppleSkin syncs exhaustion to the client, so this is accurate while it is installed.
        return Mth.clamp(player.getFoodData().getExhaustionLevel() / max, 0F, 1F);
    }

    @Nullable
    private static FoodProperties heldFood(Player player) {
        int tick = Minecraft.getInstance().gui.getGuiTicks();
        if (tick != cachedGuiTick) {
            cachedGuiTick = tick;
            cachedFood = queryHeldFood(player);
        }
        return cachedFood;
    }

    /** Mirrors AppleSkin's own held-food logic: main hand first, then off hand if AppleSkin allows it. */
    @Nullable
    private static FoodProperties queryHeldFood(Player player) {
        FoodProperties main = consumable(player.getMainHandItem(), player);
        if (main != null) return main;
        if (!ModConfig.SHOW_FOOD_VALUES_OVERLAY_WHEN_OFFHAND.get()) return null;
        return consumable(player.getOffhandItem(), player);
    }

    @Nullable
    private static FoodProperties consumable(ItemStack stack, Player player) {
        if (stack.isEmpty()) return null;
        FoodHelper.QueriedFoodResult result = FoodHelper.query(stack, player);
        if (result == null || result.modifiedFoodProperties == null) return null;
        return FoodHelper.canConsume(player, result.modifiedFoodProperties) ? result.modifiedFoodProperties : null;
    }
}
