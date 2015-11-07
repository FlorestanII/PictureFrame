package me.florestanii.pictureframe;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.map.MapView;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MapHandler implements Runnable {
    private final List<ItemStack> renderedMaps;
    private final Player player;
    private final ImageRendererThread imageRendererThread;
    private final PictureFrame plugin;
    private Callback callback;

    public MapHandler(Player player, String path, int width, int height, PictureFrame plugin) {
        this.renderedMaps = new ArrayList<>(width * height);
        this.player = player;
        this.imageRendererThread = new ImageRendererThread(path, width, height);
        this.plugin = plugin;
    }

    /**
     * Asynchronously creates the images for the maps, then synchronously creates the map items and calls the callback.
     */
    public void run() {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                final Poster poster;
                try {
                    poster = imageRendererThread.createPoster();
                } catch (IOException e) {
                    if (callback != null) {
                        callback.posterFailed(new Exception("Creating the poster failed", e));
                    }
                    return;
                }

                final BufferedImage[] images = poster.getImages();

                plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                    @Override
                    @SuppressWarnings("deprecation")
                    public void run() {
                        ItemStack map;
                        for (BufferedImage image : images) {
                            MapView mapView;

                            mapView = plugin.getServer().createMap(player.getWorld());

                            ImageRendererThread.removeRenderer(mapView);
                            mapView.addRenderer(new ImageMapRenderer(image));
                            map = new ItemStack(Material.MAP, 1, mapView.getId());

                            renderedMaps.add(map);

                            SavedMap svg = new SavedMap(plugin, mapView.getId(), image, player.getWorld());
                            svg.saveMap();
                            player.sendMap(mapView);
                        }

                        if (callback != null) {
                            callback.posterReady(poster, renderedMaps);
                        }
                    }
                });
            }
        });
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public interface Callback {
        void posterReady(Poster poster, List<ItemStack> maps);

        void posterFailed(Throwable exception);
    }
}
