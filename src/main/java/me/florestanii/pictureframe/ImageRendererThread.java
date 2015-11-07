package me.florestanii.pictureframe;

import org.bukkit.map.MapView;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URI;

public class ImageRendererThread {
    private final String path;
    private final int width;
    private final int height;

    public ImageRendererThread(String path, int width, int height) {
        this.path = path;
        this.width = width;
        this.height = height;
    }

    public Poster createPoster() throws IOException {
        BufferedImage imgSrc;
        new File(PictureFrame.getPlugin().getDataFolder(), "/images/").mkdirs();
        try {
            imgSrc = ImageIO.read(URI.create(this.path).toURL().openStream());
        } catch (Exception e) {
            imgSrc = ImageIO.read(new File(PictureFrame.getPlugin().getDataFolder(), "/images/" + path));
        }
        return new Poster(imgSrc, width, height);
    }

    public static void removeRenderer(MapView map) {
        for (int i = 0; i < map.getRenderers().size(); i++) {
            map.removeRenderer(map.getRenderers().get(i));
        }
    }
}
