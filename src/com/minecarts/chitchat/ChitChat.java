package com.minecarts.chitchat;

import com.minecarts.chitchat.channel.Channel;
import com.minecarts.chitchat.channel.LocalChannel;
import com.minecarts.chitchat.channel.PrefixChannel;
import com.minecarts.chitchat.channel.PermanentChannel;
import com.minecarts.chitchat.manager.ChannelManager;
import com.minecarts.chitchat.command.*;
import com.minecarts.dbquery.DBQuery;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
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
            getCommand("leave").setExecutor(new LeaveCommand());
            getCommand("say").setExecutor(new SayCommand());
            getCommand("whisper").setExecutor(new WhisperCommand());
            //getCommand("who").setExecutor(new WhoCommand());

        //Join existing players to our default / static channels
        for(Player player : Bukkit.getOnlinePlayers()){
            joinPlayerToStaticChannels(player);
            joinPlayerToDynamicChannels(player);
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

        PrefixChannel channel = ChannelManager.getChannelFromPrefix(player, prefix);
        if(channel == null){
            return; //Don't handle it, it's probably not a channel index
        }
        //channel.getBaseChannel().sendMessage(player,message);
        channel.setDefault();
        Bukkit.getPluginManager().callEvent(new PlayerChatEvent(player,message)); //Send it via a player chat event
        event.setCancelled(true); //Cancel the event becuase we handled it
    }

    @EventHandler
    public void onPlayerChat(PlayerChatEvent e){
        Channel channel = ChannelManager.getDefaultPlayerChannel(e.getPlayer());
        channel.broadcast(e.getPlayer(), e.getMessage());
        e.setCancelled(true);
    }
    

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e){
        final Player player = e.getPlayer();
        //Query the channels for this player and join them to them, in addition to global and announce
        joinPlayerToStaticChannels(player);
        joinPlayerToDynamicChannels(player);
    }
    @EventHandler
    public void onPlayerKick(PlayerKickEvent e){
        for(Channel channel : ChannelManager.getPlayerChannels(e.getPlayer())){
            channel.leave();
        }
    }
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e){
        ArrayList<Channel> channels = ChannelManager.getPlayerChannels(e.getPlayer());
        if(channels == null) return;
        for(Channel channel : channels){
            channel.leave();
        }
    }



    private void joinPlayerToDynamicChannels(final Player player){
        new Query("SELECT * FROM `player_channels` WHERE `playerName` = ?") {
            @Override
            public void onFetch(ArrayList<HashMap> rows) {
                if(rows == null || rows.size() == 0) return;
                for(HashMap row : rows){
                    PrefixChannel channel = new PrefixChannel(player,(String)row.get("channelName"),row.get("channelIndex").toString());
                    channel.join();
                }
            }
        }.fetch(player.getName());
    }
    private void joinPlayerToStaticChannels(Player player){
        PrefixChannel global = new PermanentChannel(player, "Global", "g", ChatColor.GOLD);
        PrefixChannel announce = new PermanentChannel(player, "Announce", "!", ChatColor.RED);
        LocalChannel local = new LocalChannel(player);

        if(player.hasPermission("subscriber")){
            PrefixChannel subscriber = new PermanentChannel(player, "Subscriber", "$", ChatColor.GREEN);
        }
        if(player.hasPermission("chitchat.admin.chat")){
            PrefixChannel admin = new PermanentChannel(player, "Admin", "@", ChatColor.YELLOW);
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
