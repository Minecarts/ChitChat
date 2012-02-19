package com.minecarts.chitchat.channel;

import com.minecarts.chitchat.manager.ChannelManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class BaseChannel {
    private HashMap<Player, PlayerChannel> members = new HashMap<Player, PlayerChannel>();
    private String uniqueId;
    private String name;
    private String prefix;
    private ChatColor color;

    public BaseChannel(String name){
        this.name = name;
        this.uniqueId = name.toLowerCase();
        ChannelManager.addChannel(this);
    }
    
    public String getName(){
        return this.name;
    }
    public String getId(){
        return this.uniqueId;
    }
    
    public void setPrefix(String prefix){
        this.prefix = prefix;
    }
    public String getPrefix(){
        return this.prefix;
    }
    
    public void setColor(ChatColor color){
        this.color = color;
    }
    public ChatColor getColor(){
        return this.color;
    }
    
    public Boolean isPlayerInChannel(Player player){
        return this.members.containsKey(player);
    }
    
    public void joinPlayer(Player player, PlayerChannel playerChannel){
        this.members.put(player, playerChannel);
    }

    public void leavePlayer(Player player){
        if(!this.members.containsKey(player)) return;
        this.members.remove(player);
    }


    public Set<Player> getMembers(){
        return this.members.keySet();
    }

    
    public PlayerChannel getPlayerChannel(Player player){
        return this.members.get(player);
    }



    public void broadcastExceptPlayer(Player player, String message){
        for(PlayerChannel channel : this.members.values()){
            if(channel.getPlayer().equals(player)){
                continue;
            }
            channel.broadcast(message);
        }
    }
    public void broadcast(String message){
        for(PlayerChannel channel : this.members.values()){
            channel.broadcast(message);
        }
    }
    
    public void sendMessage(Player player, String message){
        for(PlayerChannel channel : this.members.values()){
            channel.sendMessage(player,message);
        }
    }
}
