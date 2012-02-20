package com.minecarts.chitchat.channel;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.text.MessageFormat;

public class WhisperChannel extends Channel {
    private Player source;
    private Player destination;

    public WhisperChannel(Player source, Player destination){
        super(source, "Whisper-"+source.getName()+"-"+destination.getName());
        this.source = source;
        this.destination = destination;
    }
    

    public void sendMessage(Player player, String message){
        source.sendMessage(MessageFormat.format("{0}> [{1}{0}] {2}", ChatColor.DARK_AQUA, destination.getDisplayName(), message));
        destination.sendMessage(MessageFormat.format("{0}[{1}{0}] {2}", ChatColor.AQUA,source.getDisplayName(),message));
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
