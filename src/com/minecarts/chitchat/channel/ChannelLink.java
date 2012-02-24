package com.minecarts.chitchat.channel;

import com.minecarts.chitchat.manager.ChannelManager;
import com.minecarts.chitchat.manager.IgnoreManager;
import com.minecarts.chitchat.manager.SpamManager;
import org.bukkit.entity.Player;

import java.text.MessageFormat;
import java.util.*;

public class ChannelLink {
    private HashMap<Player, Channel> members = new HashMap<Player, Channel>();
    private String uniqueId;
    private String name;

    public ChannelLink(String name){
        this.name = name;
        this.uniqueId = name.toLowerCase();
        ChannelManager.createChannelLink(this);
    }
    
    public String getName(){
        return this.name;
    }
    public String getId(){
        return this.uniqueId;
    }

    public Boolean isPlayerInChannel(Player player){
        return this.members.containsKey(player);
    }
    
    public void joinPlayer(Player player, Channel playerChannel){
        this.members.put(player, playerChannel);
    }

    public void leavePlayer(Player player){
        if(!this.members.containsKey(player)) return;
        this.members.remove(player);
    }


    public Set<Player> getMembers(){
        return this.members.keySet();
    }

    
    public Channel getPlayerChannel(Player player){
        return this.members.get(player);
    }


    public void relayMessageExceptPlayer(Player exception, String message){
        for(Channel channel : this.members.values()){
            if(channel.getOwner().equals(exception)){
                continue;
            }
            channel.display(message);
        }
    }
    public void relayMessage(Player[] taggedPlayers, String format, String... args){
        if(taggedPlayers != null && taggedPlayers.length > 0){
            SpamManager.checkSpam(taggedPlayers[0]);
        }
        for(Channel channel : this.members.values()){
            List<Player> taggedList = Arrays.asList(taggedPlayers);

            //Check ignores
            boolean hasIgnore = false;
            for(Player p : taggedPlayers){
                if(IgnoreManager.isIgnoring(channel.getOwner(), p.getName())){
                    hasIgnore = true;
                    break;
                };
            }
            if(hasIgnore) continue; //Skip this message if the player is ignoring them

            List<String> varargs = new ArrayList<String>();
            varargs.add(channel.color().toString());
            if(args != null){
                varargs.addAll(Arrays.asList(args));
            }
            String message = MessageFormat.format(format, varargs.toArray());


            if(taggedList.size() == 0){
                channel.display(message);
                continue;
            }
            if(taggedList.contains(channel.getOwner())){
                channel.displayOutbound(taggedList.get(0),message); //The primary player is index 0
                continue;
            }
            channel.displayInbound(taggedList.get(0), message);
        }
    }
    public void relayMessage(Player[] taggedPlayers, String message){
        relayMessage(taggedPlayers,message,null);
    }
    public void relayMessage(String message){
        relayMessage(null,message,null);
    }
}
