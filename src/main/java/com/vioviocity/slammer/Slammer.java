package com.vioviocity.slammer;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class Slammer extends JavaPlugin implements Listener {
    
    static Logger log = Logger.getLogger("Slammer");
    static public FileConfiguration jailConfig = null;
    static File jailConfigFile = null;
    
    @Override
    public void onDisable() {
        // plugin disabled
        log.info(this + " is now disabled.");
    }

    @Override
    public void onEnable() {
        // register events
        getServer().getPluginManager().registerEvents(new SlammerPlayerListener(), this);
        
        // setup config files
        loadJailConfig();
        saveJailConfig();
        
        // register commands
        getCommand("jail").setExecutor(new JailCommand(this));
        getCommand("unjail").setExecutor(new UnjailCommand(this));
        
        // plugin enabled
        log.info(this + " is now enabled.");
    }
    
    // load jail config file
    public FileConfiguration loadJailConfig() {
        if (jailConfig == null) {
            if (jailConfigFile == null)
                jailConfigFile = new File(this.getDataFolder(), "jails.yml");
            if (jailConfigFile.exists()) {
                jailConfig = YamlConfiguration.loadConfiguration(jailConfigFile);
            } else {
                InputStream defConfigStream = getResource("jails.yml");
                jailConfig = YamlConfiguration.loadConfiguration(defConfigStream);
            }
        }
        return jailConfig;
    }
    
    // save jail config file
    static public void saveJailConfig() {
        if (jailConfig == null || jailConfigFile == null)
            return;
        try {
            jailConfig.save(jailConfigFile);
        } catch (IOException e) {
            log.severe("Unable to save jail config to " + jailConfigFile + '.');
        }
    }
    
    // permission handler
    static public boolean checkPermission(String permission, Player player) {
        if (!player.hasPermission(permission)) {
            player.sendMessage(ChatColor.RED + "You do not have permission.");
            return false;
        }
        return true;
    }
    
    // check if player is jailed
    static public boolean checkJailed(String playerName) {
        JailCommand.jailed = Slammer.jailConfig.getStringList("slammer.jailed");
        for (String each : JailCommand.jailed)
            if (each.toLowerCase().contains(playerName.toLowerCase()))
                return true;
        return false;
    }
    
    // check jailed player name
    static public String whoJailed(String playerName) {
        JailCommand.jailed = Slammer.jailConfig.getStringList("slammer.jailed");
        for (String each : JailCommand.jailed)
            if (each.toLowerCase().contains(playerName))
                return each;
        return "";
    }
}