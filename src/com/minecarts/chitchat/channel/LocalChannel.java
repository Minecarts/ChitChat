package com.minecarts.chitchat.channel;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.text.MessageFormat;

public class LocalChannel extends Channel{

    public LocalChannel(Player player){
        super(player, "Local");
    }

    protected String formatMessage(String message){
        ChatColor color = (isDefault()) ? ChatColor.GRAY : ChatColor.DARK_GRAY;
        return MessageFormat.format("LocalEvent: {0}",
                message
        );
    }
    protected String formatMessage(Player player, String message){
        ChatColor color = (isDefault()) ? ChatColor.GRAY : ChatColor.DARK_GRAY;
        return MessageFormat.format("Local: <{0}> {1}",
                player.getDisplayName(),
                message
        );
    }
}
