package net.bandit.betterhp_fabric.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;


public class BetterHpModMenuIntegration implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return this::createConfigScreen;
    }

    // Create the configuration screen
    public Screen createConfigScreen(Screen parent) {
        // Ensure configData is loaded
        if (ConfigManager.getConfigData() == null) {
            ConfigManager.loadConfig();  // Load config data if it's not already loaded
        }

        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Component.translatable("config.betterhp.title"));

        ConfigCategory general = builder.getOrCreateCategory(Component.translatable("config.betterhp.category.general"));

        ConfigEntryBuilder entryBuilder = builder.entryBuilder();

        general.addEntry(entryBuilder.startBooleanToggle(Component.translatable("config.betterhp.renderVanillaHud"), ConfigManager.getConfigData().renderVanillaHud)
                .setDefaultValue(false)
                .setSaveConsumer(newValue -> {
                    ConfigManager.getConfigData().renderVanillaHud = newValue;
                    ConfigManager.saveConfig();
                })
                .build());

        general.addEntry(entryBuilder.startBooleanToggle(Component.translatable("config.betterhp.showHealthIcon"), ConfigManager.showHealthIcon())
                .setDefaultValue(true)
                .setSaveConsumer(newValue -> {
                    ConfigManager.getConfigData().showHealthIcon = newValue;
                    ConfigManager.saveConfig();
                })
                .build());

        general.addEntry(entryBuilder.startBooleanToggle(Component.translatable("config.betterhp.showHungerIcon"), ConfigManager.showHungerIcon())
                .setDefaultValue(true)
                .setSaveConsumer(newValue -> {
                    ConfigManager.getConfigData().showHungerIcon = newValue;
                    ConfigManager.saveConfig();
                })
                .build());
        general.addEntry(entryBuilder.startBooleanToggle(Component.translatable("config.betterhp.showNumericHunger"), ConfigManager.getConfigData().showNumericHunger)
                .setDefaultValue(true)
                .setSaveConsumer(newValue -> {
                    ConfigManager.getConfigData().showNumericHunger = newValue;
                    ConfigManager.saveConfig();
                })
                .build());


        general.addEntry(entryBuilder.startBooleanToggle(Component.translatable("config.betterhp.showArmorIcon"), ConfigManager.showArmorIcon())
                .setDefaultValue(true)
                .setSaveConsumer(newValue -> {
                    ConfigManager.getConfigData().showArmorIcon = newValue;
                    ConfigManager.saveConfig();
                })
                .build());
        general.addEntry(entryBuilder.startBooleanToggle(Component.translatable("config.betterhp.showToughnessIcon"), ConfigManager.showArmorIcon())
                .setDefaultValue(true)
                .setSaveConsumer(newValue -> {
                    ConfigManager.getConfigData().showToughnessIcon = newValue;
                    ConfigManager.saveConfig();
                })
                .build());

        general.addEntry(entryBuilder.startBooleanToggle(Component.translatable("config.betterhp.showBreatheIcon"), ConfigManager.showBreatheIcon())
                .setDefaultValue(true)
                .setSaveConsumer(newValue -> {
                    ConfigManager.getConfigData().showBreatheIcon = newValue;
                    ConfigManager.saveConfig();
                })
                .build());
        general.addEntry(entryBuilder.startBooleanToggle(Component.translatable("config.betterhp.showNumericOxygen"), ConfigManager.getConfigData().showNumericOxygen)
                .setDefaultValue(true)
                .setSaveConsumer(newValue -> {
                    ConfigManager.getConfigData().showNumericOxygen = newValue;
                    ConfigManager.saveConfig();
                })
                .build());


        // Integer fields (X and Y positions)
        // Health
        general.addEntry(entryBuilder.startIntField(Component.translatable("config.betterhp.healthDisplayX"), ConfigManager.healthDisplayX())
                .setDefaultValue(-76)
                .setSaveConsumer(newValue -> {
                    ConfigManager.getConfigData().healthDisplayX = newValue;
                    ConfigManager.saveConfig();
                })
                .build());

        general.addEntry(entryBuilder.startIntField(Component.translatable("config.betterhp.healthDisplayY"), ConfigManager.healthDisplayY())
                .setDefaultValue(44)
                .setSaveConsumer(newValue -> {
                    ConfigManager.getConfigData().healthDisplayY = newValue;
                    ConfigManager.saveConfig();
                })
                .build());

        // Hunger
        general.addEntry(entryBuilder.startIntField(Component.translatable("config.betterhp.hungerDisplayX"), ConfigManager.hungerDisplayX())
                .setDefaultValue(24)
                .setSaveConsumer(newValue -> {
                    ConfigManager.getConfigData().hungerDisplayX = newValue;
                    ConfigManager.saveConfig();
                })
                .build());

        general.addEntry(entryBuilder.startIntField(Component.translatable("config.betterhp.hungerDisplayY"), ConfigManager.hungerDisplayY())
                .setDefaultValue(46)
                .setSaveConsumer(newValue -> {
                    ConfigManager.getConfigData().hungerDisplayY = newValue;
                    ConfigManager.saveConfig();
                })
                .build());

        // Toggle: Show Saturation
        general.addEntry(entryBuilder.startBooleanToggle(Component.translatable("config.betterhp.showSaturation"), ConfigManager.showSaturation())
                .setDefaultValue(true)
                .setSaveConsumer(newValue -> {
                    ConfigManager.getConfigData().showSaturation = newValue;
                    ConfigManager.saveConfig();
                })
                .build());

        // Saturation Position X
        general.addEntry(entryBuilder.startIntField(Component.translatable("config.betterhp.saturationDisplayX"), ConfigManager.saturationDisplayX())
                .setDefaultValue(80)
                .setSaveConsumer(newValue -> {
                    ConfigManager.getConfigData().saturationDisplayX = newValue;
                    ConfigManager.saveConfig();
                })
                .build());

        // Saturation Position Y
        general.addEntry(entryBuilder.startIntField(Component.translatable("config.betterhp.saturationDisplayY"), ConfigManager.saturationDisplayY())
                .setDefaultValue(43)
                .setSaveConsumer(newValue -> {
                    ConfigManager.getConfigData().saturationDisplayY = newValue;
                    ConfigManager.saveConfig();
                })
                .build());

        // Armor
        general.addEntry(entryBuilder.startIntField(Component.translatable("config.betterhp.armorDisplayX"), ConfigManager.armorDisplayX())
                .setDefaultValue(-8)
                .setSaveConsumer(newValue -> {
                    ConfigManager.getConfigData().armorDisplayX = newValue;
                    ConfigManager.saveConfig();
                })
                .build());

        general.addEntry(entryBuilder.startIntField(Component.translatable("config.betterhp.armorDisplayY"), ConfigManager.armorDisplayY())
                .setDefaultValue(56)
                .setSaveConsumer(newValue -> {
                    ConfigManager.getConfigData().armorDisplayY = newValue;
                    ConfigManager.saveConfig();
                })
                .build());

        //Toughness
        general.addEntry(entryBuilder.startIntField(Component.translatable("config.betterhp.toughnessDisplayX"), ConfigManager.toughnessDisplayX())
                .setDefaultValue(-8)
                .setSaveConsumer(newValue -> {
                    ConfigManager.getConfigData().toughnessDisplayX = newValue;
                    ConfigManager.saveConfig();
                })
                .build());

        general.addEntry(entryBuilder.startIntField(Component.translatable("config.betterhp.toughnessDisplayY"), ConfigManager.toughnessDisplayY())
                .setDefaultValue(56)
                .setSaveConsumer(newValue -> {
                    ConfigManager.getConfigData().toughnessDisplayY = newValue;
                    ConfigManager.saveConfig();
                })
                .build());


        // Breathing
        general.addEntry(entryBuilder.startIntField(Component.translatable("config.betterhp.breatheDisplayX"), ConfigManager.breatheDisplayX())
                .setDefaultValue(24)
                .setSaveConsumer(newValue -> {
                    ConfigManager.getConfigData().breatheDisplayX = newValue;
                    ConfigManager.saveConfig();
                })
                .build());

        general.addEntry(entryBuilder.startIntField(Component.translatable("config.betterhp.breatheDisplayY"), ConfigManager.breatheDisplayY())
                .setDefaultValue(60)
                .setSaveConsumer(newValue -> {
                    ConfigManager.getConfigData().breatheDisplayY = newValue;
                    ConfigManager.saveConfig();
                })
                .build());

        builder.setSavingRunnable(ConfigManager::saveConfig);
        return builder.build();
    }
}
