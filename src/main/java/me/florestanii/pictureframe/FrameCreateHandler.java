package me.florestanii.pictureframe;

import java.awt.image.BufferedImage;

import me.florestanii.pictureframe.util.Cache;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Rotation;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class FrameCreateHandler implements Listener {

    public FrameCreateHandler(PictureFrame plugin) {
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityInteract(PlayerInteractEntityEvent e) {
        if (e.isCancelled() || e.getRightClicked().getType() != EntityType.ITEM_FRAME)
            return;

        Player p = e.getPlayer();
        ItemFrame item = (ItemFrame) e.getRightClicked();

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

            item.setItem(map);
            item.setRotation(Rotation.NONE);
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
                item.setItem(mapHandler.getRenderedMapsAsItemStacks().get(0));
                Cache.removeCacheMultiCreating(p);
                e.setCancelled(true);
                return;
            }
            BufferedImage srcImg = mapHandler.getRendererThread().getSrcImg();
            int columnCount = srcImg.getWidth() % 128 == 0 ? ((int) (srcImg.getWidth() / 128)) : ((int) (srcImg.getWidth() / 128)) + 1;
            int rowCount = srcImg.getHeight() % 128 == 0 ? ((int) (srcImg.getHeight() / 128)) : ((int) (srcImg.getHeight() / 128)) + 1;

            int i = 0;
            for (int column = 1; column <= columnCount; column++) {
                for (int row = 1; row <= rowCount; row++) {
                    ItemStack itemStack = mapHandler.getRenderedMapsAsItemStacks().get(i);
                    ItemMeta meta = itemStack.getItemMeta();
                    
                    meta.setDisplayName("");
                    itemStack.setItemMeta(meta);

                    if (column == 1 && row == 1)
                        item.setItem(itemStack);
                    item.setRotation(Rotation.NONE);

                    Location loc;

                    switch (item.getFacing()) {
                    case EAST:

                        loc = new Location(item.getWorld(), item.getLocation().getX(), item.getLocation().getY() - (row - 1), item.getLocation().getZ() - (column - 1));

                        for (Entity entity : item.getNearbyEntities(0, row, column)) {
                            if (!(entity instanceof ItemFrame))
                                continue;
                            ItemFrame nextItemFrame = (ItemFrame) entity;

                            if (nextItemFrame.getLocation().distanceSquared(loc) <= 0.0001) {
                                nextItemFrame.setItem(itemStack);
                                nextItemFrame.setRotation(Rotation.NONE);
                            }

                        }

                        break;

                    case WEST:

                        loc = new Location(item.getWorld(), item.getLocation().getX(), item.getLocation().getY() - (row - 1), item.getLocation().getZ() + (column - 1));

                        for (Entity entity : item.getNearbyEntities(0, row, column)) {
                            if (!(entity instanceof ItemFrame))
                                continue;
                            ItemFrame nextItemFrame = (ItemFrame) entity;

                            if (nextItemFrame.getLocation().distanceSquared(loc) <= 0.0001) {
                                nextItemFrame.setItem(itemStack);
                                nextItemFrame.setRotation(Rotation.NONE);
                            }

                        }

                        break;

                    case NORTH:

                        loc = new Location(item.getWorld(), item.getLocation().getX() - (column - 1), item.getLocation().getY() - (row - 1), item.getLocation().getZ());

                        for (Entity entity : item.getNearbyEntities(column, row, 0)) {
                            if (!(entity instanceof ItemFrame))
                                continue;
                            ItemFrame nextItemFrame = (ItemFrame) entity;

                            if (nextItemFrame.getLocation().distanceSquared(loc) <= 0.0001) {
                                nextItemFrame.setItem(itemStack);
                                nextItemFrame.setRotation(Rotation.NONE);
                            }

                        }

                        break;

                    case SOUTH:

                        loc = new Location(item.getWorld(), item.getLocation().getX() + (column - 1), item.getLocation().getY() - (row - 1), item.getLocation().getZ());

                        for (Entity entity : item.getNearbyEntities(column, row, 0)) {
                            if (!(entity instanceof ItemFrame))
                                continue;
                            ItemFrame nextItemFrame = (ItemFrame) entity;

                            if (nextItemFrame.getLocation().distanceSquared(loc) <= 0.0001) {
                                nextItemFrame.setItem(itemStack);
                                nextItemFrame.setRotation(Rotation.NONE);
                            }

                        }

                        break;

                    default:
                        break;
                    }

                    i++;
                }
            }
            e.setCancelled(true);
            Cache.removeCacheMultiCreating(p);

        }
    }

}
