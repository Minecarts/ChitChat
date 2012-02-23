package com.minecarts.chitchat.manager;

import com.minecarts.chitchat.ChitChat;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;

public class IgnoreManager {
    
    private static HashMap<Player, ArrayList<String>> ignoreList = new HashMap<Player, ArrayList<String>>();
    
    public static Boolean isIgnoring(Player player, String ignoreeName){
        ArrayList<String> ignoredPlayers = ignoreList.get(player);
        if(ignoredPlayers == null) return false;
        return ignoredPlayers.contains(ignoreeName);
    }

    //The following functions support strings to load them from the database (thus loading offline players)
    public static void ignorePlayer(Player player, String ignoreeName){
        ArrayList<String> ignoredPlayers = ignoreList.get(player);
        if(ignoredPlayers == null){
            ignoredPlayers = new ArrayList<String>();
        }
        ignoredPlayers.add(ignoreeName);
        ignoreList.put(player, ignoredPlayers);
    }

    public static void unignorePlayer(Player player, String ignoreeName){
        ArrayList<String> ignoredPlayers = ignoreList.get(player);
        if(ignoredPlayers == null) return;
        ignoredPlayers.remove(ignoreeName);
    }
}
