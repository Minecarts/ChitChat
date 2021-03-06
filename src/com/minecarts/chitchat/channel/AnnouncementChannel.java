package com.minecarts.chitchat.channel;

import com.minecarts.chitchat.manager.ChannelManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.text.MessageFormat;

public class AnnouncementChannel extends PermanentChannel {
    public AnnouncementChannel(Player player){
        super(player, "Announcement", "!", ChatColor.RED);
    }

    @Override
    public String formatMessage(Player player, String message){
        ChatColor color = (isDefault()) ? ChatColor.GRAY : ChatColor.DARK_GRAY;

        String sender = "";
        if(getOwner().hasPermission("chitchat.announcement.chat") && player.hasPermission("chitchat.announcement.chat")){
            sender = ChatColor.DARK_GRAY + " " + player.getName()+ ":" + this.color();
        }
        
        return MessageFormat.format("{0}{2} {1}",
                this.getPrefix(), //0
                message, //1
                sender
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
