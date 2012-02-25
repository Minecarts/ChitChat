package com.minecarts.chitchat.manager;


import com.minecarts.chitchat.ChitChat;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.ListIterator;

public class SpamManager {
    private static HashMap<Player, ArrayList<Long>> messageTracker = new HashMap<Player, ArrayList<Long>>();
    
    public static void checkSpam(Player player){
        ArrayList<Long> timestampHistory = messageTracker.get(player);
        if(timestampHistory == null){
            timestampHistory = new ArrayList<Long>();
        }

        Long messageTime = (new Date()).getTime();
        timestampHistory.add(messageTime);

        //Clear out any old expired message timestamps
        ListIterator<Long> itr = timestampHistory.listIterator(timestampHistory.size());
        while(itr.hasPrevious()) {
            Long checkTime = itr.previous();
            Long elapsedTime = messageTime - checkTime;
            if(elapsedTime > (4 * 1000)){ //TODO Config
                if(itr.previousIndex() >= 0) {
                    //Why +2? Not sure, becuase previousIndex off by 1, and this is a count not a index? investigate this someday.
                    //  could be a bug in NormalizedDrops if this is the case
                    timestampHistory.subList(0, itr.previousIndex() + 2).clear();
                }
                break;
            }
        }
        if(timestampHistory.size() > 5){ //TODO Config
        //Spamming
            ((ChitChat) Bukkit.getPluginManager().getPlugin("ChitChat")).dbInsertBan(player);
            player.kickPlayer("Your keyboard must be overheating...why don't you take a little break?"); //TODO: Config
        }
        messageTracker.put(player,timestampHistory);
    }
    
    public static void banPlayer(Player player){
        
    }
}
