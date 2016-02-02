package me.florestanii.pictureframe;

import me.florestanii.pictureframe.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.map.MapView;

import java.awt.image.BufferedImage;

/**
 * A single map of a poster.
 */
public class SavedMap {
    private final short id;
    private BufferedImage image;

    public SavedMap(short id) {
        this.id = id;
    }

    @SuppressWarnings("deprecation")
    private boolean updateMapRenderer() {
        MapView mapView = Bukkit.getMap(id);
        if (mapView != null) {
            Util.removeAllRenderers(mapView);
            if (image != null) {
                mapView.addRenderer(new ImageMapRenderer(image));
            }
            return true;
        }
        return false;
    }

    public short getId() {
        return id;
    }

    public boolean setImage(BufferedImage image) {
        this.image = image;
        return updateMapRenderer();
    }
}
