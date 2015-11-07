package me.florestanii.pictureframe;

import org.bukkit.map.MapView;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URI;

public class ImageRendererThread extends Thread {
    private final String path;
    private final int width;
    private final int height;
    private Poster poster;
    private boolean ready = false;
    private boolean failed = false;

    private BufferedImage srcImg;

    public boolean isErreur() {
        return this.failed;
    }

    public ImageRendererThread(String path, int width, int height) {
        this.path = path;
        this.width = width;
        this.height = height;
    }

    public Poster getPoster() {
        return poster;
    }

    public boolean getStatus() {
        return ready;
    }

    public void run() {
        BufferedImage imgSrc;
        new File(PictureFrame.getPlugin().getDataFolder(), "/images/").mkdirs();
        try {
            try {
                imgSrc = ImageIO.read(URI.create(this.path).toURL().openStream());
            } catch (Exception e) {
                imgSrc = ImageIO.read(new File(PictureFrame.getPlugin().getDataFolder(), "/images/" + path));
            }
            srcImg = imgSrc;
            this.poster = new Poster(srcImg, width, height);
        } catch (IOException e) {
            e.printStackTrace();
            this.failed = true;
        }
        this.ready = true;
    }

    public static void removeRenderer(MapView map) {
        for (int i = 0; i < map.getRenderers().size(); i++) {
            map.removeRenderer(map.getRenderers().get(i));
        }
    }

    public BufferedImage getSrcImg() {
        return srcImg;
    }
}
