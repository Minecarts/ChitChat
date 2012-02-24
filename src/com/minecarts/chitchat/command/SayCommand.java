package com.minecarts.chitchat.command;

import com.minecarts.chitchat.channel.Channel;
import com.minecarts.chitchat.channel.LocalChannel;
import com.minecarts.chitchat.manager.ChannelManager;
import helper.StringHelper;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SayCommand implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String message = StringHelper.join(args, 0);
        if(message.length() == 0){ return false; }
        if(sender instanceof Player){
            Player player = (Player) sender;
            for(Channel channel : ChannelManager.getPlayerChannels(player)){
                if(channel instanceof LocalChannel){
                    LocalChannel lc = (LocalChannel) channel;
                    lc.setDefault();
                    lc.broadcast(new Player[] {(Player) sender},message);
                    return true;
                }
            }
        }

        return false;
    }
}
