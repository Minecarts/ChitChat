package com.minecarts.chitchat.command;

import com.minecarts.chitchat.ChitChat;
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
import java.util.logging.Level;

public class MuteCommand implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!sender.hasPermission("chitchat.admin.mute")) return true;

        double minutes = 15;
        try{
            minutes = Double.parseDouble(args[0]);
            args[0] = null;
        } catch (NumberFormatException e) {
            // first arg not a number?
        }

        for(String name : args) {
            if(name == null) continue;

            List<Player> matches = Bukkit.matchPlayer(name);
            if(matches.size() > 1){
                sender.sendMessage("Unable to mute " + ChatColor.YELLOW + name + ChatColor.WHITE + ". Too many players matched.");
                continue;
            }
            if(matches.size() < 1){
                sender.sendMessage("Unable to mute " + ChatColor.YELLOW + name + ChatColor.WHITE + ". No online players matched.");
                continue;
            }

            Player matchedPlayer = matches.get(0);

            if(!MuteManager.isMuted(matchedPlayer)){
                matchedPlayer.sendMessage(ChitChat.getPlugin().getConfig().getString("messages.PLAYER_MUTED"));
            }

            MuteManager.mute(matchedPlayer, (long) (1000 * 60 * minutes)); //Store the gag to auto gag on login
            
            for(Player p : Bukkit.getOnlinePlayers()){
                if(p.hasPermission("chitchat.admin.mute")) {
                    p.sendMessage(sender.getName() + " muted " + matchedPlayer.getName() + " for " + minutes + " minutes.");
                }
            }
        }
        
        return true;
    }
}
