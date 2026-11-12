package net.bandit.better_hp.config;

import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class BetterHPConfig {
    public static final ForgeConfigSpec CLIENT_SPEC;
    public static final Client CLIENT;

    static {
        Pair<Client, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Client::new);
        CLIENT_SPEC = specPair.getRight();
        CLIENT = specPair.getLeft();
    }

    /** True once the client config file has been read. Rendering before this would throw. */
    public static boolean isLoaded() {
        return CLIENT_SPEC.isLoaded();
    }

    public static class Client {
        public final ForgeConfigSpec.BooleanValue showVanillaHearts;
        public final ForgeConfigSpec.BooleanValue enableDynamicHealthColor;
        public final ForgeConfigSpec.BooleanValue showVanillaArmor;
        public final ForgeConfigSpec.BooleanValue showVanillaHunger;
        public final ForgeConfigSpec.BooleanValue showVanillaOxygen;
        public final ForgeConfigSpec.BooleanValue showNumericHunger;
        public final ForgeConfigSpec.BooleanValue showNumericHealth;
        public final ForgeConfigSpec.BooleanValue showOxygenIcon;
        public final ForgeConfigSpec.BooleanValue showNumericOxygen;
        public final ForgeConfigSpec.BooleanValue showHealthIcon;
        public final ForgeConfigSpec.BooleanValue showArmorIcon;
        public final ForgeConfigSpec.BooleanValue showHungerIcon;
        public final ForgeConfigSpec.IntValue healthTextOffset;
        public final ForgeConfigSpec.IntValue healthDisplayX;
        public final ForgeConfigSpec.IntValue healthDisplayY;
        public final ForgeConfigSpec.IntValue armorDisplayX;
        public final ForgeConfigSpec.IntValue armorDisplayY;
        public final ForgeConfigSpec.IntValue hungerDisplayX;
        public final ForgeConfigSpec.IntValue hungerDisplayY;
        public final ForgeConfigSpec.IntValue oxygenDisplayX;
        public final ForgeConfigSpec.IntValue oxygenDisplayY;
        public final ForgeConfigSpec.IntValue healthColor;
        public final ForgeConfigSpec.BooleanValue showDecimalHealth;
        public final ForgeConfigSpec.BooleanValue showHealthOutline;
        public final ForgeConfigSpec.BooleanValue showVanillaMountHealth;
        public final ForgeConfigSpec.BooleanValue showMountIcon;
        public final ForgeConfigSpec.IntValue mountDisplayX;
        public final ForgeConfigSpec.IntValue mountDisplayY;
        public final ForgeConfigSpec.BooleanValue showToughnessIcon;
        public final ForgeConfigSpec.IntValue toughnessDisplayX;
        public final ForgeConfigSpec.IntValue toughnessDisplayY;
        public final ForgeConfigSpec.BooleanValue showAbsorptionText;
        public final ForgeConfigSpec.BooleanValue absorptionFollowHealthWidth;
        public final ForgeConfigSpec.IntValue absorptionOffsetX;
        public final ForgeConfigSpec.IntValue absorptionOffsetY;
        public final ForgeConfigSpec.BooleanValue showSaturationText;
        public final ForgeConfigSpec.BooleanValue hideSaturationIfVanillaHunger;
        public final ForgeConfigSpec.BooleanValue enableIronsManaCompat;
        public final ForgeConfigSpec.BooleanValue hideIronsManaOverlay;

        public final ForgeConfigSpec.BooleanValue showMana;
        public final ForgeConfigSpec.BooleanValue showNumericMana;
        public final ForgeConfigSpec.BooleanValue showManaIcon;

        public final ForgeConfigSpec.BooleanValue manaFollowHealthWidth;
        public final ForgeConfigSpec.IntValue manaOffsetX;
        public final ForgeConfigSpec.IntValue manaOffsetY;

        // 8.2: status icons + mod compat
        public final ForgeConfigSpec.BooleanValue showStatusHealthIcons;
        public final ForgeConfigSpec.BooleanValue appleSkinCompat;
        public final ForgeConfigSpec.BooleanValue appleSkinFoodPreview;
        public final ForgeConfigSpec.BooleanValue appleSkinHealthPreview;
        public final ForgeConfigSpec.BooleanValue appleSkinExhaustionBar;
        public final ForgeConfigSpec.BooleanValue eidolonCompat;
        public final ForgeConfigSpec.BooleanValue eidolonChilledFrozenHearts;
        public final ForgeConfigSpec.BooleanValue eidolonShowEtherealHealth;
        public final ForgeConfigSpec.BooleanValue eidolonHideHeartsOverlay;
        public final ForgeConfigSpec.BooleanValue lsoCompat;
        public final ForgeConfigSpec.BooleanValue lsoReplaceThirstBar;
        public final ForgeConfigSpec.BooleanValue lsoShowThirstSaturation;
        public final ForgeConfigSpec.IntValue lsoThirstX;
        public final ForgeConfigSpec.IntValue lsoThirstY;
        public final ForgeConfigSpec.BooleanValue lsoReplaceTemperature;
        public final ForgeConfigSpec.IntValue lsoTemperatureX;
        public final ForgeConfigSpec.IntValue lsoTemperatureY;
        public final ForgeConfigSpec.BooleanValue lsoColdHungerFrozen;
        public final ForgeConfigSpec.BooleanValue lsoShowShieldHealth;
        public final ForgeConfigSpec.BooleanValue lsoShowBrokenHearts;


        public Client(ForgeConfigSpec.Builder builder) {
            builder.push("Display Settings");

            showVanillaHearts = builder
                    .comment("Show vanilla hearts")
                    .define("showVanillaHearts", false);
            showDecimalHealth = builder
                    .comment("Show health as decimals or whole numbers")
                    .define("showDecimalHealth", true);

            showVanillaArmor = builder
                    .comment("Show vanilla armor bar")
                    .define("showVanillaArmor", false);

            showVanillaHunger = builder
                    .comment("Show vanilla hunger bar")
                    .define("showVanillaHunger", false);

            showVanillaOxygen = builder
                    .comment("Show vanilla oxygen bubbles")
                    .define("showVanillaOxygen", false);

            showNumericHunger = builder
                    .comment("Show numeric hunger instead of vanilla hunger bar")
                    .define("showNumericHunger", true);

            showOxygenIcon = builder
                    .comment("Show custom oxygen icon when underwater")
                    .define("showOxygenIcon", true);

            showNumericHealth = builder
                    .comment("Show numeric health instead of vanilla health bar")
                    .define("showNumericHealth", true);

            healthTextOffset = builder
                    .comment("Horizontal offset between the health icon and the numeric health text")
                    .defineInRange("healthTextOffset", 25, 0, 100);

            showNumericOxygen = builder
                    .comment("Show numeric oxygen value when underwater")
                    .define("showNumericOxygen", true);

            showHealthIcon = builder
                    .comment("Show custom health icon")
                    .define("showHealthIcon", true);

            showArmorIcon = builder
                    .comment("Show custom armor icon")
                    .define("showArmorIcon", true);

            showHungerIcon = builder
                    .comment("Show custom hunger icon")
                    .define("showHungerIcon", true);

            enableDynamicHealthColor = builder
                    .comment("Enable dynamic health color change (Green -> Full, Yellow -> Low, Red -> Critical)")
                    .define("enableDynamicHealthColor", true);

            healthDisplayX = builder
                    .comment("Horizontal position of the health icon display")
                    .defineInRange("healthDisplayX", -85, -1000, 1000);

            healthDisplayY = builder
                    .comment("Vertical position of the health icon display")
                    .defineInRange("healthDisplayY", 43, 0, 1000);

            armorDisplayX = builder
                    .comment("Horizontal position of the armor icon display")
                    .defineInRange("armorDisplayX", -85, -1000, 1000);

            armorDisplayY = builder
                    .comment("Vertical position of the armor icon display")
                    .defineInRange("armorDisplayY", 60, 0, 1000);

            hungerDisplayX = builder
                    .comment("Horizontal position of the hunger icon display")
                    .defineInRange("hungerDisplayX", 70, -1000, 1000);

            hungerDisplayY = builder
                    .comment("Vertical position of the hunger icon display")
                    .defineInRange("hungerDisplayY", 43, 0, 1000);

            oxygenDisplayX = builder
                    .comment("Horizontal position of the oxygen icon display")
                    .defineInRange("oxygenDisplayX", 70, -1000, 1000);

            oxygenDisplayY = builder
                    .comment("Vertical position of the oxygen icon display")
                    .defineInRange("oxygenDisplayY", 60, 0, 1000);

            healthColor = builder
                    .comment("Color of the health numerical value in the HUD. Use's Decimal color code; Default is Red")
                    .defineInRange("healthColor", 0xFF5555, 0x000000, 0xFFFFFF);

            builder.comment("Enable or disable black outline around health numerical values.");
            showHealthOutline = builder.define("showHealthOutline", false);

            showToughnessIcon = builder
                    .comment("Show custom toughness icon")
                    .define("showToughnessIcon", true);

            toughnessDisplayX = builder
                    .comment("Horizontal position of the toughness icon display")
                    .defineInRange("toughnessDisplayX", 35, -1000, 1000);

            toughnessDisplayY = builder
                    .comment("Vertical position of the toughness icon display")
                    .defineInRange("toughnessDisplayY", 56, 0, 1000);

            showVanillaMountHealth = builder
                    .comment("Show vanilla mount hearts (when riding an entity like a horse)")
                    .define("showVanillaMountHealth", false);

            showMountIcon = builder
                    .comment("Show custom mount health icon and value on HUD when riding")
                    .define("showMountIcon", true);

            mountDisplayX = builder
                    .comment("Horizontal position of the mount health icon display")
                    .defineInRange("mountDisplayX", -140, -1000, 1000);

            mountDisplayY = builder
                    .comment("Vertical position of the mount health icon display")
                    .defineInRange("mountDisplayY", 43, 0, 1000);

            showAbsorptionText = builder
                    .comment("Show +Absorption numeric text")
                    .define("showAbsorptionText", true);

            absorptionFollowHealthWidth = builder
                    .comment("Place absorption right after the rendered health text so it never overlaps")
                    .define("absorptionFollowHealthWidth", true);

            absorptionOffsetX = builder
                    .comment("Absorption X offset RELATIVE to the health anchor (used if absorptionFollowHealthWidth=false)")
                    .defineInRange("absorptionOffsetX", -18, -1000, 1000);

            absorptionOffsetY = builder
                    .comment("Absorption Y offset RELATIVE to the health anchor")
                    .defineInRange("absorptionOffsetY", 0, -1000, 1000);

            showSaturationText = builder
                    .comment("Show +Saturation numeric text")
                    .define("showSaturationText", true);

            hideSaturationIfVanillaHunger = builder
                    .comment("Hide saturation number when vanilla hunger bar is enabled")
                    .define("hideSaturationIfVanillaHunger", true);

            enableIronsManaCompat = builder
                    .comment("If Iron's Spells & Spellbooks is installed, render BetterHP's mana display.")
                    .define("enableIronsManaCompat", true);

            hideIronsManaOverlay = builder
                    .comment("If enabled, BetterHP will hide Iron's default mana bar when compat is active.")
                    .define("hideIronsManaOverlay", true);

            showMana = builder
                    .comment("Show Iron's Spells & Spellbooks mana value (only when Iron's is installed)")
                    .define("showMana", true);

            showNumericMana = builder
                    .comment("Show numeric mana value")
                    .define("showNumericMana", true);

            showManaIcon = builder
                    .comment("Show mana icon")
                    .define("showManaIcon", true);

            manaFollowHealthWidth = builder
                    .comment("Place mana right after the rendered health text so it never overlaps")
                    .define("manaFollowHealthWidth", true);

            manaOffsetX = builder
                    .comment("Mana X offset relative to the health anchor (used if manaFollowHealthWidth=false)")
                    .defineInRange("manaOffsetX", 150, -1000, 1000);

            manaOffsetY = builder
                    .comment("Mana Y offset relative to the health anchor")
                    .defineInRange("manaOffsetY", 0, -1000, 1000);

            showStatusHealthIcons = builder
                    .comment("Swap the health icon and text colour while Poisoned, Withered or Frozen (like vanilla's heart types).",
                            "Frozen covers powder snow and, with Eidolon: Repraised, the Chilled effect.")
                    .define("showStatusHealthIcons", true);

            builder.pop();

            builder.comment("AppleSkin compat (only active when AppleSkin is installed).").push("appleskin");
            appleSkinCompat = builder.comment("Master switch for AppleSkin compat.").define("enabled", true);
            appleSkinFoodPreview = builder
                    .comment("While holding food, show the hunger (+N) and saturation it restores above the hunger value.",
                            "Follows AppleSkin's own 'showFoodValuesHudOverlay' settings.")
                    .define("showFoodPreview", true);
            appleSkinHealthPreview = builder
                    .comment("While holding food, show the estimated health it will regenerate after the health value.",
                            "Follows AppleSkin's own 'showFoodHealthHudOverlay' setting.")
                    .define("showHealthPreview", true);
            appleSkinExhaustionBar = builder
                    .comment("Draw a thin exhaustion bar under the hunger value (follows AppleSkin's 'showFoodExhaustionHudUnderlay').")
                    .define("showExhaustionBar", true);
            builder.pop();

            builder.comment("Eidolon: Repraised compat (only active when Eidolon is installed).").push("eidolon");
            eidolonCompat = builder.comment("Master switch for Eidolon compat.").define("enabled", true);
            eidolonChilledFrozenHearts = builder
                    .comment("When you are Chilled (hit by a Wraith, Bonechill wand, frost spells...), your health turns frozen:",
                            "the health icon becomes an ice bottle and the number turns icy blue.")
                    .define("chilledFrozenHearts", true);
            eidolonShowEtherealHealth = builder
                    .comment("Show Eidolon ethereal (soul) health as a purple +N after your health.")
                    .define("showEtherealHealth", true);
            eidolonHideHeartsOverlay = builder
                    .comment("Hide Eidolon's own heart overlay while vanilla hearts are hidden, so it doesn't float over empty space.")
                    .define("hideEidolonHeartsOverlay", true);
            builder.pop();

            builder.comment("Legendary Survival Overhaul compat (only active when LSO is installed).").push("legendarySurvivalOverhaul");
            lsoCompat = builder.comment("Master switch for LSO compat.").define("enabled", true);
            lsoReplaceThirstBar = builder
                    .comment("Show thirst as a BetterHP value (water bottle icon) instead of LSO's thirst bar.")
                    .define("replaceThirstBar", true);
            lsoShowThirstSaturation = builder.comment("Show thirst saturation as +N after the thirst value.")
                    .define("showThirstSaturation", true);
            lsoThirstX = builder.comment("Horizontal position of the thirst display").defineInRange("thirstDisplayX", 70, -1000, 1000);
            lsoThirstY = builder.comment("Vertical position of the thirst display").defineInRange("thirstDisplayY", 74, 0, 1000);
            lsoReplaceTemperature = builder
                    .comment("Show body temperature as a BetterHP thermometer instead of LSO's temperature indicator.",
                            "The exact degrees only show with LSO's thermometer equipped, like LSO itself. Uses LSO's C/F setting.")
                    .define("replaceTemperature", true);
            lsoTemperatureX = builder.comment("Horizontal position of the temperature display").defineInRange("temperatureDisplayX", -5, -1000, 1000);
            lsoTemperatureY = builder.comment("Vertical position of the temperature display").defineInRange("temperatureDisplayY", 60, 0, 1000);
            lsoColdHungerFrozen = builder.comment("Show a frozen hunger icon/colour while LSO's Cold Hunger effect is active.")
                    .define("coldHungerFrozenIcon", true);
            lsoShowShieldHealth = builder.comment("Show LSO shield health as a silver +N after your health.")
                    .define("showShieldHealth", true);
            lsoShowBrokenHearts = builder.comment("Show LSO broken hearts as a dark red -N\u2764 after your health.")
                    .define("showBrokenHearts", true);
            builder.pop();
        }
    }
}
