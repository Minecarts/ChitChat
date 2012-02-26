package com.minecarts.chitchat.command;


import com.minecarts.chitchat.channel.AnnouncementChannel;
import com.minecarts.chitchat.channel.ChannelLink;
import com.minecarts.chitchat.manager.ChannelManager;
import helper.StringHelper;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AnnounceCommand implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args.length == 0) return false;
        
        String message = StringHelper.join(args, 0);
        ChannelLink link = ChannelManager.getOrCreateChannelLink("Announcement");
        link.relayMessage(new Player[]{},ChatColor.YELLOW + message);
        return true;
    }
}
