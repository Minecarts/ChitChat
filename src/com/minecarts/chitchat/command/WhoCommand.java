package com.minecarts.chitchat.command;

import com.minecarts.chitchat.channel.BaseChannel;
import com.minecarts.chitchat.channel.PlayerChannel;
import com.minecarts.chitchat.manager.ChannelManager;
import com.minecarts.chitchat.manager.IgnoreManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class WhoCommand implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        switch(args.length){
            case 0:
                for(Player p : Bukkit.getOnlinePlayers()){
                    if(sender instanceof Player){
                        Player commandPlayer = (Player)sender;
                        if(!commandPlayer.canSee(p)) continue;
                        if(IgnoreManager.isIgnoring(commandPlayer,p)) continue;
                    }
                    //TODO: Clean up this display (into a CSV, with worlds??)
                    sender.sendMessage(p.getDisplayName());
                }
                return true;
            case 1:
                //Try and match a player from the args
                 Player p = Bukkit.getPlayer(args[1]);
                if(p != null && sender.hasPermission("chitchat.who.detail")){
                    sender.sendMessage("TODO: Detailed player info!");
                    return true;
                }
                
                //Else try and match this arg to a player's channel
                if(sender instanceof Player){
                    //By index
                    PlayerChannel channel = ChannelManager.getPlayerChannelFromPrefix((Player)sender,args[1]);
                    for(Player channelMember : channel.getBaseChannel().getMembers()){
                        sender.sendMessage(channelMember.getDisplayName());
                    }
                    return true;
                } else { //It's a console, so list the members of the channel, but by name
                    BaseChannel channel = ChannelManager.getChannelByName(args[1]);
                    if(channel == null){
                        sender.sendMessage("Unable to find a channel with the name: " + args[1]);
                        return true;
                    }
                    
                    for(Player channelMember : channel.getMembers()){
                        sender.sendMessage(channelMember.getDisplayName());
                    }
                }
            default:
                return false;
        }
    }
}
