package com.minecarts.chitchat.manager;

import org.bukkit.entity.Player;
import java.util.ArrayList;

public class GagManager {
    private static ArrayList<Player> gaggedPlayers = new ArrayList<Player>();
    
    public static Boolean isGagged(Player player){
        return gaggedPlayers.contains(player);
    }
    
    public static void gag(Player player){
        if(gaggedPlayers.contains(player)) return;
        gaggedPlayers.add(player);
    }
    
    public static void ungag(Player player){
        gaggedPlayers.remove(player);
    }
}
