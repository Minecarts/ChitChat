package com.minecarts.chitchat.command;

import com.minecarts.chitchat.channel.Channel;
import com.minecarts.chitchat.channel.LocalChannel;
import com.minecarts.chitchat.manager.ChannelManager;
import helper.StringHelper;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SayCommand implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String message = StringHelper.join(args, 0);
        if(sender instanceof Player){
            Player player = (Player) sender;
            for(Channel channel : ChannelManager.getPlayerChannels(player)){
                if(channel instanceof LocalChannel){
                    LocalChannel lc = (LocalChannel) channel;
                    if(lc.setDefault()){
                        //Set default;
                        if(message.length() == 0){
                            //Oh look ma! A feedback message that's totally hacked!
                            sender.sendMessage(ChatColor.GRAY + ":" + ChatColor.DARK_GRAY + "/s [Local Chat] is now your default channel.");
                        }
                    }
                    if(message.length() > 0){
                        lc.broadcast(new Player[] {(Player) sender},message);
                    }
                    return true;
                }
            }
        } else {
            Bukkit.broadcastMessage(ChatColor.YELLOW + "[Server] " + message);
            return true;
        }

        return false;
    }
}
