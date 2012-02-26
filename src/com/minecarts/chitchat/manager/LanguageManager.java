package com.minecarts.chitchat.manager;

import org.bukkit.ChatColor;

import java.util.regex.Pattern;

public class LanguageManager {
    public static String filter(ChatColor channelColor, String message){
        //return message.replaceAll("(fuck)",ChatColor.MAGIC + "$1" + channelColor);
        return message;
    }
    public static String translate(String message){
        return message;
    }
}
