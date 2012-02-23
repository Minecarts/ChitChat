package com.minecarts.chitchat.channel;

import com.minecarts.chitchat.ChitChat;
import com.minecarts.chitchat.manager.ChannelManager;
import com.minecarts.chitchat.manager.LanguageManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Random;
import java.util.Set;

public class PrefixChannel extends Channel{

    private Player player;
    private String prefix;
    private ChatColor channelColor = null;
    private final ChatColor[] colors = {ChatColor.GREEN, ChatColor.LIGHT_PURPLE, ChatColor.DARK_GREEN, ChatColor.DARK_RED, ChatColor.AQUA, ChatColor.DARK_AQUA, ChatColor.RED, ChatColor.GOLD};

    public PrefixChannel(Player player, String name, String prefix){
        super(player,name);
        this.player = player;
        this.prefix = prefix;
    }

    public PrefixChannel(Player player, String name){
        super(player,name);
        final ArrayList<String> usedPrefixes = ChannelManager.getUsedPrefix(player);
        for(Integer i = 1; i < 50; i++){ //TODO: Make this a config value
            if(!usedPrefixes.contains(i.toString())){
                this.prefix = i.toString();
                break;
            }
        }
        this.player = player;
    }

    public void join(){
        if(getLink().isPlayerInChannel(this.player)){
            getLink().getPlayerChannel(this.player).display("You are already in this channel (" + getName() + ").");
            return;
        }
        super.join();

        ChannelManager.markPrefixUsed(this.player,this.prefix);

        //TODO: Permanent and announcment channels should not announce joins...

        this.display("You joined the channel (" + getName() + ").");
        this.broadcastExceptPlayer(this.player, this.player.getDisplayName() + " joined the channel.");
    }
    
    public void leave(){
        if(!getLink().isPlayerInChannel(this.player)){
            this.display("You are not in that channel (" + getName() + ").");
            return;
        }
        super.leave();

        ChannelManager.markPrefixAvailable(this.player, this.prefix);

        this.display("You left the channel (" + getName() + ").");
        this.broadcastExceptPlayer(this.player, this.player.getDisplayName() + " left the channel.");
    }


    @Override
    protected String formatMessage(String message){
        ChatColor color = (isDefault()) ? ChatColor.GRAY : ChatColor.DARK_GRAY;
        return MessageFormat.format("{0}/{2}{1} {3}",
                color, //0
                this.getColor(), //1
                this.getPrefix(), //2
                message //3
        );
    }
    @Override
    protected String formatMessage(Player player, String message){
        ChatColor color = (isDefault()) ? ChatColor.GRAY : ChatColor.DARK_GRAY;
        return MessageFormat.format("{0}/{2}{1} <{3}{1}> {4}",
                color, //0
                this.getColor(), //1
                this.getPrefix(), //2
                player.getDisplayName(), //3
                message //4
        );
    }

    public void setColor(ChatColor color){
        this.channelColor = color;
    }
    public ChatColor getColor(){
        if(channelColor != null) return channelColor; //Return the channel color if it's specifically set
        try{
            Integer index = Integer.parseInt(this.prefix);
            return colors[index % colors.length];
        } catch (NumberFormatException e){
            System.out.println("ChitChat> Prefix was not able to be colored" + this.prefix);
            return ChatColor.WHITE;
        } 
    }

    public String getPrefix(){
        return this.prefix;
    }

    @Override
    public void setDefault(){
        super.setDefault();
        //this.display(getName() + " is now your default channel.");
    }
}
