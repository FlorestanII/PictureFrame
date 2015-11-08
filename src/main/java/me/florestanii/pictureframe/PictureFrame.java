package me.florestanii.pictureframe;

import me.florestanii.pictureframe.listener.ChunkListener;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.Set;

public class PictureFrame extends JavaPlugin {
    private FileConfiguration mapConfig;
    private File mapConfigFile;
    private File scaledImagesDirectory;
    private File imagesDirectory;

    @Override
    public void onEnable() {
        scaledImagesDirectory = new File(getDataFolder(), "scaledimages");
        if (!scaledImagesDirectory.exists() && !scaledImagesDirectory.mkdirs()) {
            getLogger().severe("No scaled images directory found (and it couldn't be created automatically).");
            setEnabled(false);
            return;
        }

        imagesDirectory = new File(getDataFolder(), "images");
        if (!imagesDirectory.exists() && !imagesDirectory.mkdirs()) {
            getLogger().severe("No images directory found (and it couldn't be created automatically).");
            setEnabled(false);
            return;
        }

        getCommand("pictureframe").setExecutor(new PictureFrameCommand(this));
        getServer().getPluginManager().registerEvents(new ChunkListener(this), this);

        saveDefaultConfig();
        loadMap();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    public void loadMap() {
        Set<String> keys = getMapConfig().getKeys(false);
        int loadedMaps = 0;
        int failedMaps = 0;
        for (String key : keys) {
            ConfigurationSection section = getMapConfig().getConfigurationSection(key);
            SavedMap map = new SavedMap(this, Short.valueOf(section.getString("id")));
            if (map.loadMap()) {
                loadedMaps++;
            } else {
                failedMaps++;
            }

        }
        getLogger().info(loadedMaps + " maps was loaded");
        if (failedMaps != 0) {
            getLogger().info(failedMaps + " maps can't be loaded");
        }
    }

    public void reloadMapConfig() {
        if (this.mapConfigFile == null) {
            this.mapConfigFile = new File(getDataFolder(), "map.yml");
        }
        this.mapConfig = YamlConfiguration.loadConfiguration(this.mapConfigFile);
    }

    public FileConfiguration getMapConfig() {
        if (this.mapConfig == null) {
            reloadMapConfig();
        }
        return this.mapConfig;
    }

    public void saveMapConfig() {
        if ((this.mapConfig == null) || (this.mapConfigFile == null)) {
            return;
        }
        try {
            getMapConfig().save(this.mapConfigFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public File getScaledImagesDirectory() {
        return scaledImagesDirectory;
    }

    public File getImagesDirectory() {
        return imagesDirectory;
    }
}
