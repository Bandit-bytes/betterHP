package net.bandit.betterhp_fabric.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class ConfigManager {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final File CONFIG_FILE = new File(FabricLoader.getInstance().getConfigDir().toFile(), "betterhp_config.json");

    private static ConfigData configData;
    private static long lastSaveTime = 0;
    private static final long SAVE_COOLDOWN_MS = 500;

    static {
        loadConfig();
    }

    public static synchronized void loadConfig() {
        if (!CONFIG_FILE.exists()) {
            System.out.println("Configuration file does not exist. Creating new config with default values.");
            configData = new ConfigData();
            saveConfig(); // Save default
        } else {
            try (FileReader reader = new FileReader(CONFIG_FILE)) {
                configData = GSON.fromJson(reader, ConfigData.class);
                if (configData == null) {
                    throw new IOException("Config file contains invalid data.");
                }
                System.out.println("Configuration loaded from " + CONFIG_FILE.getName());
            } catch (IOException e) {
                System.err.println("Error reading configuration file: " + e.getMessage());
                configData = new ConfigData();
                saveConfig();
            }
        }
    }

    public static synchronized void saveConfig() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastSaveTime < SAVE_COOLDOWN_MS) return;

        try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
            GSON.toJson(configData, writer);
            writer.flush();
            lastSaveTime = currentTime;
            System.out.println("Configuration saved to " + CONFIG_FILE.getName());
        } catch (IOException e) {
            System.err.println("Error saving configuration file: " + e.getMessage());
        }
    }

    // Getters
    public static boolean renderVanillaHud() { return configData != null && configData.renderVanillaHud; }
    public static boolean showHealthIcon() { return configData != null && configData.showHealthIcon; }
    public static boolean showArmorIcon() { return configData != null && configData.showArmorIcon; }
    public static boolean showToughnessIcon() { return configData != null && configData.showToughnessIcon; }
    public static boolean showHungerIcon() { return configData != null && configData.showHungerIcon; }
    public static boolean showBreatheIcon() { return configData != null && configData.showBreatheIcon; }
    public static boolean showNumericHunger() { return configData != null && configData.showNumericHunger; }
    public static boolean showNumericOxygen() { return configData != null && configData.showNumericOxygen; }
    public static boolean showSaturation() { return configData != null && configData.showSaturation; }

    public static int healthDisplayX() { return configData != null ? configData.healthDisplayX : -70; }
    public static int healthDisplayY() { return configData != null ? configData.healthDisplayY : 43; }
    public static int hungerDisplayX() { return configData != null ? configData.hungerDisplayX : 66; }
    public static int hungerDisplayY() { return configData != null ? configData.hungerDisplayY : 43; }
    public static int armorDisplayX() { return configData != null ? configData.armorDisplayX : -10; }
    public static int armorDisplayY() { return configData != null ? configData.armorDisplayY : 60; }
    public static int toughnessDisplayX() { return configData != null ? configData.toughnessDisplayX : 35; }
    public static int toughnessDisplayY() { return configData != null ? configData.toughnessDisplayY : 0; }
    public static int breatheDisplayX() { return configData != null ? configData.breatheDisplayX : 90; }
    public static int breatheDisplayY() { return configData != null ? configData.breatheDisplayY : 60; }
    public static int saturationDisplayX() { return configData != null ? configData.saturationDisplayX : 15; }
    public static int saturationDisplayY() { return configData != null ? configData.saturationDisplayY : 43; }
    public static int mountDisplayX() {return configData != null ?configData.mountDisplayX: -85; }
    public static int mountDisplayY() { return configData != null ? configData.mountDisplayY: 60; }

    public static ConfigData getConfigData() {
        if (configData == null) loadConfig();
        return configData;
    }

    public static void setConfigData(ConfigData newConfigData) {
        configData = newConfigData;
        saveConfig();
    }

    public static class ConfigData {
        boolean renderVanillaHud = false;
        boolean showHealthIcon = true;
        boolean showArmorIcon = true;
        boolean showToughnessIcon = true;
        boolean showHungerIcon = true;
        boolean showBreatheIcon = true;
        public boolean showNumericHunger = true;
        public boolean showNumericOxygen = true;
        public boolean showSaturation = true;

        int healthDisplayX = -85;
        int healthDisplayY = 43;
        int hungerDisplayX = 90;
        int hungerDisplayY = 43;
        int armorDisplayX = -10;
        int armorDisplayY = 45;
        int toughnessDisplayX = 35;
        int toughnessDisplayY = 0;
        int breatheDisplayX = 90;
        int breatheDisplayY = 60;
        int saturationDisplayX = 40;
        int saturationDisplayY = 43;
        int mountDisplayX = 0;
        int mountDisplayY = 120;
    }
}
