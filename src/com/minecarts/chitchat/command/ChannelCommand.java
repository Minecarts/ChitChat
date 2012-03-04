package com.minecarts.chitchat.command;

import com.minecarts.chitchat.ChitChat;
import com.minecarts.chitchat.channel.Channel;
import com.minecarts.chitchat.channel.PrefixChannel;
import com.minecarts.chitchat.manager.ChannelManager;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.MessageFormat;
import java.util.ArrayList;

public class ChannelCommand implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args.length == 0){
            //Display a helpful message
            sender.sendMessage("Type " + ChatColor.YELLOW + "/help" + ChatColor.WHITE +" for a list of channel commands.");
            return true;
        }

        if(args[0].equalsIgnoreCase("reload")){
            if(!sender.hasPermission("chitchat.admin.reload")) return true;
            ChitChat.getPlugin().reloadConfig();
            sender.sendMessage("ChitChat config reloaded.");
            return true;
        }

        if(args[0].equalsIgnoreCase("list") || args[0].equalsIgnoreCase("who")){
            Player player = null;
            if(args.length == 1 && sender instanceof Player){
                player = (Player)sender;
            } else if (args.length == 2 && Bukkit.getPlayer(args[1]) != null){
                if(!sender.hasPermission("chitchat.admin.list")){
                    return false;
                }
                player = Bukkit.getPlayer(args[1]);
            }

            if(player == null){
                //It might be a prefix, so lets check it
                if(sender instanceof Player && args[1].length() == 1){
                    PrefixChannel channel = ChannelManager.getChannelFromPrefix((Player)sender,args[1]);
                    if(channel == null){
                        sender.sendMessage("No channel found with the prefix " + args[1]);
                        return true;
                    }
                    sender.sendMessage(channel.color() + "---- Players in " +channel.getName() +" (" + channel.getPrefix() + ") -----");
                    sender.sendMessage(StringUtils.join(channel.getMemberNames((Player)sender),", "));
                    return true;
                } else {
                    sender.sendMessage("Could not query player channels. No player found?");
                    return true;
                }
            }

            ArrayList<Channel> channels = ChannelManager.getPlayerChannels(player);
            sender.sendMessage("---- Channel Listing for " + player.getDisplayName() + " ----");
            for(Channel channel : channels){
                if(channel instanceof PrefixChannel){
                    PrefixChannel prefixChannel = (PrefixChannel) channel;
                    sender.sendMessage(MessageFormat.format("{0} {1} ({2} players)",
                            prefixChannel.getPrefix(), //0
                            prefixChannel.getName(), //1
                            prefixChannel.getMembers().size() //2
                    ));
                }
            }
            return true;
        }
        return false;
    }
}
