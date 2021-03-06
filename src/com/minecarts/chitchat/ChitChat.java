package com.minecarts.chitchat;

import com.minecarts.chitchat.channel.*;
import com.minecarts.chitchat.event.ChannelMessage;
import com.minecarts.chitchat.manager.ChannelManager;
import com.minecarts.chitchat.command.*;
import com.minecarts.chitchat.manager.MuteManager;
import com.minecarts.chitchat.manager.IgnoreManager;
import com.minecarts.dbquery.DBQuery;
import org.apache.commons.lang.StringUtils;
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
    
    private static ChitChat plugin;
    static public ChitChat getPlugin() {
        return plugin;
    }
    
    @Override
    public void onEnable(){
        plugin = this;
        
        // load config and copy plugin's default config values to it
        getConfig().options().copyDefaults(true);
        
        dbq = (DBQuery) getServer().getPluginManager().getPlugin("DBQuery");
        getServer().getPluginManager().registerEvents(this,this);
        
        //Register our commands
            getCommand("join").setExecutor(new JoinCommand());
            getCommand("leave").setExecutor(new LeaveCommand());
            getCommand("say").setExecutor(new SayCommand());
            getCommand("whisper").setExecutor(new WhisperCommand());
            getCommand("channel").setExecutor(new ChannelCommand());
            getCommand("rewhisper").setExecutor(new RewhisperCommand());
            getCommand("reply").setExecutor(new ReplyCommand());
            getCommand("ignore").setExecutor(new IgnoreCommand());
            getCommand("mute").setExecutor(new MuteCommand());
            getCommand("unmute").setExecutor(new UnmuteCommand());
            getCommand("announce").setExecutor(new AnnounceCommand());
            getCommand("hint").setExecutor(new HintsCommand());

        //Join existing players to our default / static channels
        for(Player player : Bukkit.getOnlinePlayers()){
            ChannelManager.setJoinLocked(player,true);
            this.joinPlayerToStaticChannels(player,true);
            this.joinPlayerToDynamicChannels(player,true);
            this.dbLoadIgnores(player);
        }

    }
    

    @EventHandler(priority = EventPriority.LOW) //Listen on low for /! worldedit cmd workaround :(
    public void onPlayerCommand(PlayerCommandPreprocessEvent event){
        //This will occur on all commands, we want to check to see if
        //    it's a chat message to a specific channel, if so
        //    we're going to handle it here
        Player player = event.getPlayer();
        String[] args = event.getMessage().replaceAll(" +", " ").split(" ", 2);
        String prefix = args[0].replaceAll("/", "");

        
        //Check to see if it's the /who command to allow for ghetto hooking of /who $ for example
        if(prefix.equalsIgnoreCase("who")){
            if(args.length != 2) return; //Only hook if it's /who #
            PrefixChannel channel = ChannelManager.getChannelFromPrefix(player, args[1]);
            if(channel == null) return; //If no channel, let /who plugin handle it
            player.sendMessage(channel.color() + "---- Players in " +channel.getName() +" (" + channel.getPrefix() + ") -----");
            player.sendMessage(StringUtils.join(channel.getMemberNames(player), channel.color() + ", "));
            event.setCancelled(true);
            return;
        }
        
        
        PrefixChannel channel = ChannelManager.getChannelFromPrefix(player, prefix);
        if(channel == null){
            return; //Don't handle it, it's probably not a channel index
        }
        if(channel.setDefault()){
            dbUpdateChannel(player, channel);

            if(args.length == 1){
                player.sendMessage(MessageFormat.format("{0} {2}[{1}] is now your default chat channel.",
                        channel.getPrefix(),
                        channel.getName(),
                        ChatColor.DARK_GRAY
                ));
            }
        }
        if(args.length == 2){
            String message = ChatColor.stripColor(args[1]);
            channel.broadcast(new Player[]{player},message);
        }
        event.setCancelled(true); //Cancel the event becuase we handled it
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerChat(PlayerChatEvent e){
        //WorldEdit CUI needs to see some data that's passed via chat and handled by the plugin??
        if(StringUtils.left(e.getMessage(),5).equalsIgnoreCase("u00a7")) return;

        Channel channel = ChannelManager.getDefaultPlayerChannel(e.getPlayer());
        if(channel == null) return;
        channel.broadcast(new Player[]{e.getPlayer()}, ChatColor.stripColor(e.getMessage()));
        e.setCancelled(true);
    }


    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent e){
        final Player player = e.getPlayer();
        ChannelManager.setJoinLocked(player,true);
        //Query the channels for this player and join them to them, in addition to global and announce
        this.joinPlayerToStaticChannels(player,false);
        this.joinPlayerToDynamicChannels(player,false);
        this.dbLoadIgnores(player);
    }
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerKick(PlayerKickEvent e){
        ArrayList<Channel> channels = ChannelManager.getPlayerChannels(e.getPlayer());
        if(channels == null) return;
        for(Channel channel : channels){
            channel.leaveWithoutClear(); //Todo: This wont notify players on leave, is this intended?
        }
        ChannelManager.clearPlayerChannels(e.getPlayer());
    }
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerQuit(PlayerQuitEvent e){
        ArrayList<Channel> channels = ChannelManager.getPlayerChannels(e.getPlayer());
        if(channels == null) return;
        for(Channel channel : channels){
            channel.leaveWithoutClear();
        }
        ChannelManager.clearPlayerChannels(e.getPlayer());
    }
    
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onChannelMessage(ChannelMessage event){
        ChannelLink link = ChannelManager.getOrCreateChannelLink(event.getChannelName());
        link.relayMessage(event.getTaggedPlayers(),event.getFormat(),event.getArgs());
    }



    private void joinPlayerToDynamicChannels(final Player player, final boolean isReload){
        new Query("SELECT * FROM `player_channels` WHERE `playerName` = ? ORDER BY `channelIndex`") {
            @Override
            public void onFetch(ArrayList<HashMap> rows) {
                ChannelManager.setJoinLocked(player,false);
                if(rows == null || rows.size() == 0) return;
                for(HashMap row : rows){
                    PrefixChannel channel = new PrefixChannel(player,(String)row.get("channelName"),row.get("channelIndex").toString());
                    channel.join(true,isReload);
                    if((Boolean)row.get("isDefault")){
                        channel.setDefault();
                    }
                }
                if(!isReload){
                    player.sendMessage(MessageFormat.format("{2}You joined {0} chat channels. Type {1}/ch list{2} for details.",
                            ChannelManager.getVisibleChannelCount(player),
                            ChatColor.YELLOW,
                            ChatColor.GRAY));
                }
            }
        }.fetch(player.getName());
    }
    private void joinPlayerToStaticChannels(Player player, boolean isReload){
        PrefixChannel global = new PermanentChannel(player, "Global", "g", ChatColor.GOLD);
        global.join(true,true); //Never show joins for global
        PrefixChannel announce = new AnnouncementChannel(player);
        announce.join(true,true);
        LocalChannel local = new LocalChannel(player); //Local auto joins on construction
    }
    
    
    @EventHandler
    public void on(com.minecarts.dbpermissions.PermissionsCalculated event) {
        if(event.getPermissible() instanceof Player) {
            Player player = (Player) event.getPermissible();
            
            PrefixChannel subscriber = new PermanentChannel(player, "Subscribers", "$", ChatColor.GREEN);
            if(player.hasPermission("subscriber")) {
                if(!subscriber.hasPlayer()) {
                    subscriber.join(true, true);
                }
            }
            else {
                if(subscriber.hasPlayer()) {
                    subscriber.leave(false, false);
                }
            }
            
            PrefixChannel admin = new PermanentChannel(player, "Admin", "@", ChatColor.YELLOW);
            if(player.hasPermission("chitchat.admin.chat")) {
                if(!admin.hasPlayer()) {
                    admin.join(true, true);
                }
            }
            else {
                if(admin.hasPlayer()) {
                    admin.leave(false, false);
                }
            }
        }
    }

    //Bans
    public void dbInsertBan(final Player player){
        new Query("INSERT INTO `player_bans` VALUES (?,?,?,NOW(),TIMESTAMPADD(MINUTE, ?, NOW()))") {
            @Override
            public void onAffected(Integer affected) {
                System.out.println("ChitChat> Automatically banned " + player.getName() + " for spam.");
            }
        }.affected(
                player.getName(),
                getConfig().getString("spam.ban_message"),
                "plugin.ChitChat",
                15
        );
    }

    //Channels
    public void dbUpdateChannel(final Player player, final PrefixChannel channel){
        if(!channel.shouldSave()) return;
        new Query("INSERT INTO `player_channels` VALUES (?,?,?,?,?) ON DUPLICATE KEY UPDATE channelId=?,channelName=?,channelIndex=?,isDefault=?") {
            @Override
            public void onAffected(Integer affected) {

            }
        }.affected(player.getName(),
                channel.getRawPrefix(),
                channel.getName().toLowerCase(),
                channel.getName(),
                channel.isDefault(),
                channel.getName().toLowerCase(),
                channel.getName(),
                channel.getRawPrefix(),
                0
        );
    }
    public void dbSetDefaultChannel(final Player player, final Channel channel){
        new Query("UPDATE `player_channels` SET isDefault=0 WHERE `playerName` = ?") {
            @Override
            public void onAffected(Integer affected) {
                new Query("UPDATE `player_channels` SET isDefault=1 WHERE `playerName` = ? AND `channelId` = ? LIMIT 1") {
                    @Override
                    public void onAffected(Integer affected) {

                    }
                }.affected(player.getName(),
                        channel.getName().toLowerCase());
            }
        }.affected(player.getName());
    }
    
    public void dbRemoveChannel(final Player player, final PrefixChannel channel){
        new Query("DELETE FROM `player_channels` WHERE `playerName` = ? AND `channelIndex` = ? AND `channelId` = ? LIMIT 1") {
            @Override
            public void onAffected(Integer affected) {
                if(affected == 0){
                    //player.sendMessage("Channel " + channel.getName() + " with prefix " + channel.getPrefix() + " not in DB.");
                }
            }
        }.affected(player.getName(),
                channel.getRawPrefix(),
                channel.getName().toLowerCase());
    }

    //Ignores
    public void dbLoadIgnores(final Player player){
        new Query("SELECT * FROM `player_ignore` WHERE `playerName` = ?") {
            @Override
            public void onFetch(ArrayList<HashMap> rows) {
                if(rows == null || rows.size() == 0) return;
                for(HashMap row : rows){
                    IgnoreManager.ignorePlayer(player,(String)row.get("ignoreName"));
                }
            }
        }.fetch(player.getName());
    }
    public void dbRemoveIgnore(final Player player, final String ignoree){
        new Query("DELETE FROM `player_ignore` WHERE `playerName` = ? AND `ignoreName` = ? LIMIT 1") {}
            .affected(player.getName(),
                    ignoree);
    }
    public void dbAddIgnore(final Player player, String ignoree){
        new Query("INSERT INTO `player_ignore` VALUES (?,?)") {}
        .affected(player.getName(),
                ignoree
        );
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
