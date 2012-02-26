package com.minecarts.chitchat.command;

import com.minecarts.chitchat.ChitChat;
import com.minecarts.chitchat.channel.PrefixChannel;
import com.minecarts.chitchat.manager.ChannelManager;
import com.minecarts.chitchat.manager.GagManager;
import com.minecarts.chitchat.manager.PluginManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.logging.Level;

public class MuteCommand implements CommandExecutor {
    private ChitChat plugin;
    
    public MuteCommand(ChitChat plugin){
        this.plugin = plugin;
    }
    
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!sender.hasPermission("chitchat.admin.mute")) return true;
        if(args.length != 1 && args.length != 2) return false;
        
        int durationTicks = (20 * 60) * 15; 
        if(args.length == 2){
            try{
                durationTicks = Integer.parseInt(args[1]);
            } catch (NumberFormatException e){
                sender.sendMessage("Please specify a numerical duration in minutes.");
                return true;
            }
        }

        List<Player> matches = Bukkit.matchPlayer(args[0]);
        if(matches.size() > 1){
            sender.sendMessage("Unable to mute " + ChatColor.YELLOW + args[0] + ChatColor.WHITE + ". Too many players matched.");
            return true;
        }
        if(matches.size() < 1){
            sender.sendMessage("Unable to mute " + ChatColor.YELLOW + args[0] + ChatColor.WHITE + ". No online players matched.");
            return true;
        }
        
        final Player matchedPlayer = matches.get(0);
        final PrefixChannel global = ChannelManager.getChannelFromPrefix(matchedPlayer,"g");
        final PrefixChannel announcement = ChannelManager.getChannelFromPrefix(matchedPlayer,"!");
        global.canChat(false);
        announcement.canChat(false);
        GagManager.gag(matchedPlayer); //Store the gag to auto gag on login

        Integer minutes = (durationTicks / 20 / 60);
        sender.sendMessage("You have muted " + matchedPlayer.getDisplayName() + " for " + minutes + " minutes");
        for(Player p : Bukkit.getOnlinePlayers()){
            if(!p.hasPermission("chitchat.admin.mute")) continue;
            if(p.equals(sender)) continue;
            p.sendMessage(sender.getName() + " muted " + matchedPlayer.getDisplayName() + ChatColor.WHITE + " for " + minutes + " minutes.");
        }
        matchedPlayer.sendMessage(PluginManager.config().getString("messages.PLAYER_GAGGED"));
        
        Bukkit.getScheduler().scheduleAsyncDelayedTask(plugin,new Runnable() {
            public void run() {
                if(matchedPlayer.isOnline()){
                    plugin.getLogger().log(Level.INFO,"Player mute time expired for " + global.getOwner());
                    global.canChat(true);
                    announcement.canChat(true);
                }
                GagManager.ungag(matchedPlayer);
            }
        },durationTicks);
        
        return true;
    }
}
