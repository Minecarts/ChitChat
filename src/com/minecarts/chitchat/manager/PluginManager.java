package com.minecarts.chitchat.manager;

import com.minecarts.chitchat.ChitChat;
import org.bukkit.configuration.file.FileConfiguration;

public class PluginManager {
    private static ChitChat pluginRef;
    
    public static ChitChat plugin(){
        return pluginRef;
    }
    public static void plugin(ChitChat plugin){
        pluginRef=plugin;
    }
    public static FileConfiguration config(){
        return pluginRef.getConfig();
    }
    
}
