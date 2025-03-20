package net.bandit.better_hp.config;

import net.bandit.better_hp.BetterhpMod;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

@EventBusSubscriber(modid = BetterhpMod.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class BetterHPConfig {

    // Configuration builder for client settings
    private static final ModConfigSpec.Builder CLIENT_BUILDER = new ModConfigSpec.Builder();

    // Configuration options
    public static final ModConfigSpec.BooleanValue enableCustomHUD = CLIENT_BUILDER
            .comment("Enable or disable the custom Better HP HUD")
            .define("enableCustomHUD", true);

    public static final ModConfigSpec.BooleanValue showVanillaHearts = CLIENT_BUILDER
            .comment("Show vanilla hearts")
            .define("showVanillaHearts", false);

    public static final ModConfigSpec.BooleanValue enableDynamicHealthColor = CLIENT_BUILDER
            .comment("Enable dynamic health color change (Green -> Full, Yellow -> Low, Red -> Critical)")
            .define("enableDynamicHealthColor", true);

    public static final ModConfigSpec.BooleanValue showVanillaArmor = CLIENT_BUILDER
            .comment("Show vanilla armor bar")
            .define("showVanillaArmor", false);

    public static final ModConfigSpec.BooleanValue showVanillaHunger = CLIENT_BUILDER
            .comment("Show vanilla hunger bar")
            .define("showVanillaHunger", false);

    public static final ModConfigSpec.BooleanValue showVanillaOxygen = CLIENT_BUILDER
            .comment("Show vanilla oxygen bubbles")
            .define("showVanillaOxygen", false);

    public static final ModConfigSpec.BooleanValue showNumericHunger = CLIENT_BUILDER
            .comment("Show numeric hunger instead of vanilla hunger bar")
            .define("showNumericHunger", true);

    public static final ModConfigSpec.BooleanValue showNumericHealth = CLIENT_BUILDER
            .comment("Show numeric health instead of vanilla health bar")
            .define("showNumericHealth", true);

    public static final ModConfigSpec.BooleanValue showOxygenIcon = CLIENT_BUILDER
            .comment("Show custom oxygen icon when underwater")
            .define("showOxygenIcon", true);

    public static final ModConfigSpec.BooleanValue showNumericOxygen = CLIENT_BUILDER
            .comment("Show numeric oxygen value when underwater")
            .define("showNumericOxygen", true);

    public static final ModConfigSpec.BooleanValue showHealthIcon = CLIENT_BUILDER
            .comment("Show custom health icon")
            .define("showHealthIcon", true);

    public static final ModConfigSpec.BooleanValue showArmorIcon = CLIENT_BUILDER
            .comment("Show custom armor icon")
            .define("showArmorIcon", true);

    public static final ModConfigSpec.BooleanValue showHungerIcon = CLIENT_BUILDER
            .comment("Show custom hunger icon")
            .define("showHungerIcon", true);

    public static final ModConfigSpec.IntValue healthDisplayX = CLIENT_BUILDER
            .comment("Horizontal position of the health icon display")
            .defineInRange("healthDisplayX", -70, -1000, 1000);

    public static final ModConfigSpec.IntValue healthDisplayY = CLIENT_BUILDER
            .comment("Vertical position of the health icon display")
            .defineInRange("healthDisplayY", 43, 0, 1000);

    public static final ModConfigSpec.IntValue armorDisplayX = CLIENT_BUILDER
            .comment("Horizontal position of the armor icon display")
            .defineInRange("armorDisplayX", -5, -1000, 1000);

    public static final ModConfigSpec.IntValue armorDisplayY = CLIENT_BUILDER
            .comment("Vertical position of the armor icon display")
            .defineInRange("armorDisplayY", 50, 0, 1000);

    public static final ModConfigSpec.BooleanValue showToughnessIcon = CLIENT_BUILDER
            .comment("Show custom toughness icon")
            .define("showToughnessIcon", true);

    public static final ModConfigSpec.IntValue toughnessDisplayX = CLIENT_BUILDER
            .comment("Horizontal position of the toughness icon display")
            .defineInRange("toughnessDisplayX", 35, -1000, 1000);

    public static final ModConfigSpec.IntValue toughnessDisplayY = CLIENT_BUILDER
            .comment("Vertical position of the toughness icon display")
            .defineInRange("toughnessDisplayY", 50, 0, 1000);


    public static final ModConfigSpec.IntValue hungerDisplayX = CLIENT_BUILDER
            .comment("Horizontal position of the hunger icon display")
            .defineInRange("hungerDisplayX", 45, -1000, 1000);

    public static final ModConfigSpec.IntValue hungerDisplayY = CLIENT_BUILDER
            .comment("Vertical position of the hunger icon display")
            .defineInRange("hungerDisplayY", 43, 0, 1000);

    public static final ModConfigSpec.IntValue oxygenDisplayX = CLIENT_BUILDER
            .comment("Horizontal position of the oxygen icon display")
            .defineInRange("oxygenDisplayX", 67, -1000, 1000);

    public static final ModConfigSpec.IntValue oxygenDisplayY = CLIENT_BUILDER
            .comment("Vertical position of the oxygen icon display")
            .defineInRange("oxygenDisplayY", 60, 0, 1000);

    public static final ModConfigSpec.IntValue healthColor = CLIENT_BUILDER
            .comment("Color of the health numerical value in the HUD. Uses Decimal color code; Default is Red")
            .defineInRange("healthColor", 0xFF5555, 0x000000, 0xFFFFFF);

    // Build the client configuration spec
    public static final ModConfigSpec CLIENT_SPEC = CLIENT_BUILDER.build();

    // Configuration variables (these will cache the values from the config)
    public static boolean customHUDEnabled;
    public static boolean heartsEnabled;
    public static boolean dynamicHealthColor;
    public static int healthX, healthY;
    public static int armorX, armorY;
    public static boolean toughnessIconEnabled;
    public static int toughnessX, toughnessY;


    // Event to load the config values
    @SubscribeEvent
    public static void onConfigLoad(ModConfigEvent event) {
        if (event.getConfig().getSpec() == CLIENT_SPEC) {
            loadCachedConfigValues();
            BetterhpMod.getLogger().info("BetterHP config reloaded.");
        }
    }

    // Cache config values for quick access
    public static void loadCachedConfigValues() {
        customHUDEnabled = enableCustomHUD.get();
        heartsEnabled = showVanillaHearts.get();
        dynamicHealthColor = enableDynamicHealthColor.get();
        healthX = healthDisplayX.get();
        healthY = healthDisplayY.get();
        armorX = armorDisplayX.get();
        armorY = armorDisplayY.get();
        toughnessIconEnabled = showToughnessIcon.get();
        toughnessX = toughnessDisplayX.get();
        toughnessY = toughnessDisplayY.get();
    }

    // Method to reload config dynamically
    public static void reloadConfig() {
        loadCachedConfigValues();
        BetterhpMod.getLogger().info("Config reloaded dynamically.");
    }
}
