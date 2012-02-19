package com.minecarts.chitchat.channel;

import org.bukkit.entity.Player;

//TODO: This class may not be used, need to determine how to secure the subscriber and admin channels
//TODO:  they may be a special case, or they may be a different type of channel (eg a StaticChannel)

public class StaticChannel extends PlayerChannel {
    public StaticChannel(Player player, String name, String prefix){
        super(player,name,prefix);
    }
    public StaticChannel(Player player, String name){
        super(player,name);
    }

    @Override
    public void join(){
        super.join();
        //Validate permissions upon attempting to manually join?????
    }
}
