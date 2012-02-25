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
    public Boolean join(){
        this.display("You cannot join the local channel.");
        return false;
    }
    @Override
    public Boolean leave(){
        this.display("You cannot leave the local channel.");
        return false;
    }

    protected String formatMessage(String message){
        return MessageFormat.format("{0}",
                message
        );
    }
    protected String formatMessage(Player player, String message){
        Double range = player.getLocation().distance(getOwner().getLocation());
        if(range > 200) return null; //Null messages wont be displayed

        ChatColor prefixColor = ChatColor.DARK_GRAY;
        if(isDefault()){
            prefixColor = ChatColor.GRAY;
        }
        
        //TODO, make these ranges a config value
        ChatColor color = ChatColor.WHITE;
        if(range > 175) color = ChatColor.DARK_GRAY;
        else if(range > 75) color = ChatColor.GRAY;

        String rangeText = "";
        if(getOwner().hasPermission("chitchat.local.range")){
            rangeText = "." + Math.round(range) + "";
        }

        return MessageFormat.format("{3}/s{2}{4} <{0}> {1}",
                player.getDisplayName(), //0
                message, //1
                rangeText, //2
                prefixColor, //3
                color //4
        );
    }
}
