package me.florestanii.pictureframe;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.map.MapView;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Poster {
    private URL source;
    private SavedMap[] maps;
    private int width;
    private int height;

    /**
     * Creates a new poster.
     *
     * @param mapIds IDs of the maps of this poster
     * @param width  width in blocks
     * @param height height in blocks
     */
    public Poster(List<Short> mapIds, int width, int height) {
        maps = new SavedMap[width * height];
        int i = 0;
        for (Short id : mapIds) {
            maps[i] = new SavedMap(id);
            i++;
        }
        this.width = width;
        this.height = height;
    }

    private Poster(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public static Poster create(URL source, int width, int height) throws IOException {
        Poster poster = new Poster(width, height);
        poster.source = source;
        SavedMap[] maps = new SavedMap[width * height];
        int i = 0;
        for (BufferedImage image : splitImage(ImageIO.read(source), width, height)) {
            MapView mapView = Bukkit.getServer().createMap(Bukkit.getWorlds().get(0));
            SavedMap map = new SavedMap(mapView.getId());
            map.setImage(image);

            maps[i] = map;
            i++;
        }
        poster.maps = maps;
        return poster;
    }

    public void setImage(URL source) throws IOException {
        this.source = source;
        updateMaps();
    }

    public void reload() throws IOException {
        updateMaps();
    }

    public void sendTo(Player player) {
        for (SavedMap map : maps) {
            player.sendMap(Bukkit.getServer().getMap(map.getId()));
        }
    }

    public List<ItemStack> getMaps() {
        List<ItemStack> maps = new ArrayList<>();
        for (SavedMap map : this.maps) {
            maps.add(new ItemStack(Material.MAP, 1, map.getId()));
        }
        return maps;
    }

    /**
     * Gets the source of this poster.
     *
     * @return source of this poster
     */
    public URL getSource() {
        return source;
    }

    /**
     * Gets the width of this poster, in blocks.
     *
     * @return width of this poster, in blocks
     */
    public int getWidth() {
        return width;
    }

    /**
     * Gets the height of this poster, in blocks.
     *
     * @return height of this poster, in blocks
     */
    public int getHeight() {
        return height;
    }

    /**
     * Gets the IDs of all maps of this poster.
     *
     * @return IDs of all maps of this poster
     */
    public List<Short> getMapIds() {
        List<Short> ids = new ArrayList<>(maps.length);
        for (SavedMap map : maps) {
            ids.add(map.getId());
        }
        return ids;
    }

    private void updateMaps() throws IOException {
        int i = 0;
        for (BufferedImage image : splitImage(ImageIO.read(source), width, height)) {
            maps[i].setImage(image);
            i++;
        }
    }

    private static BufferedImage[] splitImage(BufferedImage img, int width, int height) {
        //one map is 128 x 128, so we need to scale the image to (width * 128) x (height * 128)
        img = scaleImage(img, width * 128, height * 128);

        BufferedImage[] images = new BufferedImage[width * height];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                images[y * width + x] = img.getSubimage(x * 128, y * 128, 128, 128);
            }
        }

        return images;
    }

    private static BufferedImage scaleImage(final BufferedImage img, final int width, final int height) {
        double ratio = img.getWidth() / (double) img.getHeight();

        int actualWidth = (int) (ratio * height);
        int actualHeight;
        if (actualWidth < width) {
            actualHeight = (int) (width / ratio);
            actualWidth = width;
        } else {
            actualHeight = height;
        }

        int destX = (width - actualWidth) / 2;
        int destY = (height - actualHeight) / 2;
        BufferedImage newImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics g = newImage.getGraphics();
        g.drawImage(img, destX, destY, destX + actualWidth, destY + actualHeight, 0, 0, img.getWidth(), img.getHeight(), null);
        g.dispose();

        return newImage;
    }

    public boolean containsMap(short id) {
        for (SavedMap map : maps) {
            if (map.getId() == id) {
                return true;
            }
        }
        return false;
    }
}
