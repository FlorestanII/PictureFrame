package me.florestanii.pictureframe;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class MapHandler implements Runnable {
    private final List<ItemStack> renderedMaps;
    private final Player player;
    private final PictureFrame plugin;
    private final String path;
    private final int width;
    private final int height;
    private Callback callback;

    public MapHandler(Player player, String path, int width, int height, PictureFrame plugin) {
        this.renderedMaps = new ArrayList<>(width * height);
        this.player = player;
        this.plugin = plugin;
        this.path = path;
        this.width = width;
        this.height = height;
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
                    poster = createPoster();
                } catch (IOException e) {
                    if (callback != null) {
                        callback.posterFailed(new Exception("Creating the poster failed", e));
                    }
                    return;
                }

                plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                    @Override
                    @SuppressWarnings("deprecation")
                    public void run() {
                        poster.sendTo(player);
                        callback.posterReady(poster);
                    }
                });
            }
        });
    }

    private Poster createPoster() throws IOException {
        URI source;
        if (path.startsWith("http://") || path.startsWith("https://")) {
            source = URI.create(path);
        } else {
            source = new File(plugin.getImagesDirectory(), path).toURI();
        }
        return Poster.create(source.toURL(), width, height);
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public interface Callback {
        void posterReady(Poster poster);

        void posterFailed(Throwable exception);
    }
}
