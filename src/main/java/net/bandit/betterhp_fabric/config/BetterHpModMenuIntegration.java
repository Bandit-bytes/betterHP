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

    // Create the configuration screen
    public Screen createConfigScreen(Screen parent) {
        // Ensure configData is loaded
        if (ConfigManager.getConfigData() == null) {
            ConfigManager.loadConfig();  // Load config data if it's not already loaded
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
                    ConfigManager.saveConfig();
                })
                .build());

        general.addEntry(entryBuilder.startBooleanToggle(Text.translatable("config.betterhp.showHealthIcon"), ConfigManager.showHealthIcon())
                .setDefaultValue(true)
                .setSaveConsumer(newValue -> {
                    ConfigManager.getConfigData().showHealthIcon = newValue;
                    ConfigManager.saveConfig();
                })
                .build());

        general.addEntry(entryBuilder.startBooleanToggle(Text.translatable("config.betterhp.showHungerIcon"), ConfigManager.showHungerIcon())
                .setDefaultValue(true)
                .setSaveConsumer(newValue -> {
                    ConfigManager.getConfigData().showHungerIcon = newValue;
                    ConfigManager.saveConfig();
                })
                .build());
        general.addEntry(entryBuilder.startBooleanToggle(Text.translatable("config.betterhp.showNumericHunger"), ConfigManager.getConfigData().showNumericHunger)
                .setDefaultValue(true)
                .setSaveConsumer(newValue -> {
                    ConfigManager.getConfigData().showNumericHunger = newValue;
                    ConfigManager.saveConfig();
                })
                .build());


        general.addEntry(entryBuilder.startBooleanToggle(Text.translatable("config.betterhp.showArmorIcon"), ConfigManager.showArmorIcon())
                .setDefaultValue(true)
                .setSaveConsumer(newValue -> {
                    ConfigManager.getConfigData().showArmorIcon = newValue;
                    ConfigManager.saveConfig();
                })
                .build());

        general.addEntry(entryBuilder.startBooleanToggle(Text.translatable("config.betterhp.showBreatheIcon"), ConfigManager.showBreatheIcon())
                .setDefaultValue(true)
                .setSaveConsumer(newValue -> {
                    ConfigManager.getConfigData().showBreatheIcon = newValue;
                    ConfigManager.saveConfig();
                })
                .build());
        general.addEntry(entryBuilder.startBooleanToggle(Text.translatable("config.betterhp.showNumericOxygen"), ConfigManager.getConfigData().showNumericOxygen)
                .setDefaultValue(true)
                .setSaveConsumer(newValue -> {
                    ConfigManager.getConfigData().showNumericOxygen = newValue;
                    ConfigManager.saveConfig();
                })
                .build());


        // Integer fields (X and Y positions)
        // Health
        general.addEntry(entryBuilder.startIntField(Text.translatable("config.betterhp.healthDisplayX"), ConfigManager.healthDisplayX())
                .setDefaultValue(-76)
                .setSaveConsumer(newValue -> {
                    ConfigManager.getConfigData().healthDisplayX = newValue;
                    ConfigManager.saveConfig();
                })
                .build());

        general.addEntry(entryBuilder.startIntField(Text.translatable("config.betterhp.healthDisplayY"), ConfigManager.healthDisplayY())
                .setDefaultValue(44)
                .setSaveConsumer(newValue -> {
                    ConfigManager.getConfigData().healthDisplayY = newValue;
                    ConfigManager.saveConfig();
                })
                .build());

        // Hunger
        general.addEntry(entryBuilder.startIntField(Text.translatable("config.betterhp.hungerDisplayX"), ConfigManager.hungerDisplayX())
                .setDefaultValue(24)
                .setSaveConsumer(newValue -> {
                    ConfigManager.getConfigData().hungerDisplayX = newValue;
                    ConfigManager.saveConfig();
                })
                .build());

        general.addEntry(entryBuilder.startIntField(Text.translatable("config.betterhp.hungerDisplayY"), ConfigManager.hungerDisplayY())
                .setDefaultValue(46)
                .setSaveConsumer(newValue -> {
                    ConfigManager.getConfigData().hungerDisplayY = newValue;
                    ConfigManager.saveConfig();
                })
                .build());

        // Armor
        general.addEntry(entryBuilder.startIntField(Text.translatable("config.betterhp.armorDisplayX"), ConfigManager.armorDisplayX())
                .setDefaultValue(-8)
                .setSaveConsumer(newValue -> {
                    ConfigManager.getConfigData().armorDisplayX = newValue;
                    ConfigManager.saveConfig();
                })
                .build());

        general.addEntry(entryBuilder.startIntField(Text.translatable("config.betterhp.armorDisplayY"), ConfigManager.armorDisplayY())
                .setDefaultValue(56)
                .setSaveConsumer(newValue -> {
                    ConfigManager.getConfigData().armorDisplayY = newValue;
                    ConfigManager.saveConfig();
                })
                .build());

        // Breathing
        general.addEntry(entryBuilder.startIntField(Text.translatable("config.betterhp.breatheDisplayX"), ConfigManager.breatheDisplayX())
                .setDefaultValue(24)
                .setSaveConsumer(newValue -> {
                    ConfigManager.getConfigData().breatheDisplayX = newValue;
                    ConfigManager.saveConfig();
                })
                .build());

        general.addEntry(entryBuilder.startIntField(Text.translatable("config.betterhp.breatheDisplayY"), ConfigManager.breatheDisplayY())
                .setDefaultValue(60)
                .setSaveConsumer(newValue -> {
                    ConfigManager.getConfigData().breatheDisplayY = newValue;
                    ConfigManager.saveConfig();
                })
                .build());
        // Show Toughness Icon Toggle
        general.addEntry(entryBuilder.startBooleanToggle(Text.translatable("config.betterhp.showToughnessIcon"), ConfigManager.showToughnessIcon())
                .setDefaultValue(true)
                .setSaveConsumer(newValue -> {
                    ConfigManager.getConfigData().showToughnessIcon = newValue;
                    ConfigManager.saveConfig();
                })
                .build());

// Toughness Display X
        general.addEntry(entryBuilder.startIntField(Text.translatable("config.betterhp.toughnessDisplayX"), ConfigManager.toughnessDisplayX())
                .setDefaultValue(30)
                .setSaveConsumer(newValue -> {
                    ConfigManager.getConfigData().toughnessDisplayX = newValue;
                    ConfigManager.saveConfig();
                })
                .build());

// Toughness Display Y
        general.addEntry(entryBuilder.startIntField(Text.translatable("config.betterhp.toughnessDisplayY"), ConfigManager.toughnessDisplayY())
                .setDefaultValue(0)
                .setSaveConsumer(newValue -> {
                    ConfigManager.getConfigData().toughnessDisplayY = newValue;
                    ConfigManager.saveConfig();
                })
                .build());


        builder.setSavingRunnable(ConfigManager::saveConfig);
        return builder.build();
    }
}
