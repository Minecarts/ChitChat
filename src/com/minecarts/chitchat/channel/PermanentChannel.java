package com.minecarts.chitchat.channel;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class PermanentChannel extends PrefixChannel {
    public PermanentChannel(Player player, String name, String prefix, ChatColor color){
        super(player,name,prefix);
        this.setColor(color);
        super.join();
    }

    /* Allow players to join and leave these channels
    @Override
    public void join(){
        this.display("You cannot join this channel.");
    }

    @Override
    public void leave(){
        this.display("You can't leave this channel. Feel free to /ignore.");
    }
    */
}
