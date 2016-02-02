package me.florestanii.pictureframe;

import me.florestanii.pictureframe.listener.ChunkListener;
import me.florestanii.pictureframe.listener.ProtectionListener;
import me.florestanii.pictureframe.util.Util;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class PictureFrame extends JavaPlugin {
    private final File mapConfigFile = new File(getDataFolder(), "posters.yml");
    private final File imagesDirectory = new File(getDataFolder(), "images");
    private final List<Poster> posters = new ArrayList<>();

    @Override
    public void onEnable() {
        posters.clear();

        if (!imagesDirectory.exists() && !imagesDirectory.mkdirs()) {
            getLogger().severe("No images directory found (and it couldn't be created automatically).");
            setEnabled(false);
            return;
        }

        getCommand("pictureframe").setExecutor(new PictureFrameCommand(this));
        getServer().getPluginManager().registerEvents(new ChunkListener(this), this);
        getServer().getPluginManager().registerEvents(new ProtectionListener(this), this);

        saveDefaultConfig();
        loadPosters();
    }

    @Override
    public void onDisable() {
        saveMapConfig();
        super.onDisable();
    }

    public void loadPosters() {
        ConfigurationSection posterConfig = YamlConfiguration.loadConfiguration(mapConfigFile);
        int loadedMaps = 0;
        int failedMaps = 0;
        for (ConfigurationSection section : Util.getConfigList(posterConfig, "posters")) {
            Poster poster = new Poster(section.getShortList("maps"), section.getInt("width"), section.getInt("height"));
            try {
                poster.setImage(new URL(section.getString("source")));
                posters.add(poster);
                registerUpdates(poster);
            } catch (IOException e) {
                failedMaps++;
            }
        }
        getLogger().info(loadedMaps + " posters loaded");
        if (failedMaps != 0) {
            getLogger().info(failedMaps + " posters can't be loaded");
        }
    }

    public void saveMapConfig() {
        List<Object> posterConfigs = new ArrayList<>();

        for (Poster poster : posters) {
            MemoryConfiguration posterConfig = new MemoryConfiguration();
            posterConfig.set("width", poster.getWidth());
            posterConfig.set("height", poster.getHeight());
            posterConfig.set("source", poster.getSource().toString());
            posterConfig.set("maps", poster.getMapIds());
            posterConfigs.add(posterConfig.get(""));
        }

        FileConfiguration config = new YamlConfiguration();
        config.getRoot().set("posters", posterConfigs);

        try {
            config.save(mapConfigFile);
        } catch (IOException e) {
            getLogger().log(Level.SEVERE, "Could not save the posters", e);
        }
    }

    public File getImagesDirectory() {
        return imagesDirectory;
    }

    public boolean isMapLoaded(short id) {
        for (Poster poster : posters) {
            if (poster.containsMap(id)) {
                return true;
            }
        }
        return false;
    }

    public void addPoster(Poster poster) {
        posters.add(poster);
        registerUpdates(poster);
        saveMapConfig();
    }

    private void registerUpdates(final Poster poster) {
        getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {

            @Override
            public void run() {
                try {
                    poster.reload();
                } catch (IOException e) {
                    getLogger().warning("Could not reload poster from " + poster.getSource());
                }
            }

        }, getConfig().getInt("updateInterval", 60 * 60) * 20, getConfig().getInt("updateInterval", 60 * 60) * 20);
    }
}
