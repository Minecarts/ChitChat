package com.minecarts.chitchat.command;

import org.apache.commons.lang.StringUtils;

import com.minecarts.chitchat.ChitChat;
import com.minecarts.chitchat.channel.WhisperChannel;
import com.minecarts.chitchat.manager.IgnoreManager;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class HintsCommand implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!sender.hasPermission("chitchat.admin.hint")) return false;
        
        String message = StringUtils.join(args, " ", 1, args.length);
        if(message.length() == 0) return false;
        
        List<Player> playerMatches = Bukkit.matchPlayer(args[0]);

        int numMatches = playerMatches.size();
        if(numMatches > 1){
            sender.sendMessage("There were " + numMatches + " players matching \""+args[0]+"\". Please be more specific.");
        } else if (numMatches == 1) {
            Player player = playerMatches.get(0);
            player.sendMessage(ChatColor.AQUA + "[TIP] " + ChatColor.GRAY + message);
            //sender.sendMessage(ChatColor.GRAY + " > " + player.getDisplayName() + ": " + message);
            Bukkit.broadcast(ChatColor.GRAY + "Tip: " + sender.getName() + " > " + player.getName() + ": " + message, "chitchat.admin.hint");
        } else {
            sender.sendMessage("Could not find anyone online by that name.");
        }

        return true;
    }
}
