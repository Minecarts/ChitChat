package com.minecarts.chitchat.command;

import com.minecarts.chitchat.channel.WhisperChannel;
import com.minecarts.chitchat.manager.IgnoreManager;
import com.minecarts.chitchat.manager.WhisperManager;
import helper.StringHelper;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.MessageFormat;
import java.util.List;

public class WhisperCommand implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String message = StringHelper.join(args, 1);
        if(message.length() == 0){ return false; }
        List<Player> playermatches = Bukkit.matchPlayer(args[0]);
        String senderName = sender.getName();

        if(sender instanceof Player){
            senderName = ((Player) sender).getDisplayName();
        }

        //source.sendMessage();
        //destination.sendMessage();

        int numMatches = playermatches.size();
        if(numMatches > 1){
            sender.sendMessage("There were " + numMatches + " players matching \""+args[0]+"\". Please be more specific.");
        } else if (numMatches == 1) {
            Player receiver = playermatches.get(0);
            if(sender.getName().equalsIgnoreCase(receiver.getName())){
                sender.sendMessage("You can not whisper yourself.");
                return true;
            }
            
            if(IgnoreManager.isIgnoring(receiver,sender.getName())){
                sender.sendMessage(receiver.getDisplayName() + " is ignoring you.");
                return true;
            }
            
            WhisperChannel senderChannel = new WhisperChannel((Player)sender,"Whisper-"+sender.getName() + "-"+receiver.getName());
            WhisperChannel receiverChannel = new WhisperChannel(receiver,"Whisper-"+sender.getName() + "-"+receiver.getName());
            //senderChannel.broadcast((Player) sender, message); //Broadcast to the sender channel, so that the sender message displays special
            senderChannel.displayOutbound(receiver,message);
            receiverChannel.displayInbound((Player) sender, message);
        } else {
            sender.sendMessage("Could not find anyone online by that name.");
        }

        return true;
    }
}
