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
            .defineInRange("armorDisplayY", 56, 0, 1000);

    public static final ModConfigSpec.BooleanValue showToughnessIcon = CLIENT_BUILDER
            .comment("Show custom toughness icon")
            .define("showToughnessIcon", true);

    public static final ModConfigSpec.IntValue toughnessDisplayX = CLIENT_BUILDER
            .comment("Horizontal position of the toughness icon display")
            .defineInRange("toughnessDisplayX", 35, -1000, 1000);

    public static final ModConfigSpec.IntValue toughnessDisplayY = CLIENT_BUILDER
            .comment("Vertical position of the toughness icon display")
            .defineInRange("toughnessDisplayY", 56, 0, 1000);


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

    public static final ModConfigSpec.BooleanValue showVanillaMountHealth = CLIENT_BUILDER
            .comment("Show vanilla mount hearts (when riding an entity like a horse)")
            .define("showVanillaMountHealth", false);

    public static final ModConfigSpec.BooleanValue showMountIcon = CLIENT_BUILDER
            .comment("Show custom mount health icon and value on HUD when riding")
            .define("showMountIcon", true);


    public static final ModConfigSpec.IntValue mountDisplayX = CLIENT_BUILDER
            .comment("Horizontal position of the mount health icon display")
            .defineInRange("mountDisplayX", -70, -1000, 1000);

    public static final ModConfigSpec.IntValue mountDisplayY = CLIENT_BUILDER
            .comment("Vertical position of the mount health icon display")
            .defineInRange("mountDisplayY", 63, 0, 1000);

    public static final ModConfigSpec.BooleanValue showAbsorptionText = CLIENT_BUILDER
            .comment("Show +Absorption numeric text")
            .define("showAbsorptionText", true);

    public static final ModConfigSpec.BooleanValue absorptionFollowHealthWidth = CLIENT_BUILDER
            .comment("Place absorption right after the rendered health text so it never overlaps")
            .define("absorptionFollowHealthWidth", true);

    public static final ModConfigSpec.IntValue absorptionOffsetX = CLIENT_BUILDER
            .comment("Absorption X offset RELATIVE to the health anchor (used if absorptionFollowHealthWidth=false)")
            .defineInRange("absorptionOffsetX", -18, -1000, 1000);

    public static final ModConfigSpec.IntValue absorptionOffsetY = CLIENT_BUILDER
            .comment("Absorption Y offset RELATIVE to the health anchor (positive moves down)")
            .defineInRange("absorptionOffsetY", 0, -1000, 1000);


    public static final ModConfigSpec.BooleanValue showSaturationText = CLIENT_BUILDER
            .comment("Show +Saturation numeric text")
            .define("showSaturationText", true);

    public static final ModConfigSpec.BooleanValue hideSaturationIfVanillaHunger = CLIENT_BUILDER
            .comment("Hide saturation number when vanilla hunger bar is enabled")
            .define("hideSaturationIfVanillaHunger", true);

    public static final ModConfigSpec.BooleanValue showMana = CLIENT_BUILDER
            .comment("Show Iron's Spells & Spellbooks mana value (only when Iron's is installed)")
            .define("showMana", true);

    public static final ModConfigSpec.BooleanValue showNumericMana = CLIENT_BUILDER
            .comment("Show numeric mana value")
            .define("showNumericMana", true);

    public static final ModConfigSpec.BooleanValue showManaIcon = CLIENT_BUILDER
            .comment("Show mana icon")
            .define("showManaIcon", true);

    public static final ModConfigSpec.BooleanValue manaFollowHealthWidth = CLIENT_BUILDER
            .comment("Place mana right after the rendered health text so it never overlaps")
            .define("manaFollowHealthWidth", true);

    public static final ModConfigSpec.IntValue manaOffsetX = CLIENT_BUILDER
            .comment("Mana X offset relative to the health anchor (used if manaFollowHealthWidth=false)")
            .defineInRange("manaOffsetX", 122, -1000, 1000);

    public static final ModConfigSpec.IntValue manaOffsetY = CLIENT_BUILDER
            .comment("Mana Y offset relative to the health anchor (positive moves up in your coordinate system if you add to healthY)")
            .defineInRange("manaOffsetY", 0, -1000, 1000);

    public static final ModConfigSpec.BooleanValue showIronsMana = CLIENT_BUILDER
            .comment("Show Iron's Spells & Spellbooks mana in the BetterHP HUD (requires irons_spellbooks)")
            .define("showIronsMana", true);

    public static final ModConfigSpec.BooleanValue disableIronsManaBar = CLIENT_BUILDER
            .comment("If true, BetterHP will attempt to disable Iron's own mana bar so only BetterHP renders it.")
            .define("disableIronsManaBar", true);

    public static final ModConfigSpec.BooleanValue enableIronsManaCompat = CLIENT_BUILDER
            .comment("If Iron's Spells & Spellbooks is installed, render BetterHP's mana display.")
            .define("enableIronsManaCompat", true);

    public static final ModConfigSpec.BooleanValue hideIronsManaOverlay = CLIENT_BUILDER
            .comment("If enabled, BetterHP will hide Iron's default mana bar when compat is active.")
            .define("hideIronsManaOverlay", true);


    public static final ModConfigSpec CLIENT_SPEC = CLIENT_BUILDER.build();

    public static boolean customHUDEnabled;
    public static boolean heartsEnabled;
    public static boolean dynamicHealthColor;
    public static int healthX, healthY;
    public static int armorX, armorY;
    public static boolean toughnessIconEnabled;
    public static int toughnessX, toughnessY;
    public static boolean mountIconEnabled;
    public static boolean vanillaMountHeartsEnabled;
    public static boolean manaEnabled;
    public static int mountX, mountY;


    @SubscribeEvent
    public static void onConfigLoad(ModConfigEvent event) {
        if (event.getConfig().getSpec() == CLIENT_SPEC) {
            loadCachedConfigValues();
            BetterhpMod.getLogger().info("BetterHP config reloaded.");
        }
    }

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
        mountIconEnabled = showMountIcon.get();
        vanillaMountHeartsEnabled = showVanillaMountHealth.get();
        mountX = mountDisplayX.get();
        mountY = mountDisplayY.get();
        manaEnabled = showMana.get();


    }

    public static void reloadConfig() {
        loadCachedConfigValues();
        BetterhpMod.getLogger().info("Config reloaded dynamically.");
    }
}
