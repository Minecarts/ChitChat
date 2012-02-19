package com.minecarts.chitchat.command;

import com.minecarts.chitchat.channel.PlayerChannel;
import com.minecarts.chitchat.manager.ChannelManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class LeaveCommand implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        //Leave a channel
        return true;
    }
}
