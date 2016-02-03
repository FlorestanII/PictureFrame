package me.florestanii.pictureframe.util;

import org.bukkit.Material;
import org.bukkit.Rotation;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.entity.ItemFrame;
import org.bukkit.inventory.ItemStack;
import org.bukkit.map.MapView;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Util {
    public static void attachItemFrame(Block block, ItemStack map, BlockFace face) {
        ItemFrame frame = block.getWorld().spawn(block.getRelative(face).getLocation(), ItemFrame.class);
        Block frameBlock = block.getRelative(face);
        frameBlock.setType(Material.AIR);
        frame.teleport(frameBlock.getLocation());
        frame.setFacingDirection(face, true);
        frame.setItem(map);
        frame.setRotation(Rotation.NONE);
    }

    public static Block getRelative(Block block, BlockFace face, int upDown, int leftRight, int frontBack) {
        switch (face) {
            case NORTH:
                return block.getRelative(leftRight, upDown, -frontBack);
            case EAST:
                return block.getRelative(frontBack, upDown, leftRight);
            case SOUTH:
                return block.getRelative(-leftRight, upDown, frontBack);
            case WEST:
                return block.getRelative(-frontBack, upDown, -leftRight);
        }
        throw new IllegalArgumentException("Unsupported BlockFace: " + face);
    }

    public static void removeAllRenderers(MapView map) {
        for (int i = 0; i < map.getRenderers().size(); i++) {
            map.removeRenderer(map.getRenderers().get(i));
        }
    }

    public static List<ConfigurationSection> getConfigList(ConfigurationSection config, String path) {
        if (!config.isList(path)) {
            return Collections.emptyList();
        }

        List<ConfigurationSection> list = new LinkedList<>();
        for (Map object : config.getMapList(path)) {
            MemoryConfiguration mc = new MemoryConfiguration();
            mc.addDefaults(object);
            list.add(mc);
        }

        return list;
    }
}
