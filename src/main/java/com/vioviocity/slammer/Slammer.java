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
    static public FileConfiguration slammerConfig = null;
    static File slammerConfigFile = null;
    
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
        loadSlammerConfig();
        saveSlammerConfig();
        
        // register commands
        getCommand("slam").setExecutor(new SlamCommand(this));
        getCommand("free").setExecutor(new FreeCommand(this));
        
        // plugin enabled
        log.info(this + " is now enabled.");
    }
    
    // load jail config file
    public FileConfiguration loadSlammerConfig() {
        if (slammerConfig == null) {
            if (slammerConfigFile == null)
                slammerConfigFile = new File(this.getDataFolder(), "slammers.yml");
            if (slammerConfigFile.exists()) {
                slammerConfig = YamlConfiguration.loadConfiguration(slammerConfigFile);
            } else {
                InputStream defConfigStream = getResource("slammers.yml");
                slammerConfig = YamlConfiguration.loadConfiguration(defConfigStream);
            }
        }
        return slammerConfig;
    }
    
    // save jail config file
    static public void saveSlammerConfig() {
        if (slammerConfig == null || slammerConfigFile == null)
            return;
        try {
            slammerConfig.save(slammerConfigFile);
        } catch (IOException e) {
            log.severe("Unable to save Slammer config to " + slammerConfigFile + '.');
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
    
    // check if player is slammed
    static public boolean checkSlammed(String playerName) {
	
	if (!slammerConfig.isConfigurationSection("player"))
	    return false;
        
	SlamCommand.slammed = slammerConfig.getConfigurationSection("player").getKeys(false);
        for (String each : SlamCommand.slammed)
            if (each.toLowerCase().contains(playerName.toLowerCase()))
                return true;
        return false;
    }
    
    // check slammed player name
    static public String whoJailed(String playerName) {
        SlamCommand.slammed = slammerConfig.getConfigurationSection("player").getKeys(false);
        for (String each : SlamCommand.slammed)
            if (each.toLowerCase().contains(playerName))
                return each;
        return "";
    }
}