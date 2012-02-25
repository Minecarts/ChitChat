package com.minecarts.chitchat.channel;

import com.minecarts.chitchat.ChitChat;
import com.minecarts.chitchat.manager.ChannelManager;
import com.minecarts.chitchat.manager.LanguageManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.awt.*;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Random;
import java.util.Set;

public class PrefixChannel extends Channel{

    private Player player;
    private String prefix;
    private final ChatColor[] colors = {ChatColor.GREEN, ChatColor.BLUE, ChatColor.LIGHT_PURPLE, ChatColor.DARK_GREEN, ChatColor.DARK_RED, ChatColor.RED, ChatColor.GOLD};

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


    public Boolean join(boolean suppressJoinSelf, boolean suppressJoinOther){
        if(this.prefix == null){
            getOwner().sendMessage("You are in too many channels and are unable to join " + this.getName() + ".");
            return false;
        }
        if(getLink().isPlayerInChannel(this.player)){
            getLink().getPlayerChannel(this.player).setDefault();
            getLink().getPlayerChannel(this.player).display("You are already in this channel (" + getName() + ").");
            return false;
        }
        super.join();

        ChannelManager.markPrefixUsed(this.player,this.prefix);

        //TODO: Permanent and announcment channels should not announce joins...
        if(!suppressJoinSelf){
            this.display("You joined the channel (" + getName() + ").");
        }
        if(!suppressJoinOther){
            this.broadcastExceptPlayer(this.player, this.player.getDisplayName() + " joined the channel.");
        }
        return true;
    }

    public Boolean leave(boolean silentQuitSelf, boolean silentQuitOther){
        if(!getLink().isPlayerInChannel(this.player)){
            this.display("You are not in that channel (" + getName() + ").");
            return false;
        }
        super.leave();

        ChannelManager.markPrefixAvailable(this.player, this.prefix);

        if(!silentQuitSelf){
            this.display("You left the channel (" + getName() + ").");
        }
        if(!silentQuitOther){
            this.broadcastExceptPlayer(this.player, this.player.getDisplayName() + " left the channel.");
        }
        return true;
    }


    @Override
    protected String formatMessage(String message){
        ChatColor defaultColor = (isDefault()) ? ChatColor.GRAY : ChatColor.DARK_GRAY;
        return MessageFormat.format("{0} {1}",
                this.getPrefix(), //0
                message //1
        );
    }
    @Override
    protected String formatMessage(Player player, String message){
        Random rand = new Random();
        //ChatColor defaultColor = (isDefault()) ? ChatColor.GRAY : ChatColor.DARK_GRAY;
        return MessageFormat.format("{0} <{2}{1}> {3}",
                this.getPrefix(), //0
                this.color(), //1
                player.getDisplayName(), //2
                message //3
        );
    }

    @Override
    public ChatColor color(){
        if(super.color() != null) return super.color(); //Return the channel color if it's specifically set
        try{
            Integer index = Integer.parseInt(this.prefix);
            return colors[index % colors.length];
        } catch (NumberFormatException e){
            System.out.println("ChitChat> Prefix was not able to be colored" + this.prefix);
            return ChatColor.WHITE;
        } 
    }

    public String getPrefix(){
        return MessageFormat.format("{0}/{1}{2}",
                (isDefault() ? ChatColor.GRAY + ":" + ChatColor.DARK_GRAY : ChatColor.DARK_GRAY),
                this.prefix,
                this.color()
                );
    }
    
    public String getRawPrefix(){
        return this.prefix;
    }
}
