package com.minecarts.chitchat.manager;

import com.minecarts.chitchat.ChitChat;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

public class IgnoreManager {
    
    private static Map<Player, Set<String>> ignoreList = new HashMap<Player, Set<String>>();
    
    public static Boolean isIgnoring(Player player, String ignoreeName){
        Set<String> ignoredPlayers = ignoreList.get(player);
        if(ignoredPlayers == null) return false;
        return ignoredPlayers.contains(ignoreeName);
    }

    //The following functions support strings to load them from the database (thus loading offline players)
    public static void ignorePlayer(Player player, String ignoreeName){
        Set<String> ignoredPlayers = ignoreList.get(player);
        if(ignoredPlayers == null){
            ignoredPlayers = new HashSet<String>();
            ignoreList.put(player, ignoredPlayers);
        }
        ignoredPlayers.add(ignoreeName);
    }

    public static void unignorePlayer(Player player, String ignoreeName){
        Set<String> ignoredPlayers = ignoreList.get(player);
        if(ignoredPlayers == null) return;
        ignoredPlayers.remove(ignoreeName);
        if(ignoredPlayers.isEmpty()) ignoreList.remove(player);
    }
    
    public static boolean hasIgnoreList(Player player) {
        return ignoreList.containsKey(player);
    }
    
    public static String[] getIgnoreList(Player player) {
        Set<String> list = ignoreList.get(player);
        return list == null ? new String[0] : list.toArray(new String[0]);
    }
    
}
