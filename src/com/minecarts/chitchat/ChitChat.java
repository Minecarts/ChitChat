package com.minecarts.chitchat;

import com.minecarts.chitchat.channel.BaseChannel;
import com.minecarts.chitchat.channel.PlayerChannel;
import com.minecarts.chitchat.manager.ChannelManager;
import com.minecarts.chitchat.command.JoinCommand;
import com.minecarts.dbquery.DBQuery;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;
import org.bukkit.plugin.java.JavaPlugin;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.logging.Level;

public class ChitChat extends JavaPlugin implements Listener {
    private DBQuery dbq;
    private final Random random = new Random();

    public void onEnable(){
        dbq = (DBQuery) getServer().getPluginManager().getPlugin("DBQuery");
        getServer().getPluginManager().registerEvents(this,this);
        //Register our commands
            getCommand("join").setExecutor(new JoinCommand());

        //Join existing players to our default / static channels
        for(Player player : Bukkit.getOnlinePlayers()){
            ChannelManager.addPlayerChannel(player, new PlayerChannel(player, "Global", "g"));
            ChannelManager.addPlayerChannel(player, new PlayerChannel(player, "Announce", "!"));
            if(player.hasPermission("subscriber")){
                ChannelManager.addPlayerChannel(player, new PlayerChannel(player, "Subscriber", "$"));
            }
        }
    }


    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerCommand(PlayerCommandPreprocessEvent event){
        //This will occur on all commands, we want to check to see if
        //    it's a chat message to a specific channel, if so
        //    we're going to handle it here
        Player player = event.getPlayer();
        String[] args = event.getMessage().replaceAll(" +", " ").split(" ", 2);
        String prefix = args[0].replaceAll("/", "");
        String message = args[1];

        PlayerChannel channel = ChannelManager.getPlayerChannelFromPrefix(player,prefix);
        if(channel == null){
            return; //Don't handle it, it's probably not a channel index
        }
        channel.getBaseChannel().sendMessage(player,message);
        channel.setDefault();
        event.setCancelled(true); //Cancel the event becuase we handled it
    }

    @EventHandler
    public void onPlayerChat(PlayerChatEvent e){
        //s = String.format(event.getFormat(), event.getPlayer().getDisplayName(), event.getMessage());
        PlayerChannel channel = ChannelManager.getDefaultPlayerChannel(e.getPlayer());
        e.setFormat(channel.getMinecraftFormat());
        e.getRecipients().clear();
        e.getRecipients().addAll(channel.getBaseChannel().getMembers());
    }
    

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e){
        final Player player = e.getPlayer();
        //Query the channels for this player and join them to them, in addition to global and announce
        ChannelManager.addPlayerChannel(player,new PlayerChannel(player,"Global","g"));
        ChannelManager.addPlayerChannel(player,new PlayerChannel(player,"Announce","!"));
        if(player.hasPermission("subscriber")){
            ChannelManager.addPlayerChannel(player, new PlayerChannel(player, "Subscriber", "$"));
        }

        new Query("SELECT * FROM `player_channels` WHERE `playerName` = ?") {
            @Override
            public void onFetch(ArrayList<HashMap> rows) {
                if(rows == null || rows.size() == 0) return;
                for(HashMap row : rows){
                    ChannelManager.addPlayerChannel(player,new PlayerChannel(player,(String)row.get("channelName"),row.get("channelIndex").toString()));
                }
            }
        }.fetch(e.getPlayer().getName());
    }
    @EventHandler
    public void onPlayerKick(PlayerKickEvent e){
        for(PlayerChannel channel : ChannelManager.getPlayerChannels(e.getPlayer())){
            channel.leavePlayer(e.getPlayer());
        }
    }
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e){
        for(PlayerChannel channel : ChannelManager.getPlayerChannels(e.getPlayer())){
            channel.leavePlayer(e.getPlayer());
        }
    }



    //DB
    class Query extends com.minecarts.dbquery.Query {
        public Query(String sql) {
            //getConfig().getString("db.provider")
            super(ChitChat.this, dbq.getProvider(getConfig().getString("db.provider")), sql);
        }
        @Override
        public void onComplete(FinalQuery query) {
            if(query.elapsed() > 500) {
                getLogger().log(Level.INFO,MessageFormat.format("Slow query took {0,number,#} ms", query.elapsed()));
            }
        }
        @Override
        public void onException(Exception x, FinalQuery query) {
            try { throw x; }
            catch(Exception e) {
                e.printStackTrace();
            }
        }
    }
}
