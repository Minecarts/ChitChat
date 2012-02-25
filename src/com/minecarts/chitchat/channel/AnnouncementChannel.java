package com.minecarts.chitchat.channel;

import com.minecarts.chitchat.manager.ChannelManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.text.MessageFormat;

public class AnnouncementChannel extends PrefixChannel {
    public AnnouncementChannel(Player player){
        super(player,"Announcement","!");
        this.color(ChatColor.RED);
    }

    @Override
    public String formatMessage(Player player, String message){
        ChatColor color = (isDefault()) ? ChatColor.GRAY : ChatColor.DARK_GRAY;
        return MessageFormat.format("{0} {1}",
                this.getPrefix(), //0
                message //1
        );
    }

    @Override
    public void broadcast(Player[] taggedPlayers, String message){
        if(taggedPlayers[0].hasPermission("chitchat.announcement.chat")){
            super.broadcast(taggedPlayers,ChatColor.YELLOW + message);
        } else {
            this.display("You are not allowed to chat in this channel.");
        }
    }
    @Override
    public void broadcast(String message){
        this.display("You are not allowed to chat in this channel.");
    }
    @Override
    public Boolean setDefault(){
        return false;
    }
}
