package com.minecarts.chitchat.command;

import com.minecarts.chitchat.channel.WhisperChannel;
import helper.StringHelper;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.MessageFormat;
import java.util.List;

public class WhisperCommand implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String message = StringHelper.join(args, 1);
        if(message.length() == 0){ return false; }
        List<Player> playermatches = Bukkit.matchPlayer(args[0]);
        String senderName = sender.getName();

        if(sender instanceof Player){
            senderName = ((Player) sender).getDisplayName();
        }

        int numMatches = playermatches.size();
        if(numMatches > 1){
            sender.sendMessage("There were " + numMatches + " players matching \""+args[0]+"\". Please be more specific.");
        } else if (numMatches == 1) {
            Player receiver = playermatches.get(0);
            WhisperChannel channel = new WhisperChannel((Player)sender,receiver);
            channel.sendMessage((Player)sender,message);
            //channel.sendMessage((Player)sender,);

        } else {
            sender.sendMessage("Could not find anyone online by that name.");
        }

        return true;
    }
}
