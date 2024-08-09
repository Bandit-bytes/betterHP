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

    // Static instance of configData to hold the current configuration
    private static ConfigData configData;

    // Timestamp for last modification to avoid rapid reloading
    private static long lastModified = 0;

    static {
        loadConfig(); // Load the config on startup
        watchConfigFile(); // Watch for changes
    }

    // Method to load the configuration
    public static void loadConfig() {
        if (!CONFIG_FILE.exists()) {
            System.out.println("Configuration file does not exist. Creating new config with default values.");
            configData = new ConfigData(); // Initialize with default values
            saveConfig(); // Save default config if file doesn't exist
        } else {
            try (FileReader reader = new FileReader(CONFIG_FILE)) {
                configData = GSON.fromJson(reader, ConfigData.class);
                if (configData == null) {
                    System.out.println("Configuration file is empty or corrupted. Reverting to default values.");
                    configData = new ConfigData(); // Ensure configData is not null
                }
                System.out.println("Configuration loaded from " + CONFIG_FILE.getName());
            } catch (IOException e) {
                System.err.println("Error reading configuration file: " + e.getMessage());
                configData = new ConfigData(); // Default to in-memory defaults on error
            }
        }
    }

    // Method to save the configuration
    public static void saveConfig() {
        try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
            GSON.toJson(configData, writer);
            lastModified = CONFIG_FILE.lastModified(); // Update last modified time
            System.out.println("Configuration saved to " + CONFIG_FILE.getName());
        } catch (IOException e) {
            System.err.println("Error saving configuration file: " + e.getMessage());
        }
    }

    // Watch the config file for changes and reload the config when modified
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
    public static boolean showNumericHunger() { return configData != null && configData.showNumericHunger; }
    public static boolean showNumericOxygen() { return configData != null && configData.showNumericOxygen; }
    public static int healthDisplayX() { return configData != null ? configData.healthDisplayX : -76; }
    public static int healthDisplayY() { return configData != null ? configData.healthDisplayY : 44; }
    public static int hungerDisplayX() { return configData != null ? configData.hungerDisplayX : 24; }
    public static int hungerDisplayY() { return configData != null ? configData.hungerDisplayY : 46; }
    public static int armorDisplayX() { return configData != null ? configData.armorDisplayX : -8; }
    public static int armorDisplayY() { return configData != null ? configData.armorDisplayY : 56; }
    public static int breatheDisplayX() { return configData != null ? configData.breatheDisplayX : 24; }
    public static int breatheDisplayY() { return configData != null ? configData.breatheDisplayY : 60; }

    // Inner class to hold the configuration data
    private static class ConfigData {
        boolean renderVanillaHud = false; // New setting for rendering the vanilla HUD
        boolean showHealthIcon = true;
        boolean showArmorIcon = true;
        boolean showHungerIcon = true;
        boolean showBreatheIcon = true;
        boolean showNumericHunger = true;
        boolean showNumericOxygen = true;
        int healthDisplayX = -70;
        int healthDisplayY = 43;
        int hungerDisplayX = 66;
        int hungerDisplayY = 43;
        int armorDisplayX = -70;
        int armorDisplayY = 60;
        int breatheDisplayX = 67;
        int breatheDisplayY = 60;
    }
}
