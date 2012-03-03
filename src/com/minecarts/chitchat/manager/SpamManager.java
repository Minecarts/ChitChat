package com.minecarts.chitchat.manager;


import com.minecarts.chitchat.ChitChat;
import com.minecarts.chitchat.channel.Channel;
import com.minecarts.chitchat.channel.ChannelLink;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.ListIterator;

public class SpamManager {
    private static HashMap<Player, ArrayList<Long>> messageTracker = new HashMap<Player, ArrayList<Long>>();
    private static HashMap<Player, String> lastMessage = new HashMap<Player, String>();
    private static HashMap<Player, Long> lastMessageTime = new HashMap<Player, Long>(); 
    private static HashMap<Player, ChannelLink> lastChannel = new HashMap<Player, ChannelLink>();

    public static Boolean isRepeatedMessage(Player player, ChannelLink link, String message){
        if(lastChannel.get(player) == link 
                && lastMessage.get(player).equalsIgnoreCase(message) 
                && ((new Date()).getTime() - lastMessageTime.get(player)) > (PluginManager.config().getInt("spam.timeout") * 1000)){
            return true;
        }
        lastMessage.put(player,message);
        lastMessageTime.put(player,(new Date()).getTime());
        lastChannel.put(player,link);
        return false;
    }
    
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
            if(elapsedTime > (PluginManager.config().getInt("spam.timeout") * 1000)){
                if(itr.previousIndex() >= 0) {
                    //Why +2? Not sure, becuase previousIndex off by 1, and this is a count not a index? investigate this someday.
                    //  could be a bug in NormalizedDrops if this is the case
                    timestampHistory.subList(0, itr.previousIndex() + 2).clear();
                }
                break;
            }
        }
        if(timestampHistory.size() > PluginManager.config().getInt("spam.quantity")){
        //Spamming
            ((ChitChat) Bukkit.getPluginManager().getPlugin("ChitChat")).dbInsertBan(player);
            player.kickPlayer(PluginManager.config().getString("spam.ban_message"));
        }
        messageTracker.put(player,timestampHistory);
    }
    
    public static void banPlayer(Player player){
        
    }
}
