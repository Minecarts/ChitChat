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

import org.apache.commons.lang.StringUtils;

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
            player.sendMessage("You are ignoring: " + StringUtils.join(names, ", "));
            return true;
        }
        

        String ignoreText = (label.equals("unignore")) ? "unignore" : "ignore";

        for(String name : args) {
            List<Player> matches = Bukkit.matchPlayer(name);

            if(matches.size() > 1){
                sender.sendMessage("Unable to " + ignoreText + " " + ChatColor.YELLOW + name + ChatColor.WHITE + ". Too many players matched.");
                continue;
            }
            if(matches.size() < 1){
                sender.sendMessage("Unable to " + ignoreText + " " + ChatColor.YELLOW + name + ChatColor.WHITE + ". No online players matched.");
                continue;
            }

            Player foundPlayer = matches.get(0);

            if(IgnoreManager.isIgnoring(player,foundPlayer.getName())){
                IgnoreManager.unignorePlayer(player,foundPlayer.getName());
                sender.sendMessage("You have " + ChatColor.YELLOW + "unignored " + ChatColor.WHITE + foundPlayer.getDisplayName() + ChatColor.WHITE + ".");
                ChitChat.getPlugin().dbRemoveIgnore(player,foundPlayer.getName());
                continue;
            }
            if(label.equalsIgnoreCase("unignore")){
                sender.sendMessage("You are not currently ignoring " + foundPlayer.getDisplayName());
                continue;
            }
            if(foundPlayer.hasPermission("chitchat.ignore.immunity")){
                sender.sendMessage("You can not ignore an administrator.");
                continue;
            }
            
            IgnoreManager.ignorePlayer(player,foundPlayer.getName());
            sender.sendMessage("You have " + ChatColor.YELLOW + "ignored " + ChatColor.WHITE + foundPlayer.getDisplayName() + ChatColor.WHITE + ".");
            ChitChat.getPlugin().dbAddIgnore(player,foundPlayer.getName());
        }
        
        return true;
    }
}
