package me.florestanii.pictureframe.listener;

import me.florestanii.pictureframe.PictureFrame;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.map.MapView;

public class ChunkListener implements Listener{

    private final PictureFrame plugin;
    
    public ChunkListener(PictureFrame plugin){
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onChunkLoad(ChunkLoadEvent e){
        
        Chunk chunk = e.getChunk();
        Entity[] entities = chunk.getEntities();
        
        for(Entity entity : entities){
            
            if(!(entity instanceof ItemFrame))
                continue;
            
            ItemFrame itemFrame = (ItemFrame)entity;
            ItemStack item = itemFrame.getItem();
            
            if(item.getType() != Material.MAP)
                continue;
            
            @SuppressWarnings("deprecation")
            MapView mapView = plugin.getServer().getMap(item.getDurability());
            
            for(Entity p : itemFrame.getNearbyEntities(128, 128, 128)){
                if(!(p instanceof Player))
                    continue;
                
                ((Player)p).sendMap(mapView);
                
            }   
        }
    }
}
