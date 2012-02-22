package com.minecarts.chitchat.channel;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class AnnouncementChannel extends PrefixChannel {
    public AnnouncementChannel(Player player){
        super(player,"Announcement","!");
        this.setColor(ChatColor.RED);
        super.join();
    }

    @Override
    public void broadcast(Player player, String message){
        this.display("You are not allowed to chat in this channel.");
    }
    @Override
    public void broadcast(String message){
        this.display("You are not allowed to chat in this channel.");
    }
    @Override
    public void setDefault(){
        return;
    }
}
