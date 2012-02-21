package com.minecarts.chitchat.channel;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.text.MessageFormat;

public class LocalChannel extends Channel{

    public LocalChannel(Player player){
        super(player, "Local");
        super.join();
    }

    //Prevent anyone from joining or leaving this channel via command
    @Override
    public void join(){
        this.display("You cannot join this channel.");
    }
    @Override
    public void leave(){
        this.display("You cannot leave this channel.");
    }

    protected String formatMessage(String message){
        return MessageFormat.format("{0}",
                message
        );
    }
    protected String formatMessage(Player player, String message){
        Double range = player.getLocation().distance(getOwner().getLocation());
        if(range > 100) return null; //Null messages wont be displayed

        //TODO, make these ranges a config value
        ChatColor color = ChatColor.WHITE;
        if(range > 75) color = ChatColor.DARK_GRAY;
        else if(range > 50) color = ChatColor.GRAY;

        return MessageFormat.format(color.toString() + Math.round(range) + ": <{0}> {1}",
                player.getDisplayName(),
                message
        );
    }
}
