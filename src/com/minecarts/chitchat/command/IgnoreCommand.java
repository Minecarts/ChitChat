package com.minecarts.chitchat.command;

import com.minecarts.chitchat.ChitChat;
import com.minecarts.chitchat.manager.IgnoreManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

import helper.StringHelper;

public class IgnoreCommand implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)){
            sender.sendMessage("Ignoring is not supported by the console.");
            return true;
        }
        Player player = (Player) sender;
        
        
        if(args.length == 0 || args[0].equalsIgnoreCase("list")) {
            String[] names = IgnoreManager.getIgnoreList(player);
            if(names.length == 0) return false;
            player.sendMessage("You are ignoring: " + StringHelper.join(names, ", "));
            return true;
        }
        
        
        List<Player> matches = Bukkit.matchPlayer(args[0]);

        String ignoreText = (label.equals("unignore")) ? "unignore" : "ignore";
        
        if(matches.size() > 1){
            sender.sendMessage("Unable to " + ignoreText + " " + ChatColor.YELLOW + args[0] + ChatColor.WHITE + ". Too many players matched.");
            return true;
        }
        if(matches.size() < 1){
            sender.sendMessage("Unable to " + ignoreText + " " + ChatColor.YELLOW + args[0] + ChatColor.WHITE + ". No online players matched.");
            return true;
        }

        Player foundPlayer = matches.get(0);

        if(IgnoreManager.isIgnoring(player,foundPlayer.getName())){
            IgnoreManager.unignorePlayer(player,foundPlayer.getName());
            sender.sendMessage("You have " + ChatColor.YELLOW + "unignored " + ChatColor.WHITE + foundPlayer.getDisplayName() + ChatColor.WHITE + ".");
            ((ChitChat)Bukkit.getPluginManager().getPlugin("ChitChat")).dbRemoveIgnore(player,foundPlayer.getName());
            return true;
        } else {
            if(label.equalsIgnoreCase("unignore")){
                sender.sendMessage("You are not currently ignoring " + foundPlayer.getDisplayName());
                return true;
            }
            if(foundPlayer.hasPermission("chitchat.ignore.immunity")){
                sender.sendMessage("You can not ignore an administrator.");
                return true;
            }
            IgnoreManager.ignorePlayer(player,foundPlayer.getName());
            sender.sendMessage("You have " + ChatColor.YELLOW + "ignored " + ChatColor.WHITE + foundPlayer.getDisplayName() + ChatColor.WHITE + ".");
            ((ChitChat)Bukkit.getPluginManager().getPlugin("ChitChat")).dbAddIgnore(player,foundPlayer.getName());
            return true;
        }
    }
}
