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


            builder.pop();
        }
    }
}
