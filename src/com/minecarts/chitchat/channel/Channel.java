package com.minecarts.chitchat.channel;

import com.minecarts.chitchat.manager.ChannelManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Set;

abstract public class Channel {
    private Player owningPlayer; //The player this channel belongs to
    private ChannelLink channelLink;
    private String name;

    public Channel(Player owner, String name){
        this.owningPlayer = owner;
        this.name = name;
        this.setLink(ChannelManager.getOrCreateChannelLink(name));
    }

    public String getName(){
        return this.name;
    }
    public Player getOwner(){
        return this.owningPlayer;
    }

    public Set<Player> getMembers(){
        return this.getLink().getMembers();
    }
    
    protected ChannelLink getLink(){
        return this.channelLink;
    }
    protected void setLink(ChannelLink link){
        this.channelLink = link;
    }

    public void join(){
        getLink().joinPlayer(this.owningPlayer,this);
        ChannelManager.addPlayerChannel(this.owningPlayer,this);
    }
    public void leave(){
        getLink().leavePlayer(this.owningPlayer);
        ChannelManager.removePlayerChannel(this.owningPlayer, this);
    }

    public void broadcastExceptPlayer(Player player, String message){
        getLink().relayExceptPlayer(player,message);
    }

    //Outbound
    public void broadcast(Player player, String message){
        getLink().relayMessage(player,message);
    }
    public void broadcast(String message){
        getLink().relayMessage(message);
    }
    //Inbound
    public void display(Player player, String message){
        getOwner().sendMessage(formatMessage(player, message));
    }
    public void display(String message){
        getOwner().sendMessage(formatMessage(message));
    }

    abstract protected String formatMessage(String message);
    abstract protected String formatMessage(Player player, String message);

    public void setDefault(){
        ChannelManager.setDefaultPlayerChannel(this.owningPlayer,this);
    }

    public Boolean isDefault(){
        return (ChannelManager.getDefaultPlayerChannel(this.owningPlayer).getName().equalsIgnoreCase(this.getName()));
    }
}
