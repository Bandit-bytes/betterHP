package net.bandit.betterhp_fabric.integration.appleskin;

import net.bandit.betterhp_fabric.integration.AppleSkinCompat.FoodPreview;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import squeek.appleskin.ModConfig;
import squeek.appleskin.helpers.FoodHelper;

/** Only class-loaded when AppleSkin is installed. */
public final class AppleSkinImpl {
    private static final int MAX_FOOD = 20;

    private static int cachedGuiTick = Integer.MIN_VALUE;
    @Nullable private static FoodProperties cachedFood;

    private AppleSkinImpl() {}

    @Nullable
    public static FoodPreview foodPreview(Player player, boolean wantFood, boolean wantHealth) {
        ModConfig cfg = ModConfig.INSTANCE;
        boolean showFood = wantFood && (cfg == null || cfg.showFoodValuesHudOverlay);
        boolean showHealth = wantHealth && (cfg == null || cfg.showFoodHealthHudOverlay);
        if (!showFood && !showHealth) return null;

        FoodProperties food = heldFood(player, cfg == null || cfg.showFoodValuesHudOverlayWhenOffhand);
        if (food == null) return null;

        int hunger = 0;
        float saturation = 0F;
        if (showFood) {
            FoodData data = player.getFoodData();
            int newFood = Mth.clamp(data.getFoodLevel() + food.nutrition(), 0, MAX_FOOD);
            float newSaturation = Mth.clamp(data.getSaturationLevel() + food.saturation(), 0F, newFood);
            hunger = food.nutrition();
            saturation = Math.max(0F, newSaturation - data.getSaturationLevel());
        }
        float health = showHealth ? Math.max(0F, FoodHelper.getEstimatedHealthIncrement(player, food)) : 0F;

        if (hunger <= 0 && saturation <= 0F && health <= 0F) return null;
        return new FoodPreview(hunger, saturation, health);
    }

    public static float exhaustionFraction(Player player) {
        ModConfig cfg = ModConfig.INSTANCE;
        if (cfg != null && !cfg.showFoodExhaustionHudUnderlay) return -1F;
        float max = FoodHelper.MAX_EXHAUSTION;
        if (max <= 0F) return -1F;
        // AppleSkin syncs exhaustion to the client, so this is accurate while it is installed.
        return Mth.clamp(player.getFoodData().getExhaustionLevel() / max, 0F, 1F);
    }

    @Nullable
    private static FoodProperties heldFood(Player player, boolean allowOffhand) {
        int tick = Minecraft.getInstance().gui.getGuiTicks();
        if (tick != cachedGuiTick) {
            cachedGuiTick = tick;
            FoodProperties main = consumable(player.getMainHandItem(), player);
            cachedFood = main != null ? main : allowOffhand ? consumable(player.getOffhandItem(), player) : null;
        }
        return cachedFood;
    }

    @Nullable
    private static FoodProperties consumable(ItemStack stack, Player player) {
        if (stack.isEmpty() || !FoodHelper.isFood(stack)) return null;
        FoodHelper.QueriedFoodResult result = FoodHelper.query(stack, player);
        if (result == null || result.modifiedFoodComponent == null) return null;
        return FoodHelper.canConsume(player, result.modifiedFoodComponent) ? result.modifiedFoodComponent : null;
    }
}
