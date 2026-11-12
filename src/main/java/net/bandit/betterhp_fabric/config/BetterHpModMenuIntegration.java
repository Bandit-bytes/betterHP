package net.bandit.betterhp_fabric.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class BetterHpModMenuIntegration implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return this::createConfigScreen;
    }
    public Screen createConfigScreen(Screen parent) {
        if (ConfigManager.getConfigData() == null) {
            ConfigManager.loadConfig();
        }

        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Text.translatable("config.betterhp.title"));

        ConfigCategory general = builder.getOrCreateCategory(Text.translatable("config.betterhp.category.general"));

        ConfigEntryBuilder entryBuilder = builder.entryBuilder();

        general.addEntry(entryBuilder.startBooleanToggle(Text.translatable("config.betterhp.renderVanillaHud"), ConfigManager.getConfigData().renderVanillaHud)
                .setDefaultValue(false)
                .setSaveConsumer(newValue -> {
                    ConfigManager.getConfigData().renderVanillaHud = newValue;
                })
                .build());

        general.addEntry(entryBuilder.startBooleanToggle(Text.translatable("config.betterhp.showHealthIcon"), ConfigManager.showHealthIcon())
                .setDefaultValue(true)
                .setSaveConsumer(newValue -> {
                    ConfigManager.getConfigData().showHealthIcon = newValue;
                })
                .build());

        general.addEntry(entryBuilder.startBooleanToggle(Text.translatable("config.betterhp.showHealthValue"), ConfigManager.showHealthValue())
                .setDefaultValue(true)
                .setSaveConsumer(newValue -> {
                    ConfigManager.getConfigData().showHealthValue = newValue;
                })
                .build());


        general.addEntry(entryBuilder.startBooleanToggle(Text.translatable("config.betterhp.showHungerIcon"), ConfigManager.showHungerIcon())
                .setDefaultValue(true)
                .setSaveConsumer(newValue -> {
                    ConfigManager.getConfigData().showHungerIcon = newValue;
                })
                .build());
        general.addEntry(entryBuilder.startBooleanToggle(Text.translatable("config.betterhp.showNumericHunger"), ConfigManager.getConfigData().showHungerValue)
                .setDefaultValue(true)
                .setSaveConsumer(newValue -> {
                    ConfigManager.getConfigData().showHungerValue = newValue;
                })
                .build());


        general.addEntry(entryBuilder.startBooleanToggle(Text.translatable("config.betterhp.showArmorIcon"), ConfigManager.showArmorIcon())
                .setDefaultValue(true)
                .setSaveConsumer(newValue -> {
                    ConfigManager.getConfigData().showArmorIcon = newValue;
                })
                .build());

        general.addEntry(entryBuilder.startBooleanToggle(Text.translatable("config.betterhp.showArmorValue"), ConfigManager.showArmorValue())
                .setDefaultValue(true)
                .setSaveConsumer(newValue -> {
                    ConfigManager.getConfigData().showArmorValue = newValue;
                })
                .build());

        general.addEntry(entryBuilder.startBooleanToggle(Text.translatable("config.betterhp.showToughnessValue"), ConfigManager.showToughnessValue())
                .setDefaultValue(true)
                .setSaveConsumer(newValue -> {
                    ConfigManager.getConfigData().showToughnessValue = newValue;
                })
                .build());


        general.addEntry(entryBuilder.startBooleanToggle(Text.translatable("config.betterhp.showBreatheIcon"), ConfigManager.showBreatheIcon())
                .setDefaultValue(true)
                .setSaveConsumer(newValue -> {
                    ConfigManager.getConfigData().showBreatheIcon = newValue;
                })
                .build());

        general.addEntry(entryBuilder.startBooleanToggle(Text.translatable("config.betterhp.showOxygenValue"), ConfigManager.showOxygenValue())
                .setDefaultValue(true)
                .setSaveConsumer(newValue -> {
                    ConfigManager.getConfigData().showOxygenValue = newValue;
                })
                .build());


        // Integer fields (X and Y positions)
        // Health
        general.addEntry(entryBuilder.startIntField(Text.translatable("config.betterhp.healthDisplayX"), ConfigManager.healthDisplayX())
                .setDefaultValue(-76)
                .setSaveConsumer(newValue -> {
                    ConfigManager.getConfigData().healthDisplayX = newValue;
                })
                .build());

        general.addEntry(entryBuilder.startIntField(Text.translatable("config.betterhp.healthDisplayY"), ConfigManager.healthDisplayY())
                .setDefaultValue(44)
                .setSaveConsumer(newValue -> {
                    ConfigManager.getConfigData().healthDisplayY = newValue;
                })
                .build());

        // Hunger
        general.addEntry(entryBuilder.startIntField(Text.translatable("config.betterhp.hungerDisplayX"), ConfigManager.hungerDisplayX())
                .setDefaultValue(24)
                .setSaveConsumer(newValue -> {
                    ConfigManager.getConfigData().hungerDisplayX = newValue;
                })
                .build());

        general.addEntry(entryBuilder.startIntField(Text.translatable("config.betterhp.hungerDisplayY"), ConfigManager.hungerDisplayY())
                .setDefaultValue(46)
                .setSaveConsumer(newValue -> {
                    ConfigManager.getConfigData().hungerDisplayY = newValue;
                })
                .build());

        // Armor
        general.addEntry(entryBuilder.startIntField(Text.translatable("config.betterhp.armorDisplayX"), ConfigManager.armorDisplayX())
                .setDefaultValue(-8)
                .setSaveConsumer(newValue -> {
                    ConfigManager.getConfigData().armorDisplayX = newValue;
                })
                .build());

        general.addEntry(entryBuilder.startIntField(Text.translatable("config.betterhp.armorDisplayY"), ConfigManager.armorDisplayY())
                .setDefaultValue(56)
                .setSaveConsumer(newValue -> {
                    ConfigManager.getConfigData().armorDisplayY = newValue;
                })
                .build());

        // Breathing
        general.addEntry(entryBuilder.startIntField(Text.translatable("config.betterhp.breatheDisplayX"), ConfigManager.breatheDisplayX())
                .setDefaultValue(24)
                .setSaveConsumer(newValue -> {
                    ConfigManager.getConfigData().breatheDisplayX = newValue;
                })
                .build());

        general.addEntry(entryBuilder.startIntField(Text.translatable("config.betterhp.breatheDisplayY"), ConfigManager.breatheDisplayY())
                .setDefaultValue(60)
                .setSaveConsumer(newValue -> {
                    ConfigManager.getConfigData().breatheDisplayY = newValue;
                })
                .build());
        // Show Toughness Icon Toggle
        general.addEntry(entryBuilder.startBooleanToggle(Text.translatable("config.betterhp.showToughnessIcon"), ConfigManager.showToughnessIcon())
                .setDefaultValue(true)
                .setSaveConsumer(newValue -> {
                    ConfigManager.getConfigData().showToughnessIcon = newValue;
                })
                .build());

// Toughness Display X
        general.addEntry(entryBuilder.startIntField(Text.translatable("config.betterhp.toughnessDisplayX"), ConfigManager.toughnessDisplayX())
                .setDefaultValue(30)
                .setSaveConsumer(newValue -> {
                    ConfigManager.getConfigData().toughnessDisplayX = newValue;
                })
                .build());

// Toughness Display Y
        general.addEntry(entryBuilder.startIntField(Text.translatable("config.betterhp.toughnessDisplayY"), ConfigManager.toughnessDisplayY())
                .setDefaultValue(0)
                .setSaveConsumer(newValue -> {
                    ConfigManager.getConfigData().toughnessDisplayY = newValue;
                })
                .build());
        // Toggle for static health color
        general.addEntry(entryBuilder.startBooleanToggle(Text.translatable("config.betterhp.useStaticHealthColor"), ConfigManager.useStaticHealthColor())
                .setDefaultValue(false)
                .setSaveConsumer(newValue -> {
                    ConfigManager.getConfigData().useStaticHealthColor = newValue;
                })
                .build());

// Health color picker
        general.addEntry(entryBuilder.startColorField(Text.translatable("config.betterhp.staticHealthColor"), ConfigManager.staticHealthColor())
                .setDefaultValue(0xFF0000)
                .setSaveConsumer(newValue -> {
                    ConfigManager.getConfigData().staticHealthColor = newValue;
                })
                .build());

// Toggle for static hunger color
        general.addEntry(entryBuilder.startBooleanToggle(Text.translatable("config.betterhp.useStaticHungerColor"), ConfigManager.useStaticHungerColor())
                .setDefaultValue(false)
                .setSaveConsumer(newValue -> {
                    ConfigManager.getConfigData().useStaticHungerColor = newValue;
                })
                .build());

// Hunger color picker
        general.addEntry(entryBuilder.startColorField(Text.translatable("config.betterhp.staticHungerColor"), ConfigManager.staticHungerColor())
                .setDefaultValue(0xFFA500)
                .setSaveConsumer(newValue -> {
                    ConfigManager.getConfigData().staticHungerColor = newValue;
                })
                .build());



        ConfigCategory compat = builder.getOrCreateCategory(Text.translatable("config.betterhp.category.compat"));
        compat.addEntry(entryBuilder.startBooleanToggle(Text.translatable("config.betterhp.showStatusHealthIcons"), ConfigManager.showStatusHealthIcons())
                .setDefaultValue(true)
                .setTooltip(Text.translatable("config.betterhp.showStatusHealthIcons.tooltip"))
                .setSaveConsumer(v -> ConfigManager.getConfigData().showStatusHealthIcons = v)
                .build());
        compat.addEntry(entryBuilder.startBooleanToggle(Text.translatable("config.betterhp.appleSkinCompat"), ConfigManager.appleSkinCompat())
                .setDefaultValue(true)
                .setTooltip(Text.translatable("config.betterhp.appleSkinCompat.tooltip"))
                .setSaveConsumer(v -> ConfigManager.getConfigData().appleSkinCompat = v)
                .build());
        compat.addEntry(entryBuilder.startBooleanToggle(Text.translatable("config.betterhp.appleSkinFoodPreview"), ConfigManager.appleSkinFoodPreview())
                .setDefaultValue(true)
                .setSaveConsumer(v -> ConfigManager.getConfigData().appleSkinFoodPreview = v)
                .build());
        compat.addEntry(entryBuilder.startBooleanToggle(Text.translatable("config.betterhp.appleSkinHealthPreview"), ConfigManager.appleSkinHealthPreview())
                .setDefaultValue(true)
                .setSaveConsumer(v -> ConfigManager.getConfigData().appleSkinHealthPreview = v)
                .build());
        compat.addEntry(entryBuilder.startBooleanToggle(Text.translatable("config.betterhp.appleSkinExhaustionBar"), ConfigManager.appleSkinExhaustionBar())
                .setDefaultValue(true)
                .setSaveConsumer(v -> ConfigManager.getConfigData().appleSkinExhaustionBar = v)
                .build());

        // One save after every entry has been applied.
        builder.setSavingRunnable(ConfigManager::saveConfig);
        return builder.build();
    }
}
