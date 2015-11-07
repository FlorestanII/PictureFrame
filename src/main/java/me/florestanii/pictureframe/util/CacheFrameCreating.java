package me.florestanii.pictureframe.util;

import me.florestanii.pictureframe.MapHandler;

import org.bukkit.entity.Player;

public class CacheFrameCreating {

    private Player player;
    private MapHandler mapHandler;
    
    public CacheFrameCreating(Player player, MapHandler mapHandler){
        this.player = player;
        this.mapHandler = mapHandler;
    }
    
    public Player getPlayer(){
        return player;
    }
    
    public MapHandler getMapHandler(){
        return mapHandler;
    }
    
}
