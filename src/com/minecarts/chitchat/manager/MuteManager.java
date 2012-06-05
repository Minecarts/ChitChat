package com.minecarts.chitchat.manager;

import java.util.Map;
import java.util.HashMap;
import java.util.Date;

import org.bukkit.OfflinePlayer;
import org.bukkit.permissions.Permissible;


public class MuteManager {
    private static Map<String, Date> mutedPlayers = new HashMap<String, Date>();
    
    public static Boolean isMuted(OfflinePlayer player) {
        if(player instanceof Permissible && ((Permissible) player).hasPermission("chitchat.mute")) return true;
        return isMuted(player.getName());
    }
    
    public static Boolean isMuted(String player) {
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
        mute(player.getName());
    }
    public static void mute(String player) {
        mute(player, 1000 * 60 * 15);
    }
    
    public static void mute(OfflinePlayer player, long duration) {
        mute(player.getName(), duration);
    }
    public static void mute(String player, long duration) {
        if(duration > 0) {
            mutedPlayers.put(player, new Date(new Date().getTime() + duration));
        }
    }
    
    public static void unmute(OfflinePlayer player) {
        unmute(player.getName());
    }
    public static void unmute(String player) {
        mutedPlayers.remove(player);
    }
}
