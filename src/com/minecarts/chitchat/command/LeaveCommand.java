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

public class LeaveCommand implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        //Leave a channel
        if(args.length != 1) return false;
        if(!(sender instanceof Player)){
            sender.sendMessage("You cannot leave a channel as a non player");
            return true;
        }
        PrefixChannel channel = ChannelManager.getChannelFromPrefix((Player) sender, args[0]);
        if(channel == null){
            sender.sendMessage("You are in no channel with " + ChatColor.GRAY + "/" + args[0] + ChatColor.WHITE + " as the prefix.");
            return true;
        }
        if(channel.isDefault()){ //Reset the default channel to global if they leave their default channel
            ChannelManager.getChannelFromPrefix((Player)sender,"g").setDefault();
        }
        channel.leave();
        ((ChitChat) Bukkit.getPluginManager().getPlugin("ChitChat")).dbRemoveChannel((Player)sender, channel);
        return true;
    }
}
