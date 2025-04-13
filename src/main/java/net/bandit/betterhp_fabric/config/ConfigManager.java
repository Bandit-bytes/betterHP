package net.bandit.betterhp_fabric.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.*;

public class ConfigManager {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final File CONFIG_FILE = new File(FabricLoader.getInstance().getConfigDir().toFile(), "betterhp_config.json");
    private static ConfigData configData;
    private static long lastModified = 0;

    static {
        loadConfig();
        watchConfigFile();
    }

    public static void loadConfig() {
        if (!CONFIG_FILE.exists()) {
            System.out.println("Configuration file does not exist. Creating new config with default values.");
            configData = new ConfigData();
            saveConfig();
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
//                saveConfig();
            }
        }
    }

    private static long lastSaveTime = 0;
    private static final long SAVE_COOLDOWN_MS = 500;

    public static void saveConfig() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastSaveTime < SAVE_COOLDOWN_MS) {
            return;
        }

        try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
            GSON.toJson(configData, writer);
            lastModified = CONFIG_FILE.lastModified();
            lastSaveTime = currentTime;
            System.out.println("Configuration saved to " + CONFIG_FILE.getName());
        } catch (IOException e) {
            System.err.println("Error saving configuration file: " + e.getMessage());
        }
    }
    private static void watchConfigFile() {
        try {
            Path configPath = CONFIG_FILE.toPath();
            WatchService watchService = FileSystems.getDefault().newWatchService();
            configPath.getParent().register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);

            Thread watchThread = new Thread(() -> {
                try {
                    WatchKey key;
                    while ((key = watchService.take()) != null) {
                        for (WatchEvent<?> event : key.pollEvents()) {
                            if (event.context().toString().equals(configPath.getFileName().toString())) {
                                // Avoid reloading if not necessary (debounce)
                                long newModified = CONFIG_FILE.lastModified();
                                if (newModified > lastModified + 500) { // 500ms debounce
                                    loadConfig(); // Reload the config on change
                                    lastModified = newModified;
                                    System.out.println("Configuration reloaded from " + CONFIG_FILE.getName());
                                }
                            }
                        }
                        key.reset();
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    System.err.println("Configuration watcher interrupted: " + e.getMessage());
                }
            });
            watchThread.setDaemon(true);
            watchThread.start();
        } catch (IOException e) {
            System.err.println("Error setting up configuration file watcher: " + e.getMessage());
        }
    }

    // Configuration getters
    public static boolean renderVanillaHud() { return configData != null && configData.renderVanillaHud; }
    public static boolean showHealthIcon() { return configData != null && configData.showHealthIcon; }
    public static boolean showArmorIcon() { return configData != null && configData.showArmorIcon; }
    public static boolean showHungerIcon() { return configData != null && configData.showHungerIcon; }
    public static boolean showBreatheIcon() { return configData != null && configData.showBreatheIcon; }
    public static boolean showHealthValue() { return configData != null && configData.showHealthValue; }
    public static boolean showArmorValue() { return configData != null && configData.showArmorValue; }
    public static boolean showHungerValue() { return configData != null && configData.showHungerValue; }
    public static boolean showOxygenValue() { return configData != null && configData.showOxygenValue; }
    public static boolean showToughnessValue() { return configData != null && configData.showToughnessValue; }
    public static boolean useStaticHealthColor() { return configData != null && configData.useStaticHealthColor; }
    public static int staticHealthColor() { return configData != null ? configData.staticHealthColor : 0xFF0000; }
    public static boolean useStaticHungerColor() { return configData != null && configData.useStaticHungerColor; }
    public static int staticHungerColor() { return configData != null ? configData.staticHungerColor : 0xFFA500; }
    public static boolean showToughnessIcon() { return configData != null && configData.showToughnessIcon; }
    public static int toughnessDisplayX() { return configData != null ? configData.toughnessDisplayX : 0; }
    public static int toughnessDisplayY() { return configData != null ? configData.toughnessDisplayY : 16; }
    public static int healthDisplayX() { return configData != null ? configData.healthDisplayX : -76; }
    public static int healthDisplayY() { return configData != null ? configData.healthDisplayY : 44; }
    public static int hungerDisplayX() { return configData != null ? configData.hungerDisplayX : 24; }
    public static int hungerDisplayY() { return configData != null ? configData.hungerDisplayY : 46; }
    public static int armorDisplayX() { return configData != null ? configData.armorDisplayX : -8; }
    public static int armorDisplayY() { return configData != null ? configData.armorDisplayY : 56; }
    public static int breatheDisplayX() { return configData != null ? configData.breatheDisplayX : 24; }
    public static int breatheDisplayY() { return configData != null ? configData.breatheDisplayY : 60; }

    public static ConfigData getConfigData() {
        if (configData == null) {
            loadConfig();
        }
        return configData;
    }

    public static void setConfigData(ConfigData newConfigData) {
        configData = newConfigData;
    }

    public static class ConfigData {
        boolean renderVanillaHud = false;
        boolean showHealthIcon = true;
        boolean showArmorIcon = true;
        boolean showHungerIcon = true;
        boolean showBreatheIcon = true;
        boolean showToughnessIcon = true;
        public boolean showHealthValue = true;
        public boolean showArmorValue = true;
        public boolean showHungerValue = true;
        public boolean showOxygenValue = true;
        public boolean showToughnessValue = true;
        public boolean useStaticHealthColor = false;
        public int staticHealthColor = 0xFF0000; // default: red
        public boolean useStaticHungerColor = false;
        public int staticHungerColor = 0xFFA500; // default: orange
        int healthDisplayX = -70;
        int healthDisplayY = 43;
        int hungerDisplayX = 66;
        int hungerDisplayY = 43;
        int armorDisplayX = -70;
        int armorDisplayY = 60;
        int breatheDisplayX = 67;
        int breatheDisplayY = 60;
        int toughnessDisplayX = 30;
        int toughnessDisplayY = 0;
    }
}
