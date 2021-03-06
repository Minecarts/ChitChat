package com.minecarts.chitchat.command;

import org.apache.commons.lang.StringUtils;

import com.minecarts.chitchat.channel.WhisperChannel;
import com.minecarts.chitchat.manager.WhisperManager;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.MessageFormat;

public class ReplyCommand implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args.length == 0) return false;
        
        String message = StringUtils.join(args, " ");
        
        WhisperChannel channel = WhisperManager.getLastReceivedWhisper((Player) sender);
        if(channel == null){
            //Try to do a rewhisper
            channel = WhisperManager.getLastSentWhisper((Player) sender);
            if(channel == null){
                sender.sendMessage("A player must whisper you before you can reply.");
                return true;
            }
        }
        if(!channel.target().isOnline()){
            sender.sendMessage(MessageFormat.format("{0} is no longer online.",
                    channel.target().getName()
                    ));
            return true;
        }
        if(channel.setDefault()){
            //Default set to whispers
        }
        channel.broadcast(new Player[] {(Player) sender}, message);
        return true;
    }
}
