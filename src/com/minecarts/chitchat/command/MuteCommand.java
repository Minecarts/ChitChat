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

        int argCounter = 1;
        int durationTicks = (20 * 60) * 15;
        Boolean setTicks = false;
        try{
            durationTicks = Integer.parseInt(args[args.length - 1]) * 20 * 60;
            setTicks = true;
        } catch (NumberFormatException e){ }

        for(String name : args){
            if(argCounter++ == args.length && setTicks){ break; }

            List<Player> matches = Bukkit.matchPlayer(name);
            if(matches.size() > 1){
                sender.sendMessage("Unable to mute " + ChatColor.YELLOW + name + ChatColor.WHITE + ". Too many players matched.");
                continue;
            }
            if(matches.size() < 1){
                sender.sendMessage("Unable to mute " + ChatColor.YELLOW + name + ChatColor.WHITE + ". No online players matched.");
                continue;
            }

            final Player matchedPlayer = matches.get(0);
            final PrefixChannel global = ChannelManager.getChannelFromPrefix(matchedPlayer,"g");
            final PrefixChannel announcement = ChannelManager.getChannelFromPrefix(matchedPlayer,"!");
            final PrefixChannel subscriber = ChannelManager.getChannelFromPrefix(matchedPlayer,"$");
            global.setCanChat(false);
            announcement.setCanChat(false);
            if(subscriber != null){
                subscriber.setCanChat(false);
            }

            if(!GagManager.isGagged(matchedPlayer)){
                matchedPlayer.sendMessage(PluginManager.config().getString("messages.PLAYER_GAGGED"));
            }

            GagManager.gag(matchedPlayer); //Store the gag to auto gag on login

            Integer minutes = (durationTicks / 20 / 60);
            sender.sendMessage("You have muted " + matchedPlayer.getDisplayName() + ChatColor.WHITE + " for " + minutes + " minutes");
            for(Player p : Bukkit.getOnlinePlayers()){
                if(!p.hasPermission("chitchat.admin.mute")) continue;
                if(p.equals(sender)) continue;
                p.sendMessage(sender.getName() + " muted " + matchedPlayer.getDisplayName() + ChatColor.WHITE + " for " + minutes + " minutes.");
            }

            Bukkit.getScheduler().scheduleAsyncDelayedTask(plugin,new Runnable() {
                public void run() {
                    if(matchedPlayer.isOnline()){
                        plugin.getLogger().log(Level.INFO,"Player mute time expired for " + global.getOwner());
                        global.setCanChat(true);
                        announcement.setCanChat(true);
                        if(subscriber != null){
                            subscriber.setCanChat(true);
                        }
                    }
                    GagManager.ungag(matchedPlayer);
                }
            },durationTicks);
        }
        return true;
    }
}
