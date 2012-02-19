package com.minecarts.chitchat.channel;

import com.minecarts.chitchat.manager.ChannelManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Random;

public class PlayerChannel{
    private BaseChannel baseChannel;
    private Player player;
    private String prefix;
    private final Random random = new Random();
    private final ChatColor[] colors = {ChatColor.GOLD, ChatColor.RED, ChatColor.GREEN, ChatColor.LIGHT_PURPLE, ChatColor.DARK_GREEN, ChatColor.DARK_RED};

    public PlayerChannel(Player player, String name, String prefix){
        this.player = player;
        this.prefix = prefix;
        this.baseChannel = ChannelManager.getChannelByName(name);
        this.joinPlayer(player);
    }

    public PlayerChannel(Player player, String name){
        final ArrayList<String> usedPrefixes = ChannelManager.getUsedPrefix(player);
        for(Integer i = 0; i < 50; i++){ //TODO: Make this a config value
            if(!usedPrefixes.contains(i.toString())){
                this.prefix = i.toString();
                break;
            }
        }
        this.player = player;
        this.baseChannel = ChannelManager.getChannelByName(name);
        this.joinPlayer(player);
    }
    
    public Player getPlayer(){
        return this.player;
    }
    
    public void joinPlayer(Player player){
        if(baseChannel.isPlayerInChannel(player)){
            player.sendMessage(getFormattedEvent("You are already in this channel ("+this.baseChannel.getName()+")."));
            return;
        }

        this.baseChannel.joinPlayer(player,this);
        ChannelManager.markPrefixUsed(player,this.prefix);

        player.sendMessage(getFormattedEvent("You joined the channel ("+this.baseChannel.getName()+")."));
        this.baseChannel.broadcastExceptPlayer(player,player.getDisplayName() + " joined the channel.");
    }
    
    public void leavePlayer(Player player){
        if(!baseChannel.isPlayerInChannel(player)){
            player.sendMessage(getFormattedEvent("You are not in that channel ("+this.baseChannel.getName()+")."));
            return;
        }

        this.baseChannel.leavePlayer(player);
        ChannelManager.markPrefixAvailable(player, this.prefix);

        player.sendMessage(getFormattedEvent("You left the channel ("+this.baseChannel.getName()+")."));
        this.baseChannel.broadcastExceptPlayer(player,player.getDisplayName() + " left the channel.");
    }


    public String getMinecraftFormat(){
        return MessageFormat.format("{0}/{2}{1} <%s{0}{1}> %s",
                ChatColor.DARK_GRAY, //0
                this.getColor(), //1
                this.getPrefix() //2
        );
    }
    public String getFormattedEvent(String message){
        return MessageFormat.format("{0}/{2}{1} {3}",
                ChatColor.DARK_GRAY, //0
                this.getColor(), //1
                this.getPrefix(), //2
                message //3
        );
    }
    public String getFormattedMessage(Player player, String message){
        return MessageFormat.format("{0}/{2}{1} <{3}{1}> {4}",
                ChatColor.DARK_GRAY, //0
                this.getColor(), //1
                this.getPrefix(), //2
                player.getDisplayName(), //3
                message //4
        );
    }

    public BaseChannel getBaseChannel(){
        return baseChannel;
    }
    
    public ChatColor getColor(){
        if(this.prefix == "g") return ChatColor.GOLD;
        if(this.prefix == "!") return ChatColor.RED;
        if(this.prefix == "$") return ChatColor.GREEN;
        try{
            Integer index = Integer.parseInt(this.prefix);
            return colors[index % colors.length];
        } catch (NumberFormatException e){
            System.out.println("ChitChat> Prefix was not able to be colored" + this.prefix);
            return ChatColor.BLACK;
        } 
    }
    
    public String getPrefix(){
        return this.prefix;
    }

    public void broadcast(String message){
        this.player.sendMessage(getFormattedEvent(message));
    }
    public void sendMessage(Player player, String message){
        this.player.sendMessage(getFormattedMessage(player,message));
    }

    public void setDefault(){
        player.sendMessage(ChatColor.DARK_GRAY + "/" + prefix + " "+baseChannel.getName()+" is now your default channel.");
        ChannelManager.setDefaultPlayerChannel(this.player,this);
    }
}