package com.minecarts.chitchat.command;

import org.apache.commons.lang.StringUtils;

import com.minecarts.chitchat.channel.WhisperChannel;
import com.minecarts.chitchat.manager.WhisperManager;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.MessageFormat;

public class RewhisperCommand implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args.length == 0) return false;
        
        String message = StringUtils.join(args, " ");
        
        WhisperChannel channel = WhisperManager.getLastSentWhisper((Player) sender);
        if(channel == null){
            channel = WhisperManager.getLastReceivedWhisper((Player)sender);
            if(channel == null){
                sender.sendMessage("You must whisper a player before you can rewhisper.");
                return true;
            }
        }
        if(!channel.target().isOnline()){
            sender.sendMessage(MessageFormat.format("{0} is no longer online.",
                    channel.target().getName()
            ));
            return true;
        }
        channel.setDefault();
        channel.broadcastExceptPlayer(channel.getOwner(),channel.getOwner(),message);
        channel.displayOutbound(channel.target(), channel.formatMessage(channel.target(), message));
        //channel.broadcast(new Player[] {(Player) sender, channel.target()}, message);
        return true;
    }
}
