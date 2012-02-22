package com.minecarts.chitchat.command;

import com.minecarts.chitchat.channel.WhisperChannel;
import com.minecarts.chitchat.manager.WhisperManager;
import helper.StringHelper;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ReplyCommand implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args.length == 0) return false;
        String message = StringHelper.join(args, 0);
        WhisperChannel channel = WhisperManager.getLastReceivedWhisper((Player)sender);
        if(channel == null){
            sender.sendMessage("You have received no whispers recently.");
            return true;
        }
        channel.setDefault();
        channel.broadcast((Player) sender, message);
        return true;
    }
}
