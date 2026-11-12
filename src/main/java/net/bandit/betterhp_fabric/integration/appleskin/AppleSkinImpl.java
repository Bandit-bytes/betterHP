package net.bandit.betterhp_fabric.integration.appleskin;

import net.bandit.betterhp_fabric.integration.AppleSkinCompat.FoodPreview;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;
import squeek.appleskin.ModConfig;
import squeek.appleskin.api.food.FoodValues;
import squeek.appleskin.helpers.FoodHelper;

/** Only class-loaded when AppleSkin is installed. */
public final class AppleSkinImpl {
    private static final int MAX_FOOD = 20;

    private record Held(ItemStack stack, FoodValues values) {}

    private static int cachedTick = Integer.MIN_VALUE;
    @Nullable private static Held cached;

    private AppleSkinImpl() {}

    @Nullable
    public static FoodPreview foodPreview(PlayerEntity player, boolean wantFood, boolean wantHealth) {
        ModConfig cfg = ModConfig.INSTANCE;
        boolean showFood = wantFood && (cfg == null || cfg.showFoodValuesHudOverlay);
        boolean showHealth = wantHealth && (cfg == null || cfg.showFoodHealthHudOverlay);
        if (!showFood && !showHealth) return null;

        Held held = heldFood(player, cfg == null || cfg.showFoodValuesHudOverlayWhenOffhand);
        if (held == null) return null;

        int hunger = 0;
        float saturation = 0F;
        if (showFood) {
            HungerManager data = player.getHungerManager();
            int newFood = MathHelper.clamp(data.getFoodLevel() + held.values().hunger, 0, MAX_FOOD);
            float newSaturation = MathHelper.clamp(data.getSaturationLevel() + held.values().getSaturationIncrement(), 0F, newFood);
            hunger = held.values().hunger;
            saturation = Math.max(0F, newSaturation - data.getSaturationLevel());
        }
        float health = showHealth ? Math.max(0F, FoodHelper.getEstimatedHealthIncrement(held.stack(), held.values(), player)) : 0F;

        if (hunger <= 0 && saturation <= 0F && health <= 0F) return null;
        return new FoodPreview(hunger, saturation, health);
    }

    public static float exhaustionFraction(PlayerEntity player) {
        ModConfig cfg = ModConfig.INSTANCE;
        if (cfg != null && !cfg.showFoodExhaustionHudUnderlay) return -1F;
        float max = FoodHelper.MAX_EXHAUSTION;
        if (max <= 0F) return -1F;
        // AppleSkin syncs exhaustion to the client, so this is accurate while it is installed.
        return MathHelper.clamp(player.getHungerManager().getExhaustion() / max, 0F, 1F);
    }

    @Nullable
    private static Held heldFood(PlayerEntity player, boolean allowOffhand) {
        int tick = MinecraftClient.getInstance().inGameHud.getTicks();
        if (tick != cachedTick) {
            cachedTick = tick;
            Held main = consumable(player.getMainHandStack(), player);
            cached = main != null ? main : allowOffhand ? consumable(player.getOffHandStack(), player) : null;
        }
        return cached;
    }

    @Nullable
    private static Held consumable(ItemStack stack, PlayerEntity player) {
        if (stack.isEmpty() || !FoodHelper.isFood(stack) || !FoodHelper.canConsume(stack, player)) return null;
        FoodValues values = FoodHelper.getModifiedFoodValues(stack, player);
        return values == null ? null : new Held(stack, values);
    }
}
