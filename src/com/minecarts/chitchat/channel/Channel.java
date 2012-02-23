package com.minecarts.chitchat.channel;

import com.minecarts.chitchat.manager.ChannelManager;
import com.minecarts.chitchat.manager.IgnoreManager;
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

    public void join(){
        getLink().joinPlayer(this.owningPlayer,this);
        ChannelManager.addPlayerChannel(this.owningPlayer,this);
    }
    public void leave(){
        getLink().leavePlayer(this.owningPlayer);
        ChannelManager.removePlayerChannel(this.owningPlayer, this);
    }

    //Outbound
    public void broadcastExceptPlayer(Player exception, String message){
        getLink().relayMessageExceptPlayer(exception,message);
    }
    public void broadcast(Player player, String message){
        if(!this.canChat()){
            this.display("You've been muted and can not chat in this channel."); //TODO: message from config
            return;
        }
        getLink().relayMessage(player,message);
    }
    public void broadcast(String message){
        if(!this.canChat()){
            this.display("You've been muted and can not chat in this channel."); //TODO: message from config
            return;
        }
        getLink().relayMessage(message);
    }


    public void displayInbound(Player player, String message){
        if(IgnoreManager.isIgnoring(getOwner(),player.getName())) return; //Ignore inbound messages from players
        String formattedMessage = formatMessage(player, message);
        if(formattedMessage != null){
            getOwner().sendMessage(formattedMessage);
        }
    }
    public void displayOutbound(Player player, String message){
        String formattedMessage = formatMessage(player, message);
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

    public void setDefault(){
        ChannelManager.setDefaultPlayerChannel(this.owningPlayer,this);
    }

    public Boolean isDefault(){
        return (ChannelManager.getDefaultPlayerChannel(this.owningPlayer).getName().equalsIgnoreCase(this.getName()));
    }
    
    public Boolean canChat(){
        return this.canChat;
    }
    public void canChat(Boolean chatFlag){
        this.canChat = chatFlag;
    }
}
