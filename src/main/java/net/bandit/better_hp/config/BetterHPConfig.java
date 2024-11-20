package net.bandit.better_hp.config;

import net.minecraft.client.resources.language.I18n;
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
        public final ForgeConfigSpec.IntValue healthDisplayX;
        public final ForgeConfigSpec.IntValue healthDisplayY;
        public final ForgeConfigSpec.IntValue healthTextOffset;
        public final ForgeConfigSpec.IntValue armorDisplayX;
        public final ForgeConfigSpec.IntValue armorDisplayY;
        public final ForgeConfigSpec.IntValue hungerDisplayX;
        public final ForgeConfigSpec.IntValue hungerDisplayY;
        public final ForgeConfigSpec.IntValue oxygenDisplayX;
        public final ForgeConfigSpec.IntValue oxygenDisplayY;
        public final ForgeConfigSpec.IntValue healthColor;
        public final ForgeConfigSpec.BooleanValue showDecimalHealth;

        public Client(ForgeConfigSpec.Builder builder) {
            builder.push(translate("config.better_hp.display_settings"));

            showVanillaHearts = builder
                    .comment(translate("config.better_hp.show_vanilla_hearts"))
                    .define("showVanillaHearts", false);

            showDecimalHealth = builder
                    .comment(translate("config.better_hp.show_decimal_health"))
                    .define("showDecimalHealth", true);

            showVanillaArmor = builder
                    .comment(translate("config.better_hp.show_vanilla_armor"))
                    .define("showVanillaArmor", false);

            showVanillaHunger = builder
                    .comment(translate("config.better_hp.show_vanilla_hunger"))
                    .define("showVanillaHunger", false);

            showVanillaOxygen = builder
                    .comment(translate("config.better_hp.show_vanilla_oxygen"))
                    .define("showVanillaOxygen", false);

            showNumericHunger = builder
                    .comment(translate("config.better_hp.show_numeric_hunger"))
                    .define("showNumericHunger", true);

            showOxygenIcon = builder
                    .comment(translate("config.better_hp.show_oxygen_icon"))
                    .define("showOxygenIcon", true);

            showNumericHealth = builder
                    .comment(translate("config.better_hp.show_numeric_health"))
                    .define("showNumericHealth", true);

            showNumericOxygen = builder
                    .comment(translate("config.better_hp.show_numeric_oxygen"))
                    .define("showNumericOxygen", true);

            showHealthIcon = builder
                    .comment(translate("config.better_hp.show_health_icon"))
                    .define("showHealthIcon", true);

            showArmorIcon = builder
                    .comment(translate("config.better_hp.show_armor_icon"))
                    .define("showArmorIcon", true);

            showHungerIcon = builder
                    .comment(translate("config.better_hp.show_hunger_icon"))
                    .define("showHungerIcon", true);

            enableDynamicHealthColor = builder
                    .comment(translate("config.better_hp.enable_dynamic_health_color"))
                    .define("enableDynamicHealthColor", true);

            healthDisplayX = builder
                    .comment(translate("config.better_hp.health_display_x"))
                    .defineInRange("healthDisplayX", -70, -1000, 1000);

            healthDisplayY = builder
                    .comment(translate("config.better_hp.health_display_y"))
                    .defineInRange("healthDisplayY", 43, 0, 1000);

            healthTextOffset = builder
                    .comment("config.better_hp.healthTextOffset")
                    .defineInRange("healthTextOffset", 15, 0, 100);

            armorDisplayX = builder
                    .comment(translate("config.better_hp.armor_display_x"))
                    .defineInRange("armorDisplayX", -70, -1000, 1000);

            armorDisplayY = builder
                    .comment(translate("config.better_hp.armor_display_y"))
                    .defineInRange("armorDisplayY", 60, 0, 1000);

            hungerDisplayX = builder
                    .comment(translate("config.better_hp.hunger_display_x"))
                    .defineInRange("hungerDisplayX", 66, -1000, 1000);

            hungerDisplayY = builder
                    .comment(translate("config.better_hp.hunger_display_y"))
                    .defineInRange("hungerDisplayY", 43, 0, 1000);

            oxygenDisplayX = builder
                    .comment(translate("config.better_hp.oxygen_display_x"))
                    .defineInRange("oxygenDisplayX", 67, -1000, 1000);

            oxygenDisplayY = builder
                    .comment(translate("config.better_hp.oxygen_display_y"))
                    .defineInRange("oxygenDisplayY", 60, 0, 1000);

            healthColor = builder
                    .comment(translate("config.better_hp.health_color"))
                    .defineInRange("healthColor", 0xFF5555, 0x000000, 0xFFFFFF);

            builder.pop();
        }

        private static String translate(String key) {
            return I18n.get(key);
        }
    }
}
