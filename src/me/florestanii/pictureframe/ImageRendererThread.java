package me.florestanii.pictureframe;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;

import javax.imageio.ImageIO;

import org.bukkit.map.MapView;

public class ImageRendererThread extends Thread {
    private String path;
    private BufferedImage[] img;
    private Poster poster;
    private boolean ready = false;
    private boolean resized;
    boolean failed = false;

    private BufferedImage srcImg;
    
    public boolean isErreur() {
        return this.failed;
    }

    public ImageRendererThread(String path, boolean resized) {
        this.path = path;
        this.resized = resized;
    }

    public BufferedImage[] getImg() {
        if (this.ready) {
            return this.img;
        }
        return null;
    }

    public HashMap<Integer, String> getNumberMap() {
        return poster.getNumberMap();
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
            if (this.resized) {
                this.img = new BufferedImage[1];
                Image i = imgSrc.getScaledInstance(128, 128, 4);
                BufferedImage imgScaled = new BufferedImage(128, 128, 2);
                imgScaled.getGraphics().drawImage(i, 0, 0, null);
                this.img[0] = imgScaled;
            } else {
                int width = imgSrc.getWidth();
                int height = imgSrc.getHeight();

                int tmpW = 0;
                int tmpH = 0;
                int i = 1;
                while (tmpW < width) {
                    tmpW = i * 128;
                    i++;
                }
                i = 0;
                while (tmpH < height) {
                    tmpH = i * 128;
                    i++;
                }
                BufferedImage canvas = new BufferedImage(tmpW, tmpH, 2);

                Graphics2D graph = canvas.createGraphics();

                int centerX = (tmpW - imgSrc.getWidth()) / 2;
                int centerY = (tmpH - imgSrc.getHeight()) / 2;
                
                graph.translate(centerX, centerY);

                graph.drawImage(imgSrc, null, null);

                this.poster = new Poster(canvas);
                this.img = this.poster.getPoster();
            }
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
    public BufferedImage getSrcImg(){
        return srcImg;
    }
}
