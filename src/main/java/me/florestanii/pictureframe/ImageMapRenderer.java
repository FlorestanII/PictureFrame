package me.florestanii.pictureframe;

import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

import java.awt.*;

public class ImageMapRenderer extends MapRenderer implements Runnable {
    private boolean ready;
    private Image img;
    public MapCanvas canvas;

    public ImageMapRenderer(Image img) {
        this.ready = false;
        this.img = img;
    }

    @Override
    public void render(MapView v, MapCanvas canavas, Player p) {
        this.canvas = canavas;
        if (!this.ready) {
            Thread renderThread = new Thread(this);
            renderThread.setDaemon(true);
            renderThread.start();
        }
    }

    @Override
    public void run() {
        this.canvas.drawImage(0, 0, this.img);
        this.ready = true;
    }
}
