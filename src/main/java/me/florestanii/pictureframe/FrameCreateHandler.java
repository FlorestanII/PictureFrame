package me.florestanii.pictureframe;

import me.florestanii.pictureframe.util.Cache;
import me.florestanii.pictureframe.util.Util;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class FrameCreateHandler implements Listener {

    public FrameCreateHandler(PictureFrame plugin) {
    }

    @EventHandler
    public void onBlockInteract(PlayerInteractEvent e) {
        if (e.isCancelled() || !e.hasBlock() || e.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        Player p = e.getPlayer();
        final Block topLeftBlock = e.getClickedBlock();
        final BlockFace facing = e.getBlockFace();

        if (Cache.hasCacheCreating(p)) {
            if (!p.hasPermission("pictureframe.create")) {
                p.sendMessage(ChatColor.DARK_RED + "You don't have enough permissions to use that command!");
                return;
            }

            MapHandler tache = Cache.getCacheCreating(p).getMapHandler();
            if (!tache.isReady()) {
                p.sendMessage("Please wait a moment, the picture isn't ready.");
                e.setCancelled(true);
                return;
            }
            ItemStack map = tache.getRenderedMapsAsItemStacks().get(0);
            ItemMeta meta = map.getItemMeta();
            meta.setDisplayName("");
            map.setItemMeta(meta);

            Util.attachItemFrame(topLeftBlock, map, facing);
            Cache.removeCacheCreating(p);
            e.setCancelled(true);
        } else if (Cache.hasCacheMultiCreating(p)) {
            if (!p.hasPermission("pictureframe.multicreate")) {
                p.sendMessage(ChatColor.DARK_RED + "You don't have enough permissions to use that command!");
                return;
            }

            MapHandler mapHandler = Cache.getCacheMultiCreating(p).getMapHandler();

            if (!mapHandler.isReady()) {
                p.sendMessage("Please wait a moment, the picture isn't ready.");
                e.setCancelled(true);
                return;
            }

            if (mapHandler.getRenderedMapsAsItemStacks().size() == 1) {
                Util.attachItemFrame(topLeftBlock, mapHandler.getRenderedMapsAsItemStacks().get(0), facing);
                Cache.removeCacheMultiCreating(p);
                e.setCancelled(true);
                return;
            }
            Poster poster = mapHandler.getPoster();
            for (int x = 0; x < poster.getWidth(); x++) {
                for (int y = 0; y < poster.getHeight(); y++) {
                    System.out.println("x=" + x + ", y=" + y);
                    Block block = Util.getRelative(topLeftBlock, facing, -y, -x, 0);
                    ItemStack map = mapHandler.getRenderedMapsAsItemStacks().get(y * poster.getWidth() + x);
                    ItemMeta meta = map.getItemMeta();
                    meta.setDisplayName("");
                    map.setItemMeta(meta);
                    Util.attachItemFrame(block, map, facing);
                }
            }
            e.setCancelled(true);
            Cache.removeCacheMultiCreating(p);
        }
    }
}
