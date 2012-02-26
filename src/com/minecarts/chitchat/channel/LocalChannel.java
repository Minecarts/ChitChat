package com.minecarts.chitchat.channel;

import com.minecarts.chitchat.manager.PluginManager;
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
        if(range > PluginManager.config().getInt("channel.say.range.oor")) return null; //Null messages wont be displayed

        ChatColor prefixColor = ChatColor.DARK_GRAY;
        if(isDefault()){
            prefixColor = ChatColor.GRAY;
        }

        ChatColor color = ChatColor.WHITE;
        if(range > PluginManager.config().getInt("channel.say.range.dark")) color = ChatColor.DARK_GRAY;
        else if(range > PluginManager.config().getInt("channel.say.range.gray")) color = ChatColor.GRAY;

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
