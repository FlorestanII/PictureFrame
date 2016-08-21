package me.florestanii.pictureframe.listener;

import me.florestanii.pictureframe.PictureFrame;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;

public class ProtectionListener implements Listener {
    private final PictureFrame plugin;

    public ProtectionListener(PictureFrame plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onEntityInteract(PlayerInteractEntityEvent event) {
        if (event.getRightClicked().getType() != EntityType.ITEM_FRAME) {
            return;
        }

        ItemFrame itemFrame = (ItemFrame) event.getRightClicked();

        if (itemFrame.getItem() == null || itemFrame.getItem().getType() != Material.MAP || !plugin.isMapLoaded(itemFrame.getItem().getDurability())) {
            return;
        }

        if (!event.getPlayer().hasPermission("pictureframe.interact")) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerAttackEntity(EntityDamageByEntityEvent event) {
        if (event.getEntityType() != EntityType.ITEM_FRAME) {
            return;
        }

        ItemFrame itemFrame = (ItemFrame) event.getEntity();

        if (itemFrame.getItem() == null || itemFrame.getItem().getType() != Material.MAP || !plugin.isMapLoaded(itemFrame.getItem().getDurability())) {
            return;
        }

        if (!(event.getDamager() instanceof Player)) {
            event.setCancelled(true);
            return;
        }

        Player p = (Player) event.getDamager();

        if (!p.hasPermission("pictureframe.destroy")) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onHangingBreak(HangingBreakEvent event) {
        if (event.getEntity().getType() != EntityType.ITEM_FRAME) {
            return;
        }

        ItemFrame itemFrame = (ItemFrame) event.getEntity();

        if (itemFrame.getItem() == null || itemFrame.getItem().getType() != Material.MAP || !plugin.isMapLoaded(itemFrame.getItem().getDurability())) {
            return;
        }

        if (event instanceof HangingBreakByEntityEvent) {
            if (!(((HangingBreakByEntityEvent) event).getRemover() instanceof Player)) {
                event.setCancelled(true);
                return;
            }

            Player p = (Player) ((HangingBreakByEntityEvent) event).getRemover();
            if (!p.hasPermission("pictureframe.destroy")) {
                event.setCancelled(true);
            }
        } else {
            event.setCancelled(true);
        }
    }
}
