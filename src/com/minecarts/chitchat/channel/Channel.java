package com.minecarts.chitchat.channel;

import com.minecarts.chitchat.manager.ChannelManager;
import com.minecarts.chitchat.manager.IgnoreManager;
import com.minecarts.chitchat.manager.LanguageManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

abstract public class Channel {
    private Player owningPlayer; //The player this channel belongs to
    private ChannelLink channelLink;
    private String name;
    private ChatColor color = null;

    private boolean canChat = true;

    public Channel(Player owner, String name){
        this.owningPlayer = owner;
        this.name = name;
        this.setLink(ChannelManager.getOrCreateChannelLink(name));
    }

    public String getName(){
        return this.name;
    }
    public Player getOwner(){
        return this.owningPlayer;
    }

    public ChatColor color(){
        return this.color;
    }
    public void color(ChatColor color){
        this.color = color;
    }
    
    
    public Set<Player> getMembers(){
        return this.getLink().getMembers();
    }
    
    public Set<String> getMemberNames(){
        Set<String> members = new HashSet<String>();
        for(Player p : this.getLink().getMembers()){
            members.add(p.getDisplayName());
        }
        return members;
    }
    
    protected ChannelLink getLink(){
        return this.channelLink;
    }
    protected void setLink(ChannelLink link){
        this.channelLink = link;
    }

    public Boolean join(){
        getLink().joinPlayer(this.owningPlayer, this);
        ChannelManager.addPlayerChannel(this.owningPlayer, this);
        return true;
    }
    public Boolean leave(){
        getLink().leavePlayer(this.owningPlayer);
        ChannelManager.removePlayerChannel(this.owningPlayer, this);
        return true;
    }
    public Boolean leaveWithoutClear(){
        getLink().leavePlayer(this.owningPlayer);
        return true;
    }

    //Outbound
    public void broadcastExceptPlayer(Player exception, String message){
        if(!this.canChat()) return;
        getLink().relayMessageExceptPlayer(exception,message);
    }
    public void broadcast(Player[] taggedPlayers, String format, String... args){
        if(!this.canChat()) return;
        getLink().relayMessage(taggedPlayers, format, args);
    }
    public void broadcast(Player[] taggedPlayers, String message){
        if(!this.canChat()) return;
        getLink().relayMessage(taggedPlayers,message);
    }
    public void broadcast(String message){
        if(!this.canChat()) return;
        getLink().relayMessage(message);
    }



    public void displayInbound(Player player, String message){
        String formattedMessage = formatMessage(player, LanguageManager.filter(this.color(),message));
        if(formattedMessage != null){
            getOwner().sendMessage(formattedMessage);
        }
    }
    public void displayOutbound(Player player, String message){
        String formattedMessage = formatMessage(player, LanguageManager.filter(this.color(),message));
        if(formattedMessage != null){
            getOwner().sendMessage(formattedMessage);
        }
    }
    public void display(String message){
        String formattedMessage = formatMessage(message);
        if(formattedMessage != null){
            getOwner().sendMessage(formattedMessage);
        }
    }

    abstract protected String formatMessage(String message);
    abstract protected String formatMessage(Player player, String message);

    public Boolean setDefault(){
        if(!this.canChat()) return false; //Never set default for a channel they can't chat in
        ChannelManager.setDefaultPlayerChannel(this.owningPlayer,this);
        return true;
    }

    public Boolean isDefault(){
        return (ChannelManager.getDefaultPlayerChannel(this.owningPlayer).getName().equalsIgnoreCase(this.getName()));
    }
    
    public Boolean canChat(){
        if(!this.canChat){
            this.display("You've been muted and can not chat in this channel."); //TODO: message from config
        }
        return this.canChat;
    }
    public void canChat(Boolean chatFlag){
        this.canChat = chatFlag;
    }
}
