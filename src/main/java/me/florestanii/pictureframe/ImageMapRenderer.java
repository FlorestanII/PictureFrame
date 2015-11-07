package me.florestanii.pictureframe;

import java.awt.Image;
import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

public class ImageMapRenderer extends MapRenderer implements Runnable {
    boolean ready;
    Image img;
    private Thread renderThread;
    public MapCanvas canvas;

    public ImageMapRenderer(Image img) {
        this.ready = false;
        this.img = img;
    }

    @Override
    public void render(MapView v, MapCanvas canavas, Player p) {
        this.canvas = canavas;
        if (!this.ready) {
            this.renderThread = new Thread(this);
            this.renderThread.start();
        }
    }

    @Override
    public void run() {
        this.canvas.drawImage(0, 0, this.img);
        this.ready = true;
    }
}
