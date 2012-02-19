package com.minecarts.chitchat.manager;

import com.minecarts.chitchat.channel.BaseChannel;
import com.minecarts.chitchat.channel.PlayerChannel;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class ChannelManager {
    private static HashMap<String, BaseChannel> activeChannels = new HashMap<String, BaseChannel>();
    private static HashMap<Player, PlayerChannel> defaultChannelMap = new HashMap<Player, PlayerChannel>();
    private static HashMap<Player, ArrayList<PlayerChannel>> playerChannelList = new HashMap<Player, ArrayList<PlayerChannel>>();
    private static HashMap<Player, ArrayList<String>> playerPrefixUsage = new HashMap<Player, ArrayList<String>>();

    public static BaseChannel getChannelByName(String name){
        if(activeChannels.containsKey(name.toLowerCase())){
            return activeChannels.get(name.toLowerCase());
        } else {
            BaseChannel channel = new BaseChannel(name);
            activeChannels.put(channel.getId(),channel);
            return channel;
        }
    }

    public static PlayerChannel getPlayerChannelFromPrefix(Player player, String prefix){
        for(PlayerChannel channel : playerChannelList.get(player)){
            if(prefix.equals(channel.getPrefix())){
                return channel;
            }
        }
        return null;
    }
    
    
    public static void addChannel(BaseChannel channel){
        activeChannels.put(channel.getId(),channel);
    }

    public static void addPlayerChannel(Player p, PlayerChannel ch){
        if(!playerChannelList.containsKey(p)){
            playerChannelList.put(p,new ArrayList<PlayerChannel>());
        }
        playerChannelList.get(p).add(ch);
    }
    
    public static void removePlayerChannel(Player p, PlayerChannel ch){
        if(!playerChannelList.containsKey(p)){
            return;
        }
        playerChannelList.get(p).remove(ch);
    }


    public static ArrayList<PlayerChannel> getPlayerChannels(Player p){
        return playerChannelList.get(p);
    }


    public static void setDefaultPlayerChannel(Player player, PlayerChannel channel){
        defaultChannelMap.put(player,channel);
    }

    public static PlayerChannel getDefaultPlayerChannel(Player player){
        if(defaultChannelMap.containsKey(player)){
            return defaultChannelMap.get(player);
        } else {
            return getChannelByName("Global").getPlayerChannel(player);
        }
    }
    
    public static void markPrefixUsed(Player player, String prefix){
        if(!playerPrefixUsage.containsKey(player)){
            playerPrefixUsage.put(player,new ArrayList<String>());
        }
        playerPrefixUsage.get(player).add(prefix);
    }
    public static void markPrefixAvailable(Player player, String prefix){
        if(!playerPrefixUsage.containsKey(player)){
            return;
        }
        playerPrefixUsage.get(player).remove(prefix);
    }
    public static ArrayList<String> getUsedPrefix(Player player){
        return playerPrefixUsage.get(player);
    }
}
