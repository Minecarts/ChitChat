package com.minecarts.chitchat.command;

import com.minecarts.chitchat.channel.WhisperChannel;
import com.minecarts.chitchat.manager.WhisperManager;
import helper.StringHelper;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RewhisperCommand implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args.length == 0) return false;
        String message = StringHelper.join(args, 0);
        WhisperChannel channel = WhisperManager.getLastSentWhisper((Player) sender);
        if(channel == null){
            sender.sendMessage("You have sent no whispers yet.");
            return true;
        }
        channel.setDefault();
        channel.broadcast(new Player[] {(Player) sender}, message);
        return true;
    }
}
