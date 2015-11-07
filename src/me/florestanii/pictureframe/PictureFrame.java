package me.florestanii.pictureframe;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import me.florestanii.pictureframe.listener.ChunkListener;
import me.florestanii.pictureframe.util.Util;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class PictureFrame extends JavaPlugin {

    private FileConfiguration mapConfig = null;
    private File mapConfigFile = null;

    private static PictureFrame instance;

    @Override
    public void onLoad() {
        instance = this;
    }

    @Override
    public void onEnable() {

        getCommand("pictureframe").setExecutor(new PictureFrameCommand(this));

        getServer().getPluginManager().registerEvents(new FrameCreateHandler(this), this);
        getServer().getPluginManager().registerEvents(new ChunkListener(this), this);
        
        if (Util.createScaledImagesDir(this)) {
            saveDefaultConfig();
            loadMap();
        } else {
            setEnabled(false);
        }
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

    public static PictureFrame getPlugin() {
        return instance;
    }

}
