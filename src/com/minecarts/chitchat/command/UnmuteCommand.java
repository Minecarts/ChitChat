package com.minecarts.chitchat.command;

import com.minecarts.chitchat.channel.PrefixChannel;
import com.minecarts.chitchat.manager.ChannelManager;
import com.minecarts.chitchat.manager.MuteManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class UnmuteCommand implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!sender.hasPermission("chitchat.admin.mute")) return true;
        
        for(String name : args) {
            if(name == null) continue;

            List<Player> matches = Bukkit.matchPlayer(name);
            if(matches.size() > 1){
                sender.sendMessage("Unable to unmute " + ChatColor.YELLOW + name + ChatColor.WHITE + ". Too many players matched.");
                continue;
            }
            if(matches.size() < 1){
                sender.sendMessage("Unable to unmute " + ChatColor.YELLOW + name + ChatColor.WHITE + ". No online players matched.");
                continue;
            }

            Player matchedPlayer = matches.get(0);
            MuteManager.unmute(matchedPlayer); //Store the gag to auto gag on login

            Bukkit.getServer().broadcast(sender.getName() + " unmuted " + matchedPlayer.getName() + ".","chitchat.admin.mute");
        }

        return true;
    }
}
