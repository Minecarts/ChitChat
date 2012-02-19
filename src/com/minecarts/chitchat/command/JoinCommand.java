package com.minecarts.chitchat.command;

import com.minecarts.chitchat.channel.PlayerChannel;
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
                    PlayerChannel selfChannel = new PlayerChannel(player,args[0]);
                    selfChannel.setDefault();

                    return true;
                case 2: //Force join a player
                    //TODO: Add permissions check
                    List<Player> players = Bukkit.matchPlayer(args[0]);
                    if(players.size() != 1) {
                        sender.sendMessage("Unable to force join, matched " + players.size() + " players.");
                        return true;
                    }
                    Player targetPlayer = players.get(0);
                    PlayerChannel forceChannel = new PlayerChannel(targetPlayer,args[1]);
                    forceChannel.setDefault();

                    sender.sendMessage("Force joined " + targetPlayer.getDisplayName() + " to " + args[1]);
                    return true;
                default:
                    return false;
            }
        }
}