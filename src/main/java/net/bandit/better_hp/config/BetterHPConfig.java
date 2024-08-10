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
        public final ForgeConfigSpec.BooleanValue showVanillaArmor;
        public final ForgeConfigSpec.BooleanValue showVanillaHunger;
        public final ForgeConfigSpec.BooleanValue showVanillaOxygen; // Existing config for vanilla oxygen bubbles
        public final ForgeConfigSpec.BooleanValue showNumericHunger;
        public final ForgeConfigSpec.BooleanValue showOxygenIcon; // Existing config for custom oxygen icon
        public final ForgeConfigSpec.BooleanValue showNumericOxygen; // Existing config for numeric oxygen display
        public final ForgeConfigSpec.BooleanValue showHealthIcon; // New config for custom health icon
        public final ForgeConfigSpec.BooleanValue showArmorIcon; // New config for custom armor icon
        public final ForgeConfigSpec.BooleanValue showHungerIcon; // New config for custom hunger icon
        public final ForgeConfigSpec.IntValue healthDisplayX;
        public final ForgeConfigSpec.IntValue healthDisplayY;
        public final ForgeConfigSpec.IntValue armorDisplayX;
        public final ForgeConfigSpec.IntValue armorDisplayY;
        public final ForgeConfigSpec.IntValue hungerDisplayX;
        public final ForgeConfigSpec.IntValue hungerDisplayY;
        public final ForgeConfigSpec.IntValue oxygenDisplayX; // Existing config for oxygen display position
        public final ForgeConfigSpec.IntValue oxygenDisplayY; // Existing config for oxygen display position

        public Client(ForgeConfigSpec.Builder builder) {
            builder.push("Display Settings");

            showVanillaHearts = builder
                    .comment("Show vanilla hearts")
                    .define("showVanillaHearts", false);

            showVanillaArmor = builder
                    .comment("Show vanilla armor bar")
                    .define("showVanillaArmor", false);

            showVanillaHunger = builder
                    .comment("Show vanilla hunger bar")
                    .define("showVanillaHunger", false);

            showVanillaOxygen = builder
                    .comment("Show vanilla oxygen bubbles")
                    .define("showVanillaOxygen", false); // Default to false for custom oxygen

            showNumericHunger = builder
                    .comment("Show numeric hunger instead of vanilla hunger bar")
                    .define("showNumericHunger", true);

            showOxygenIcon = builder
                    .comment("Show custom oxygen icon when underwater")
                    .define("showOxygenIcon", true);

            showNumericOxygen = builder
                    .comment("Show numeric oxygen value when underwater")
                    .define("showNumericOxygen", true);

            showHealthIcon = builder
                    .comment("Show custom health icon")
                    .define("showHealthIcon", true); // New option for health icon

            showArmorIcon = builder
                    .comment("Show custom armor icon")
                    .define("showArmorIcon", true); // New option for armor icon

            showHungerIcon = builder
                    .comment("Show custom hunger icon")
                    .define("showHungerIcon", true); // New option for hunger icon

            healthDisplayX = builder
                    .comment("Horizontal position of the health icon display")
                    .defineInRange("healthDisplayX", -70, -1000, 1000);

            healthDisplayY = builder
                    .comment("Vertical position of the health icon display")
                    .defineInRange("healthDisplayY", 43, 0, 1000);

            armorDisplayX = builder
                    .comment("Horizontal position of the armor icon display")
                    .defineInRange("armorDisplayX", -70, -1000, 1000);

            armorDisplayY = builder
                    .comment("Vertical position of the armor icon display")
                    .defineInRange("armorDisplayY", 60, 0, 1000);

            hungerDisplayX = builder
                    .comment("Horizontal position of the hunger icon display")
                    .defineInRange("hungerDisplayX", 66, -1000, 1000);

            hungerDisplayY = builder
                    .comment("Vertical position of the hunger icon display")
                    .defineInRange("hungerDisplayY", 43, 0, 1000);

            oxygenDisplayX = builder
                    .comment("Horizontal position of the oxygen icon display")
                    .defineInRange("oxygenDisplayX", 67, -1000, 1000); // Placed above hunger

            oxygenDisplayY = builder
                    .comment("Vertical position of the oxygen icon display")
                    .defineInRange("oxygenDisplayY", 60, 0, 1000); // Adjusted position

            builder.pop();
        }
    }
}
