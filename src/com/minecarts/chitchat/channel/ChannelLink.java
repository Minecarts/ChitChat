package com.minecarts.chitchat.channel;

import com.minecarts.chitchat.manager.ChannelManager;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Set;

public class ChannelLink {
    private HashMap<Player, Channel> members = new HashMap<Player, Channel>();
    private String uniqueId;
    private String name;

    public ChannelLink(String name){
        this.name = name;
        this.uniqueId = name.toLowerCase();
        ChannelManager.createChannelLink(this);
    }
    
    public String getName(){
        return this.name;
    }
    public String getId(){
        return this.uniqueId;
    }

    public Boolean isPlayerInChannel(Player player){
        return this.members.containsKey(player);
    }
    
    public void joinPlayer(Player player, Channel playerChannel){
        this.members.put(player, playerChannel);
    }

    public void leavePlayer(Player player){
        if(!this.members.containsKey(player)) return;
        this.members.remove(player);
    }


    public Set<Player> getMembers(){
        return this.members.keySet();
    }

    
    public Channel getPlayerChannel(Player player){
        return this.members.get(player);
    }



    public void relayExceptPlayer(Player player, String message){
        for(Channel channel : this.members.values()){
            if(channel.getOwner().equals(player)){
                continue;
            }
            channel.display(message);
        }
    }
    public void relayMessage(Player player, String message){
        for(Channel channel : this.members.values()){
            channel.display(player, message);
        }
    }
    public void relayMessage(String message){
        for(Channel channel : this.members.values()){
            channel.display(message);
        }
    }
}
