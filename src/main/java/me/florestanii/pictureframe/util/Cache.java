package me.florestanii.pictureframe.util;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.entity.Player;

public class Cache {

    private static HashMap<Player, CacheFrameCreating> createCache = new HashMap<Player, CacheFrameCreating>();
    private static HashMap<Player, CacheFrameCreating> createMultiCache = new HashMap<Player, CacheFrameCreating>();
    private static ArrayList<Player> removeCache = new ArrayList<Player>();

    public static boolean hasCacheCreating(Player player) {
        return createCache.containsKey(player);
    }

    public static CacheFrameCreating getCacheCreating(Player player) {
        return createCache.get(player);
    }

    public static void setCacheCreating(Player player, CacheFrameCreating cache) {
        createCache.put(player, cache);
        createMultiCache.remove(player);
        removeCache.remove(player);
    }

    public static void removeCacheCreating(Player player) {
        createCache.remove(player);
    }

    public static boolean hasCacheMultiCreating(Player player) {
        return createMultiCache.containsKey(player);
    }

    public static CacheFrameCreating getCacheMultiCreating(Player player) {
        return createMultiCache.get(player);
    }

    public static void setCacheMultiCreating(Player player, CacheFrameCreating path) {
        createCache.remove(player);
        createMultiCache.put(player, path);
        removeCache.remove(player);
    }

    public static void removeCacheMultiCreating(Player player) {
        createMultiCache.remove(player);
    }

}
