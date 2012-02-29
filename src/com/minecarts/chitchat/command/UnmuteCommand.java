package com.minecarts.chitchat.command;

import com.minecarts.chitchat.channel.PrefixChannel;
import com.minecarts.chitchat.manager.ChannelManager;
import com.minecarts.chitchat.manager.GagManager;
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
        if(args.length != 1 && args.length != 2) return false;

        List<Player> matches = Bukkit.matchPlayer(args[0]);
        if(matches.size() > 1){
            sender.sendMessage("Unable to unmute " + ChatColor.YELLOW + args[0] + ChatColor.WHITE + ". Too many players matched.");
            return true;
        }
        if(matches.size() < 1){
            sender.sendMessage("Unable to unmute " + ChatColor.YELLOW + args[0] + ChatColor.WHITE + ". No online players matched.");
            return true;
        }

        Player matchedPlayer = matches.get(0);
        PrefixChannel global = ChannelManager.getChannelFromPrefix(matchedPlayer, "g");
        PrefixChannel announce = ChannelManager.getChannelFromPrefix(matchedPlayer, "!");
        PrefixChannel subscriber = ChannelManager.getChannelFromPrefix(matchedPlayer, "$");
        global.setCanChat(true);
        announce.setCanChat(true);
        if(subscriber != null){
            subscriber.setCanChat(true);
        }
        GagManager.ungag(matches.get(0));

        sender.sendMessage("You have unmuted " + matchedPlayer.getDisplayName() + ".");
        for(Player p : Bukkit.getOnlinePlayers()){
            if(!p.hasPermission("chitchat.admin.mute")) continue;
            if(p.equals(sender)) continue;
            p.sendMessage(sender.getName() + " unmuted " + matchedPlayer.getDisplayName() + ChatColor.WHITE + ".");
        }

        return true;
    }
}
