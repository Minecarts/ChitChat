package com.minecarts.chitchat.channel;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class PermanentChannel extends PrefixChannel {
    public PermanentChannel(Player player, String name, String prefix, ChatColor color){
        super(player,name,prefix);
        this.color(color);
    }

    @Override
    public Boolean leave(){
        this.display("You can't leave this channel. Feel free to /ignore.");
        return false;
    }
}
