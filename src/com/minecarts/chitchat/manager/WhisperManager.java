package com.minecarts.chitchat.manager;

import java.util.Map;
import java.util.HashMap;
import java.util.Date;

import com.minecarts.chitchat.ChitChat;
import com.minecarts.chitchat.channel.WhisperChannel;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;


public class WhisperManager {
    private static Map<Player, WhisperChannel> lastSent = new HashMap<Player, WhisperChannel>();
    private static Map<Player, DatedWhisper> lastReceived = new HashMap<Player, DatedWhisper>();
    
    public static void setLastSentWhisper(Player player, WhisperChannel channel){
        lastSent.put(player,channel);
    }
    public static WhisperChannel getLastSentWhisper(Player player){
        return lastSent.get(player);
    }

    public static void setLastReceivedWhisper(final Player player, final WhisperChannel channel){
        DatedWhisper lastWhisper = lastReceived.get(player);
        
        if(lastWhisper == null
                || lastWhisper.getWhisper().target().equals(channel.target())
                || lastWhisper.elapsed() > 1000 * ChitChat.getPlugin().getConfig().getInt("channel.whisper.interrupt_timeout")) {
            
            lastReceived.put(player, new DatedWhisper(channel));
            
        }
    }
    public static WhisperChannel getLastReceivedWhisper(Player player) {
        DatedWhisper lastWhisper = lastReceived.get(player);
        return lastWhisper == null ? null : lastWhisper.getWhisper();
    }
    
    
    static class DatedWhisper {
        private final WhisperChannel whisper;
        private final Date date;
        
        public DatedWhisper(WhisperChannel whisper) {
            this(whisper, new Date());
        }
        public DatedWhisper(WhisperChannel whisper, Date date) {
            this.whisper = whisper;
            this.date = date;
        }
        
        public WhisperChannel getWhisper() {
            return this.whisper;
        }
        public long elapsed() {
            return new Date().getTime() - this.date.getTime();
        }
    }
    
}
