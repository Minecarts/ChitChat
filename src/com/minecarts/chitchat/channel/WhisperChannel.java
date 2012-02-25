package com.minecarts.chitchat.channel;

import com.minecarts.chitchat.manager.ChannelManager;
import com.minecarts.chitchat.manager.WhisperManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.text.MessageFormat;

public class WhisperChannel extends Channel {
    private Player target;
    public WhisperChannel(Player owner, String name){
        super(owner, name);
        super.join();
    }
    
    public void target(Player target){
        this.target = target;
    }
    public Player target(){
        return this.target;
    }

    @Override
    public void displayInbound(Player player, String message){
        String formattedMessage = formatInbound(player, message);
        if(formattedMessage != null){
            getOwner().sendMessage(formattedMessage);
            WhisperManager.setLastReceivedWhisper(getOwner(),this);
        }
    }
    @Override
     public void displayOutbound(Player player, String message){
        String formattedMessage = formatOutbound(this.target(), message);
        if(formattedMessage != null){
            getOwner().sendMessage(formattedMessage);
            WhisperManager.setLastSentWhisper(getOwner(),this);
        }
    }


    //TODO: These functions are not used as the message is pre-formatted in the broadcast
    //TODO:    ideally we should revisit this in the future and see if this is the best way to do this.
    @Override
    public String formatMessage(Player p, String message){ return message; }
    @Override
    public String formatMessage(String message){ return message; }

    protected String formatInbound(Player player, String message){
        //Inbound messages
        String prefix = (isDefault() ? ChatColor.GRAY.toString() + ":" + ChatColor.DARK_GRAY.toString() : ChatColor.DARK_GRAY.toString());
        return MessageFormat.format("{1}/r {0}[{2}] {3}",
                ChatColor.AQUA,
                prefix,
                player.getDisplayName(),
                message
        );
    }

    protected String formatOutbound(Player player, String message){
        //Outbound messages
        String prefix = (isDefault() ? ChatColor.GRAY.toString() + ":" + ChatColor.DARK_GRAY.toString() : ChatColor.DARK_GRAY.toString());
        return MessageFormat.format("{3}/rw {0}[{1}{0}] {2}",
                ChatColor.DARK_AQUA,
                player.getDisplayName(),
                message,
                prefix
        );
    }
}
