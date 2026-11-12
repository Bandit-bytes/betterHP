package net.bandit.better_hp.config;

import net.neoforged.neoforge.common.ModConfigSpec;

/**
 * Client config for Better HP.
 *
 * <p>Values are read straight from the spec ({@code .get()} is cached by NeoForge and
 * invalidated on reload), so there is no hand-maintained cache that can go stale.
 *
 * <p>The original (pre-2.3) keys live at the top level with unchanged names so existing
 * {@code better_hp-client.toml} files keep their values. Compat options live in their
 * own sections.
 */
public final class BetterHPConfig {

    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    // ------------------------------------------------------------------ general

    public static final ModConfigSpec.BooleanValue enableCustomHUD = BUILDER
            .comment("Enable or disable the custom Better HP HUD. When false, the vanilla HUD is fully restored.")
            .define("enableCustomHUD", true);

    public static final ModConfigSpec.DoubleValue hudIconScale = BUILDER
            .comment("Scale of BetterHP HUD icons. 1.0 = normal size")
            .defineInRange("hudIconScale", 1.0, 0.5, 4.0);

    public static final ModConfigSpec.DoubleValue hudTextScale = BUILDER
            .comment("Scale of BetterHP HUD text. 1.0 = normal size")
            .defineInRange("hudTextScale", 1.0, 0.5, 4.0);

    // ------------------------------------------------------------------ vanilla bars

    public static final ModConfigSpec.BooleanValue showVanillaHearts = BUILDER
            .comment("Show vanilla hearts")
            .define("showVanillaHearts", false);

    public static final ModConfigSpec.BooleanValue showVanillaArmor = BUILDER
            .comment("Show vanilla armor bar")
            .define("showVanillaArmor", false);

    public static final ModConfigSpec.BooleanValue showVanillaHunger = BUILDER
            .comment("Show vanilla hunger bar")
            .define("showVanillaHunger", false);

    public static final ModConfigSpec.BooleanValue showVanillaOxygen = BUILDER
            .comment("Show vanilla oxygen bubbles")
            .define("showVanillaOxygen", false);

    public static final ModConfigSpec.BooleanValue showVanillaMountHealth = BUILDER
            .comment("Show vanilla mount hearts (when riding an entity like a horse)")
            .define("showVanillaMountHealth", false);

    // ------------------------------------------------------------------ health

    public static final ModConfigSpec.BooleanValue showHealthIcon = BUILDER
            .comment("Show custom health icon")
            .define("showHealthIcon", true);

    public static final ModConfigSpec.BooleanValue showNumericHealth = BUILDER
            .comment("Show numeric health instead of vanilla health bar")
            .define("showNumericHealth", true);

    public static final ModConfigSpec.BooleanValue enableDynamicHealthColor = BUILDER
            .comment("Enable dynamic health color change (Green -> Full, Yellow -> Low, Red -> Critical)")
            .define("enableDynamicHealthColor", true);

    public static final ModConfigSpec.IntValue healthColor = BUILDER
            .comment("Color of the health numerical value in the HUD. Uses Decimal color code; Default is Red")
            .defineInRange("healthColor", 0xFF5555, 0x000000, 0xFFFFFF);

    public static final ModConfigSpec.BooleanValue showStatusHealthIcons = BUILDER
            .comment("Swap the health icon and text colour while Poisoned, Withered or Frozen (like vanilla's heart types).",
                    "Frozen covers powder snow.")
            .define("showStatusHealthIcons", true);

    public static final ModConfigSpec.IntValue healthDisplayX = BUILDER
            .comment("Horizontal position of the health icon display")
            .defineInRange("healthDisplayX", -70, -1000, 1000);

    public static final ModConfigSpec.IntValue healthDisplayY = BUILDER
            .comment("Vertical position of the health icon display")
            .defineInRange("healthDisplayY", 43, 0, 1000);

    public static final ModConfigSpec.BooleanValue showAbsorptionText = BUILDER
            .comment("Show +Absorption numeric text")
            .define("showAbsorptionText", true);

    public static final ModConfigSpec.BooleanValue absorptionFollowHealthWidth = BUILDER
            .comment("Place absorption right after the rendered health text so it never overlaps")
            .define("absorptionFollowHealthWidth", true);

    public static final ModConfigSpec.IntValue absorptionOffsetX = BUILDER
            .comment("Absorption X offset RELATIVE to the health anchor (used if absorptionFollowHealthWidth=false)")
            .defineInRange("absorptionOffsetX", -18, -1000, 1000);

    public static final ModConfigSpec.IntValue absorptionOffsetY = BUILDER
            .comment("Absorption Y offset RELATIVE to the health anchor (positive moves down)")
            .defineInRange("absorptionOffsetY", 0, -1000, 1000);

    // ------------------------------------------------------------------ armor / toughness

    public static final ModConfigSpec.BooleanValue showArmorIcon = BUILDER
            .comment("Show custom armor icon")
            .define("showArmorIcon", true);

    public static final ModConfigSpec.IntValue armorDisplayX = BUILDER
            .comment("Horizontal position of the armor icon display")
            .defineInRange("armorDisplayX", -5, -1000, 1000);

    public static final ModConfigSpec.IntValue armorDisplayY = BUILDER
            .comment("Vertical position of the armor icon display")
            .defineInRange("armorDisplayY", 56, 0, 1000);

    public static final ModConfigSpec.BooleanValue showToughnessIcon = BUILDER
            .comment("Show custom toughness icon")
            .define("showToughnessIcon", true);

    public static final ModConfigSpec.IntValue toughnessDisplayX = BUILDER
            .comment("Horizontal position of the toughness icon display")
            .defineInRange("toughnessDisplayX", 35, -1000, 1000);

    public static final ModConfigSpec.IntValue toughnessDisplayY = BUILDER
            .comment("Vertical position of the toughness icon display")
            .defineInRange("toughnessDisplayY", 56, 0, 1000);

    // ------------------------------------------------------------------ hunger

    public static final ModConfigSpec.BooleanValue showHungerIcon = BUILDER
            .comment("Show custom hunger icon")
            .define("showHungerIcon", true);

    public static final ModConfigSpec.BooleanValue showNumericHunger = BUILDER
            .comment("Show numeric hunger instead of vanilla hunger bar")
            .define("showNumericHunger", true);

    public static final ModConfigSpec.IntValue hungerDisplayX = BUILDER
            .comment("Horizontal position of the hunger icon display")
            .defineInRange("hungerDisplayX", 45, -1000, 1000);

    public static final ModConfigSpec.IntValue hungerDisplayY = BUILDER
            .comment("Vertical position of the hunger icon display")
            .defineInRange("hungerDisplayY", 43, 0, 1000);

    public static final ModConfigSpec.BooleanValue showSaturationText = BUILDER
            .comment("Show +Saturation numeric text")
            .define("showSaturationText", true);

    public static final ModConfigSpec.BooleanValue hideSaturationIfVanillaHunger = BUILDER
            .comment("Hide saturation number when vanilla hunger bar is enabled")
            .define("hideSaturationIfVanillaHunger", true);

    // ------------------------------------------------------------------ oxygen

    public static final ModConfigSpec.BooleanValue showOxygenIcon = BUILDER
            .comment("Show custom oxygen icon when underwater")
            .define("showOxygenIcon", true);

    public static final ModConfigSpec.BooleanValue showNumericOxygen = BUILDER
            .comment("Show numeric oxygen value when underwater")
            .define("showNumericOxygen", true);

    public static final ModConfigSpec.IntValue oxygenDisplayX = BUILDER
            .comment("Horizontal position of the oxygen icon display")
            .defineInRange("oxygenDisplayX", 67, -1000, 1000);

    public static final ModConfigSpec.IntValue oxygenDisplayY = BUILDER
            .comment("Vertical position of the oxygen icon display")
            .defineInRange("oxygenDisplayY", 60, 0, 1000);

    // ------------------------------------------------------------------ mount

    public static final ModConfigSpec.BooleanValue showMountIcon = BUILDER
            .comment("Show custom mount health icon and value on HUD when riding")
            .define("showMountIcon", true);

    public static final ModConfigSpec.IntValue mountDisplayX = BUILDER
            .comment("Horizontal position of the mount health icon display")
            .defineInRange("mountDisplayX", -70, -1000, 1000);

    public static final ModConfigSpec.IntValue mountDisplayY = BUILDER
            .comment("Vertical position of the mount health icon display")
            .defineInRange("mountDisplayY", 63, 0, 1000);

    // ------------------------------------------------------------------ held item name

    public static final ModConfigSpec.IntValue selectedItemNameOffsetY = BUILDER
            .comment("Moves the vanilla held-item name text (e.g., 'Splash Potion of Healing'). Negative = higher, Positive = lower.")
            .defineInRange("selectedItemNameOffsetY", -10, -40, 40);

    public static final ModConfigSpec.BooleanValue moveSelectedItemNameOnlyWhenArmor = BUILDER
            .comment("Only apply the selected item name offset when the player has armor > 0.")
            .define("moveSelectedItemNameOnlyWhenArmor", true);

    // ------------------------------------------------------------------ AppleSkin

    public static final ModConfigSpec.BooleanValue appleSkinCompat;
    public static final ModConfigSpec.BooleanValue appleSkinHideOverlays;
    public static final ModConfigSpec.BooleanValue appleSkinFoodPreview;
    public static final ModConfigSpec.BooleanValue appleSkinHealthPreview;
    public static final ModConfigSpec.BooleanValue appleSkinExhaustionBar;

    static {
        BUILDER.comment("AppleSkin compat (only active when AppleSkin is installed).").push("appleskin");
        appleSkinCompat = BUILDER
                .comment("Master switch for AppleSkin compat.")
                .define("enabled", true);
        appleSkinHideOverlays = BUILDER
                .comment("Hide AppleSkin's bar overlays while the matching vanilla bar is hidden by BetterHP,",
                        "so they don't float over empty space. BetterHP shows the same info as text instead.")
                .define("hideAppleSkinBarOverlays", true);
        appleSkinFoodPreview = BUILDER
                .comment("While holding food, show how much hunger (+N) and saturation it restores next to the hunger value.",
                        "Follows AppleSkin's own 'showFoodValuesHudOverlay' / 'showFoodValuesHudOverlayWhenOffhand' settings.")
                .define("showFoodPreview", true);
        appleSkinHealthPreview = BUILDER
                .comment("While holding food, show the estimated health it will regenerate next to the health value.",
                        "Follows AppleSkin's own 'showFoodHealthHudOverlay' setting.")
                .define("showHealthPreview", true);
        appleSkinExhaustionBar = BUILDER
                .comment("Draw a thin exhaustion bar under the hunger value.",
                        "Follows AppleSkin's own 'showFoodExhaustionHudUnderlay' setting.")
                .define("showExhaustionBar", true);
        BUILDER.pop();
    }

    public static final ModConfigSpec CLIENT_SPEC = BUILDER.build();

    private BetterHPConfig() {}

    /** True once the client config file has been read. Rendering before this would throw. */
    public static boolean isLoaded() {
        return CLIENT_SPEC.isLoaded();
    }

    /** Better HP is drawing its HUD (config loaded and the master switch on). */
    public static boolean hudActive() {
        return isLoaded() && enableCustomHUD.get();
    }
}
