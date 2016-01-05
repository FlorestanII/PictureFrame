package me.florestanii.pictureframe;

import me.florestanii.pictureframe.listener.ChunkListener;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Set;

import javax.imageio.ImageIO;

public class PictureFrame extends JavaPlugin {
    private FileConfiguration mapConfig;
    private File mapConfigFile;
    private File scaledImagesDirectory;
    private File imagesDirectory;

    private File updateConfigFile;
    private FileConfiguration updateConfig;
    
    private ArrayList<SavedMap> loadedMaps = new ArrayList<SavedMap>();
    
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
        loadUpdates();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    public void loadUpdates(){
    	Set<String> keys = getUpdateConfig().getKeys(false);
    	
    	for(String key : keys) {
    		final ConfigurationSection section = getUpdateConfig().getConfigurationSection(key);
    		
    		getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
				
				@Override
				public void run() {

					try {
						BufferedImage updatedImage = ImageIO.read(URI.create(section.getString("updateURL")).toURL().openStream());
						getMap((short)section.getInt("map")).updateImage(updatedImage);
						getMap((short)section.getInt("map")).loadMap();
						getMap((short)section.getInt("map")).saveMap();
					} catch (MalformedURLException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
						
				}
				
			}, section.getInt("updateInterval")*20, section.getInt("updateInterval")*20);
    		
    	}
    	
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
                this.loadedMaps.add(map);
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

    public void reloadUpdateConfig() {
    	if(this.updateConfigFile == null) {
    		this.updateConfigFile = new File(getDataFolder(), "updatedFrame.yml");
    	}
    	this.updateConfig = YamlConfiguration.loadConfiguration(this.updateConfigFile);
    }
    
    public FileConfiguration getUpdateConfig(){
    	if(this.updateConfig == null) {
    		reloadUpdateConfig();
    	}
    	return updateConfig;
    }
    
    public void saveUpdateConfig() {
    	if((this.updateConfig == null) || (this.updateConfigFile == null)){
    		return;
    	}
    	try {
			getUpdateConfig().save(this.updateConfigFile);
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
    
    public ArrayList<SavedMap> getLoadedMaps(){
    	return loadedMaps;
    }
    
    public SavedMap getMap(short id){
    	for(SavedMap map : loadedMaps){
    		if(map.getId() == id){
    			return map;
    		}
    	}
    	return null;
    }
    
    public boolean isMapLoaded(short id){
    	for(SavedMap map : loadedMaps){
    		if(map.getId() == id)
    			return true;
    	}
    	return false;
    }
    
}
