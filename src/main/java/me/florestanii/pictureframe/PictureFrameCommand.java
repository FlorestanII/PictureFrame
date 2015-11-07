package me.florestanii.pictureframe;

import me.florestanii.pictureframe.util.Cache;
import me.florestanii.pictureframe.util.CacheFrameCreating;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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

    private void handleFrameCreate(Player p, String[] args) {
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

        MapHandler mapHandler = new MapHandler(p, pathBuilder.toString(), 1, 1, this.plugin);
        mapHandler.runTaskTimer(this.plugin, 0L, 10L);

        Cache.setCacheCreating(p, new CacheFrameCreating(p, mapHandler));
        p.sendMessage(ChatColor.YELLOW + "Rightclick on a wall to place the poster.");
    }

    private void handleMultiFrameCreate(Player p, String[] args) {
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

        MapHandler mapHandler = new MapHandler(p, pathBuilder.toString(), width, height, this.plugin);
        mapHandler.runTaskTimer(this.plugin, 0L, 10L);

        Cache.setCacheMultiCreating(p, new CacheFrameCreating(p, mapHandler));
        p.sendMessage(ChatColor.YELLOW + "Rightclick on a wall to place the poster.");
    }
}