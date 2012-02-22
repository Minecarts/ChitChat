package com.minecarts.chitchat.manager;

import com.minecarts.chitchat.channel.WhisperChannel;
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

    public static void setLastReceivedWhisper(Player player, WhisperChannel channel){
        //TODO: Add a delay here to prevent whisper stealings .. but make sure it's empty before doing the delay
        //TODO     eg a whisper in an empty tracker should result in an immediate insert so /r has no delay
        lastReceivedWhisperTracker.put(player,channel);
    }
    public static WhisperChannel getLastReceivedWhisper(Player player){
        return lastReceivedWhisperTracker.get(player);
    }
}
