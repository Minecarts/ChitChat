package com.minecarts.chitchat.channel;

import com.minecarts.chitchat.ChitChat;
import com.minecarts.chitchat.manager.ChannelManager;
import com.minecarts.chitchat.manager.LanguageManager;
import com.minecarts.chitchat.manager.PluginManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.text.MessageFormat;
import java.util.*;

public class PrefixChannel extends Channel{

    private Player player;
    private String prefix;
    private boolean shouldSave = true;
    private ArrayList<ChatColor> colors = new ArrayList<ChatColor>();

    private void getColorsFromConfig(){
        List<String> colorNames = PluginManager.config().getStringList("colors");
        for(String color : colorNames){
            colors.add(ChatColor.valueOf(color));
        }
    }

    public PrefixChannel(Player player, String name, String prefix){
        super(player,name);
        getColorsFromConfig();

        this.player = player;
        this.prefix = prefix;

        //If it doesn't have a numeric prefix, this channel shouldn't be saveable
        try{
            Integer.parseInt(this.prefix);
        } catch (NumberFormatException e){
            shouldSave = false;
        }
    }

    public PrefixChannel(Player player, String name){
        super(player,name);
        getColorsFromConfig();

        final ArrayList<String> usedPrefixes = ChannelManager.getUsedPrefix(player);
        for(Integer i = 1; i < PluginManager.config().getInt("channel.prefix.max_channels"); i++){
            if(!usedPrefixes.contains(i.toString())){
                this.prefix = i.toString();
                shouldSave = true;
                break;
            }
        }
        this.player = player;
    }

    
    public Boolean shouldSave(){
        return this.shouldSave;
    }
    public void shouldSave(Boolean save){
        this.shouldSave = save;
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

        if(!suppressJoinSelf){
            this.display("You joined the channel (" + getName() + ").");
        }
        if(!suppressJoinOther){
            this.broadcastExceptPlayer(this.player, this.player.getDisplayName() + this.color() + " joined the channel.");
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
            this.broadcastExceptPlayer(this.player, this.player.getDisplayName() + this.color() +" left the channel.");
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
            return colors.get(index % colors.size());
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
