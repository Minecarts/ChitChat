package com.minecarts.chitchat.manager;

import java.util.Map;
import java.util.HashMap;
import java.util.Date;

import org.bukkit.entity.Player;

public class MuteManager {
    private static Map<Player, Date> mutedPlayers = new HashMap<Player, Date>();
    
    public static Boolean isMuted(Player player) {
        if(player.hasPermission("chitchat.mute")) return true;
        
        Date expires = mutedPlayers.get(player);
        if(expires == null) return false;
        
        if(expires.after(new Date())) {
            return true;
        }
        else {
            mutedPlayers.remove(player);
            return false;
        }
    }
    
    public static void mute(Player player) {
        mute(player, 1000 * 60 * 15);
    }
    
    public static void mute(Player player, long duration) {
        if(duration > 0) {
            mutedPlayers.put(player, new Date(new Date().getTime() + duration));
        }
    }
    
    public static void unmute(Player player) {
        mutedPlayers.remove(player);
    }
}
