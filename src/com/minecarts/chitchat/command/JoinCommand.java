package com.minecarts.chitchat.command;

import com.minecarts.chitchat.ChitChat;
import com.minecarts.chitchat.channel.PrefixChannel;
import com.minecarts.chitchat.manager.ChannelManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;


public class JoinCommand implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        switch(args.length){
            case 1: //Join a channel
                if(!(sender instanceof Player)) return true;
                if(!(sender.hasPermission("chitchat.channel.join"))){
                    sender.sendMessage("You are not allowed to join chat channels.");
                    return true;
                }
                Player player = (Player)sender;
                if(ChannelManager.isJoinLocked(player)){
                    player.sendMessage("Please wait a moment for your channels to load before joining additional channels.");
                    return true;
                }
                try{
                    Integer.parseInt(args[0]);
                    sender.sendMessage("Please join a channel with a name such as " + ChatColor.YELLOW + "/join MyName" + ChatColor.WHITE + ".");
                    return true;
                } catch (NumberFormatException e){
                    //It wasn't an int, so it's good to go
                }

                //TODO: Find a better way to do this, and or make these configuration values
                if(ChannelManager.isChannelNameJoinRestricted(args[0])){
                    sender.sendMessage("Access to that channel is restricted.");
                    return true;
                }

                PrefixChannel channel = new PrefixChannel(player,args[0]);
                if(channel.join(false,false)){
                    ChitChat.getPlugin().dbUpdateChannel(player, channel);
                    if(channel.setDefault()){
                        ChitChat.getPlugin().dbSetDefaultChannel(player,channel);
                    }
                }
                return true;
                
            case 2: //Force join a player
                if(!sender.hasPermission("chitchat.admin.force_join")) return false;
                List<Player> players = Bukkit.matchPlayer(args[0]);
                if(players.size() != 1) {
                    sender.sendMessage("Unable to force join, matched " + players.size() + " players.");
                    return true;
                }
                Player targetPlayer = players.get(0);

                if(ChannelManager.isJoinLocked(targetPlayer)){
                    sender.sendMessage(targetPlayer.getName() + "is still loading their channels. Try again in a few seconds.");
                    return true;
                }

                PrefixChannel forceChannel = new PrefixChannel(targetPlayer,args[1]);
                forceChannel.join(false,false);
                forceChannel.setDefault();
                ChitChat.getPlugin().dbUpdateChannel(targetPlayer, forceChannel);
                ChitChat.getPlugin().dbSetDefaultChannel(targetPlayer,forceChannel);

                sender.sendMessage("Force joined " + targetPlayer.getDisplayName() + " to " + args[1]);
                return true;
                
            default:
                return false;
        }
    }
}