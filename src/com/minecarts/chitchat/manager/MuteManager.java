package com.minecarts.chitchat.manager;

import java.util.Map;
import java.util.HashMap;
import java.util.Date;

import org.bukkit.OfflinePlayer;
import org.bukkit.permissions.Permissible;


public class MuteManager {
    private static Map<OfflinePlayer, Date> mutedPlayers = new HashMap<OfflinePlayer, Date>();
    
    
    public static Boolean isMuted(OfflinePlayer player) {
        if(player instanceof Permissible && ((Permissible) player).hasPermission("chitchat.mute")) return true;
        
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
    
    public static void mute(OfflinePlayer player) {
        mute(player, 1000 * 60 * 15);
    }
    
    public static void mute(OfflinePlayer player, long duration) {
        if(duration > 0) {
            mutedPlayers.put(player, new Date(new Date().getTime() + duration));
        }
    }
    
    public static void unmute(OfflinePlayer player) {
        mutedPlayers.remove(player);
    }
}
