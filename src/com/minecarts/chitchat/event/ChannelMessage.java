package com.minecarts.chitchat.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ChannelMessage extends Event {
    private String channelName;
    private Player[] taggedPlayers;
    private String format;
    private String[] args;

    private static final HandlerList handlers = new HandlerList();

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public ChannelMessage(String channelName,Player[] taggedPlayers,String format,String... args){
        this.channelName = channelName;
        this.taggedPlayers = taggedPlayers;
        this.format=format;
        this.args = args;
    }
   
    public String getChannelName(){
        return this.channelName;
    }
    public Player[] getTaggedPlayers(){
        return this.taggedPlayers;
    }
    public String getFormat(){
        return this.format;
    }
    public String[] getArgs(){
        return this.args;
    }
}
