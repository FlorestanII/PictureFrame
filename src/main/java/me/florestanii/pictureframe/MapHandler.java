package me.florestanii.pictureframe;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.map.MapView;
import org.bukkit.scheduler.BukkitRunnable;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class MapHandler extends BukkitRunnable {
    private final ArrayList<ItemStack> renderedMaps = new ArrayList<ItemStack>();

    private int i;
    private final Player player;
    private ImageRendererThread imageRendererThread;
    private final PictureFrame plugin;

    private boolean ready = false;

    public MapHandler(Player player, String path, int width, int height, PictureFrame plugin) {
        this.i = 0;
        this.player = player;
        this.imageRendererThread = new ImageRendererThread(path, width, height);
        this.imageRendererThread.start();
        this.plugin = plugin;
    }

    public ImageRendererThread getRendererThread() {
        return imageRendererThread;
    }

    @SuppressWarnings("deprecation")
    public void run() {
        if (!imageRendererThread.getStatus()) {
            i++;
            if ((imageRendererThread.isErreur()) || (this.i > 42)) {
                player.sendMessage("TIMEOUT: the render took too many time");
                cancel();
            }
        } else {
            cancel();
            BufferedImage[] images = imageRendererThread.getPoster().geImages();

            ItemStack map;
            for (int i = 0; i < images.length; i++) {
                MapView mapView;

                mapView = plugin.getServer().createMap(player.getWorld());

                ImageRendererThread.removeRenderer(mapView);
                mapView.addRenderer(new ImageMapRenderer(images[i]));
                map = new ItemStack(Material.MAP, 1, mapView.getId());

                renderedMaps.add(map);

                SavedMap svg = new SavedMap(plugin, mapView.getId(), images[i], player.getWorld());
                svg.saveMap();
                player.sendMap(mapView);
            }

            ready = true;
        }
    }

    public ArrayList<ItemStack> getRenderedMapsAsItemStacks() {
        return renderedMaps;
    }

    public boolean isReady() {
        return ready;
    }

    public Poster getPoster() {
        return imageRendererThread.getPoster();
    }
}
