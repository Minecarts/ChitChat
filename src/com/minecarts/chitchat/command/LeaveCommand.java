package com.minecarts.chitchat.command;

import com.minecarts.chitchat.channel.PrefixChannel;
import com.minecarts.chitchat.manager.ChannelManager;
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
        PrefixChannel channel = ChannelManager.getChannelFromPrefix((Player)sender,args[0]);
        if(channel.isDefault()){ //Reset the default channel to global if they leave their default channel
            ChannelManager.getChannelFromPrefix((Player)sender,"g").setDefault();
        }
        channel.leave();
        return true;
    }
}
