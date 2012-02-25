package com.minecarts.chitchat.manager;

import com.minecarts.chitchat.ChitChat;
import com.minecarts.chitchat.channel.WhisperChannel;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class WhisperManager {
    private static HashMap<Player, WhisperChannel> lastSentWhisperTracker = new HashMap<Player, WhisperChannel>();
    private static HashMap<Player, WhisperChannel> lastReceivedWhisperTracker = new HashMap<Player, WhisperChannel>();
    
    public static void setLastSentWhisper(Player player, WhisperChannel channel){
        lastSentWhisperTracker.put(player,channel);
    }
    public static WhisperChannel getLastSentWhisper(Player player){
        return lastSentWhisperTracker.get(player);
    }

    public static void setLastReceivedWhisper(final Player player, final WhisperChannel channel){
        if(lastReceivedWhisperTracker.get(player) == null){
            //No delay if there is no last whisper
            lastReceivedWhisperTracker.put(player,channel);
        } else {
            Bukkit.getScheduler().scheduleSyncDelayedTask(
                    (ChitChat)Bukkit.getPluginManager().getPlugin("ChitChat"),
                    new Runnable() {
                        public void run() {
                            lastReceivedWhisperTracker.put(player,channel);
                        }
                    },
                    20 * 5 //5 seconds, TODO: This should be a config
            );
        }
        
    }
    public static WhisperChannel getLastReceivedWhisper(Player player){
        return lastReceivedWhisperTracker.get(player);
    }
}
