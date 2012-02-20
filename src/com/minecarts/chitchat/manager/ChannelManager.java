package com.minecarts.chitchat.manager;

import com.minecarts.chitchat.channel.Channel;
import com.minecarts.chitchat.channel.ChannelLink;
import com.minecarts.chitchat.channel.PrefixChannel;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;

public class ChannelManager {
    private static HashMap<String, ChannelLink> activeChannelLinks = new HashMap<String, ChannelLink>();
    private static HashMap<Player, Channel> defaultChannelMap = new HashMap<Player, Channel>();
    private static HashMap<Player, ArrayList<Channel>> playerChannelList = new HashMap<Player, ArrayList<Channel>>();
    private static HashMap<Player, ArrayList<String>> playerPrefixUsage = new HashMap<Player, ArrayList<String>>();

    public static Boolean isChannelNameJoinRestricted(String channelName){
        return channelName.equalsIgnoreCase("Subscriber") || channelName.equalsIgnoreCase("Admin");
    }

//ChannelLinks
    public static ChannelLink getOrCreateChannelLink(String name){
        if(activeChannelLinks.containsKey(name.toLowerCase())){
            return activeChannelLinks.get(name.toLowerCase());
        } else {
            ChannelLink channel = new ChannelLink(name);
            activeChannelLinks.put(channel.getId(),channel);
            return channel;
        }
    }

    public static void createChannelLink(ChannelLink channel){
        activeChannelLinks.put(channel.getId(), channel);
    }

//Channels
    public static PrefixChannel getChannelFromPrefix(Player player, String prefix){
        for(Channel channel : playerChannelList.get(player)){
            if(!(channel instanceof PrefixChannel)) continue;
            if(prefix.equals(((PrefixChannel)channel).getPrefix())){
                return (PrefixChannel)channel;
            }
        }
        return null;
    }
    
    


    public static void addPlayerChannel(Player p, Channel ch){
        if(!playerChannelList.containsKey(p)){
            playerChannelList.put(p,new ArrayList<Channel>());
        }
        playerChannelList.get(p).add(ch);
    }
    
    public static void removePlayerChannel(Player p, Channel ch){
        if(!playerChannelList.containsKey(p)){
            return;
        }
        playerChannelList.get(p).remove(ch);
    }


    public static ArrayList<Channel> getPlayerChannels(Player p){
        return playerChannelList.get(p);
    }


    public static void setDefaultPlayerChannel(Player player, Channel channel){
        defaultChannelMap.put(player,channel);
    }

    public static Channel getDefaultPlayerChannel(Player player){
        if(defaultChannelMap.containsKey(player)){
            return defaultChannelMap.get(player);
        } else {
            return getOrCreateChannelLink("Global").getPlayerChannel(player);
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
