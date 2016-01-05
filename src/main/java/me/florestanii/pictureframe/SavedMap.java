package me.florestanii.pictureframe;

import me.florestanii.pictureframe.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.map.MapPalette;
import org.bukkit.map.MapView;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.logging.Level;

public class SavedMap {
    private PictureFrame plugin;
    private String imgName;
    private World world;
    private short id;
    private BufferedImage image;

    public SavedMap(PictureFrame plugin, short id, BufferedImage img, World world) {
        this.plugin = plugin;
        this.id = id;
        this.image = img;
        this.imgName = ("map" + id);
        this.world = world;
    }

    public SavedMap(PictureFrame plugin, short id) {
        this.id = id;
        this.plugin = plugin;
        Set<String> keys = this.plugin.getMapConfig().getKeys(false);
        for (String key : keys) {
            if (key.equals("map" + id)) {
                ConfigurationSection section = plugin.getMapConfig().getConfigurationSection(key);

                this.imgName = section.getString("image");
                try {
                    this.image = ImageIO.read(new File(plugin.getScaledImagesDirectory(), this.imgName + ".png"));
                } catch (IOException e) {
                    plugin.getLogger().log(Level.WARNING, "Could not load map image " + imgName + ".png", e);
                }
            }
        }
    }

    public boolean saveMap() {
        this.plugin.getLogger().info("Saving map " + id);
        try {
            File outputfile = new File(plugin.getScaledImagesDirectory(), imgName + ".png");
            ImageIO.write(MapPalette.resizeImage(image), "png", outputfile);
        } catch (IOException e) {
            plugin.getLogger().log(Level.WARNING, "Could not save map image " + imgName + ".png", e);
            return false;
        }
        ConfigurationSection section = plugin.getMapConfig().createSection(imgName);
        section.set("id", id);
        section.set("image", imgName);
        plugin.saveMapConfig();
        return true;
    }

    @SuppressWarnings("deprecation")
    public boolean loadMap() {
        MapView mapView = Bukkit.getMap(id);
        if (mapView != null) {
            Util.removeAllRenderers(mapView);
            mapView.addRenderer(new ImageMapRenderer(image));
            return true;
        }
        return false;
    }
    public short getId(){
    	return id;
    }
    
    public void updateImage(BufferedImage image){
    	this.image = image;
    }
    
}
