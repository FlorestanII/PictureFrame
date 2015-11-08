package me.florestanii.pictureframe;

import me.florestanii.pictureframe.util.Util;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.logging.Level;

public class PictureFrameCommand implements CommandExecutor {

    private final PictureFrame plugin;

    public PictureFrameCommand(PictureFrame plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String name, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.DARK_RED + "This command is only for players!");
            return true;
        }

        Player p = (Player) sender;

        if (args.length == 0) {
            sendHelp(p);
            return true;
        }

        if (args[0].equalsIgnoreCase("create")) {
            handleFrameCreate(p, args);
            return true;
        }

        if (args[0].equalsIgnoreCase("multicreate")) {
            handleMultiFrameCreate(p, args);
            return true;
        }

        sendHelp(p);
        return true;
    }

    public void sendHelp(Player p) {
        // TODO send help to player
    }

    private void handleFrameCreate(final Player p, String[] args) {
        if (!p.hasPermission("pictureframe.create")) {
            p.sendMessage(ChatColor.DARK_RED + "You don't have enough permissions to use that command!");
            return;
        }

        StringBuilder pathBuilder = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            if (i != 1) {
                pathBuilder.append(" ");
            }
            pathBuilder.append(args[i]);
        }

        startPlacePoster(p, pathBuilder.toString(), 1, 1);
    }

    private void handleMultiFrameCreate(final Player p, String[] args) {
        if (!p.hasPermission("pictureframe.mutlicreate")) {
            p.sendMessage(ChatColor.DARK_RED + "You don't have enough permissions to use that command!");
            return;
        }

        int width = Integer.parseInt(args[1]);
        int height = Integer.parseInt(args[2]);

        StringBuilder pathBuilder = new StringBuilder();
        for (int i = 3; i < args.length; i++) {
            if (i != 3) {
                pathBuilder.append(" ");
            }
            pathBuilder.append(args[i]);
        }

        startPlacePoster(p, pathBuilder.toString(), width, height);
    }

    private void startPlacePoster(final Player p, String path, int width, int height) {
        final MapHandler mapHandler = new MapHandler(p, path, width, height, this.plugin);
        mapHandler.setCallback(new MapHandler.Callback() {
            @Override
            public void posterReady(final Poster poster, final List<ItemStack> maps) {
                p.sendMessage(ChatColor.YELLOW + "Rightclick on a wall to place the poster.");

                plugin.getServer().getPluginManager().registerEvents(new Listener() {
                    @EventHandler
                    public void onBlockInteract(PlayerInteractEvent event) {
                        if (event.isCancelled() || !event.hasBlock() || event.getAction() != Action.RIGHT_CLICK_BLOCK) {
                            return;
                        }

                        final Block topLeftBlock = event.getClickedBlock();
                        final BlockFace facing = event.getBlockFace();

                        try {
                            for (int x = 0; x < poster.getWidth(); x++) {
                                for (int y = 0; y < poster.getHeight(); y++) {
                                    Block block = Util.getRelative(topLeftBlock, facing, -y, -x, 0);
                                    ItemStack map = maps.get(y * poster.getWidth() + x);
                                    ItemMeta meta = map.getItemMeta();
                                    meta.setDisplayName("");
                                    map.setItemMeta(meta);
                                    Util.attachItemFrame(block, map, facing);
                                }
                            }
                        } catch (Exception e) {
                            p.sendMessage(ChatColor.RED + "Attaching the poster failed.");
                            plugin.getLogger().log(Level.SEVERE, "Attaching the poster failed", e);
                        }
                        event.setCancelled(true);
                        HandlerList.unregisterAll(this);
                    }

                    @EventHandler
                    public void onPlayerQuit(PlayerQuitEvent e) {
                        HandlerList.unregisterAll(this);
                    }
                }, plugin);
            }

            @Override
            public void posterFailed(Throwable exception) {
                p.sendMessage(ChatColor.RED + "Creating the poster failed.");
                plugin.getLogger().log(Level.SEVERE, "Creating the poster failed", exception);
            }
        });
        mapHandler.run();
    }
}