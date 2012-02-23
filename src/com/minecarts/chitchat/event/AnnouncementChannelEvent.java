package com.minecarts.chitchat.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.ArrayList;

public class AnnouncementChannelEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private Player performingPlayer;
    private String message;

    public AnnouncementChannelEvent(Player performingPlayer, String announcement){
        this.performingPlayer = performingPlayer;
        this.message = announcement;
    }
    public String getMessage(){
        return this.message;
    }
    public Player getPrimaryPlayer(){
        return this.performingPlayer;
    }

    public HandlerList getHandlers() {
        return handlers;
    }
    public static HandlerList getHandlerList() {
        return handlers;
    }
            
}
