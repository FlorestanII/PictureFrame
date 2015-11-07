package me.florestanii.pictureframe.util;

import me.florestanii.pictureframe.PictureFrame;
import org.bukkit.map.MapView;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Set;

public class Util {
    public static File scaledImages = new File(PictureFrame.getPlugin().getDataFolder(), "scaledimages");

    public static boolean createScaledImagesDir(PictureFrame plugin) {
        if (!scaledImages.exists()) {
            return scaledImages.mkdirs();
        }
        return true;
    }

    public static int getCountOfMaps() {
        Set<String> keys = PictureFrame.getPlugin().getMapConfig().getKeys(false);
        return keys.size();
    }

    public static boolean isInConfig(short id) {
        return PictureFrame.getPlugin().getMapConfig().contains(id + "");
    }

    @SuppressWarnings("deprecation")
    public static MapView getMap(short id) {
        if (!isInConfig(id)) {
            return null;
        }
        MapView map = PictureFrame.getPlugin().getServer().getMap(id);
        if (map == null) {
            PictureFrame.getPlugin().getLogger().warning("Map #" + id + " exists in maps.yml but not in the world folder !");
            return null;
        }
        return map;
    }

    public static BufferedImage toBufferedImage(Image img) {
        if (img instanceof BufferedImage) {
            return (BufferedImage) img;
        }

        BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);

        Graphics2D bGr = bimage.createGraphics();
        bGr.drawImage(img, 0, 0, null);
        bGr.dispose();

        return bimage;
    }
}
