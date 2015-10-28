package me.florestanii.pictureframe;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Set;

import javax.imageio.ImageIO;

import me.florestanii.pictureframe.util.Util;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.map.MapPalette;
import org.bukkit.map.MapView;

public class SavedMap {
    PictureFrame plugin;
    String imgName;
    World world;
    short id;
    BufferedImage image;

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
        int tmp = 0;
        for (String key : keys) {
            ConfigurationSection section = plugin.getMapConfig().getConfigurationSection(key);
            if (section.contains(id + "")) {
                tmp++;

                this.imgName = section.getString("image");
                try {
                    this.image = ImageIO.read(new File(Util.scaledImages, this.imgName + ".png"));
                } catch (IOException e) {
                    System.out.println("Image " + this.imgName + ".png doesn't exists in Image directory.");
                }
            }
        }
        if (tmp == 0) {
            System.out.println("No map was loaded");
        }
    }

    public boolean saveMap() {
        this.plugin.getLogger().info("Saving map " + id);
        try {
            File outputfile = new File(Util.scaledImages, imgName + ".png");
            if (!outputfile.exists())
                outputfile.mkdirs();
            ImageIO.write(MapPalette.resizeImage(image), "png", outputfile);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        ConfigurationSection section = plugin.getMapConfig().createSection(imgName);
        section.set("id", id);
        section.set("world", world.getName());
        section.set("image", imgName);
        plugin.saveMapConfig();
        return true;
    }

    @SuppressWarnings("deprecation")
    public boolean loadMap() {

        MapView mapView = Bukkit.getMap(id);
        if (mapView != null) {
            ImageRendererThread.removeRenderer(mapView);
            mapView.addRenderer(new ImageMapRenderer(image));
            return true;
        }
        return false;
    }
}
