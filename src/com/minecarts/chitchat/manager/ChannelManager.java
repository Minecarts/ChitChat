package com.minecarts.chitchat.manager;

import com.minecarts.chitchat.ChitChat;
import com.minecarts.chitchat.channel.Channel;
import com.minecarts.chitchat.channel.ChannelLink;
import com.minecarts.chitchat.channel.LocalChannel;
import com.minecarts.chitchat.channel.PrefixChannel;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;

public class ChannelManager {
    private static HashMap<String, ChannelLink> activeChannelLinks = new HashMap<String, ChannelLink>();
    private static HashMap<Player, Channel> defaultChannelMap = new HashMap<Player, Channel>();
    private static HashMap<Player, ArrayList<Channel>> playerChannelList = new HashMap<Player, ArrayList<Channel>>();
    private static HashMap<Player, ArrayList<String>> playerPrefixUsage = new HashMap<Player, ArrayList<String>>();
    private static HashMap<Player, Boolean> loginJoinLock = new HashMap<Player, Boolean>();

    public static Boolean isChannelNameJoinRestricted(String channelName){
        // this is bad, please change this to check for PermanentChannels instead of hardcoded values
        return channelName.equalsIgnoreCase("Subscribers")
                || channelName.equalsIgnoreCase("Admin")
                || channelName.equalsIgnoreCase("Announcement");
    }
    
//Join query locking
    public static Boolean isJoinLocked(Player player){
        if(!loginJoinLock.containsKey(player)) return false;
        return loginJoinLock.get(player);
    }
    public static void setJoinLocked(Player player, Boolean locked){
        loginJoinLock.put(player,locked);
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
        ArrayList<Channel> channels = playerChannelList.get(player);
        if(channels == null) return null;
        for(Channel channel : channels){
            if(!(channel instanceof PrefixChannel)) continue;
            if(prefix.equals(((PrefixChannel)channel).getRawPrefix())){
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
    public static void clearPlayerChannels(Player p){
        playerChannelList.put(p,new ArrayList<Channel>());
    }


    public static ArrayList<Channel> getPlayerChannels(Player p){
        return playerChannelList.get(p);
    }


    public static void setDefaultPlayerChannel(Player player, Channel channel){
        if(channel == null){
            ChitChat.getPlugin().getLogger().log(Level.WARNING,"Attempted to set channel to a null channel for " + player.getName());
            return;
        }
        defaultChannelMap.put(player,channel);
    }

    public static Channel getDefaultPlayerChannel(Player player){
        if(defaultChannelMap.containsKey(player)){
            return defaultChannelMap.get(player);
        } else {
            Channel global = getOrCreateChannelLink("Global").getPlayerChannel(player);
            if(global == null){
                return ChannelManager.getPlayerChannels(player).get(0);
            }
            return global;
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
    
    public static Integer getVisibleChannelCount(Player player){
        int i = 0;
        for(Channel ch : getPlayerChannels(player)){
            if(ch instanceof PrefixChannel) i++;
        }
        return i;
    }
}
