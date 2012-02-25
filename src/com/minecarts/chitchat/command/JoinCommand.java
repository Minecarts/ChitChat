package com.minecarts.chitchat.command;

import com.minecarts.chitchat.ChitChat;
import com.minecarts.chitchat.channel.PrefixChannel;
import com.minecarts.chitchat.manager.ChannelManager;
import org.bukkit.Bukkit;
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

                    Player player = (Player)sender;
                    //TODO: Add check for certain things, like numetic only channels, etc

                    //TODO: Find a better way to do this, and or make these configuration values
                    if(ChannelManager.isChannelNameJoinRestricted(args[0])){
                        sender.sendMessage("Access to that channel is restricted.");
                        return true;
                    }

                    PrefixChannel channel = new PrefixChannel(player,args[0]);
                    channel.setDefault();
                    channel.join(false,false);
                    ((ChitChat)Bukkit.getPluginManager().getPlugin("ChitChat")).dbUpdateChannel(player, channel);
                    return true;
                case 2: //Force join a player
                    //TODO: Add permissions check
                    List<Player> players = Bukkit.matchPlayer(args[0]);
                    if(players.size() != 1) {
                        sender.sendMessage("Unable to force join, matched " + players.size() + " players.");
                        return true;
                    }
                    Player targetPlayer = players.get(0);
                    PrefixChannel forceChannel = new PrefixChannel(targetPlayer,args[1]);
                    forceChannel.join(false,false);
                    forceChannel.setDefault();
                    ((ChitChat)Bukkit.getPluginManager().getPlugin("ChitChat")).dbUpdateChannel(targetPlayer, forceChannel);

                    sender.sendMessage("Force joined " + targetPlayer.getDisplayName() + " to " + args[1]);
                    return true;
                default:
                    return false;
            }
        }
}