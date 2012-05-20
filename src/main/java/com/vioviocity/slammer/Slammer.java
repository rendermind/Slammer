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
    static public FileConfiguration langConfig = null;
    static File langConfigFile = null;
    
    @Override
    public void onDisable() {
        // plugin disabled
        log.info(langConfig.getString("slammer.disabled").replace("%plugin%", this.toString()));
    }

    @Override
    public void onEnable() {
        // register events
        getServer().getPluginManager().registerEvents(new SlammerPlayerListener(), this);
        
        // setup config files
        loadSlammerConfig();
        saveSlammerConfig();
	loadLangConfig();
        
        // register commands
        getCommand("slam").setExecutor(new SlamCommand(this));
        getCommand("free").setExecutor(new FreeCommand(this));
	
	// metrics
        try {
            Metrics metrics = new Metrics(this);
            metrics.start();
        } catch (IOException e) {
            log.warning("[Shout] " + langConfig.getString("slammer.metrics"));
        }
        
        // plugin enabled
        log.info(langConfig.getString("slammer.enabled").replace("%plugin%", this.toString()));
    }
    
    // load slammer config file
    public FileConfiguration loadSlammerConfig() {
        if (slammerConfig == null) {
            if (slammerConfigFile == null)
                slammerConfigFile = new File(this.getDataFolder(), "slammer.yml");
            if (slammerConfigFile.exists()) {
                slammerConfig = YamlConfiguration.loadConfiguration(slammerConfigFile);
            } else {
                InputStream defConfigStream = getResource("slammer.yml");
                slammerConfig = YamlConfiguration.loadConfiguration(defConfigStream);
            }
        }
        return slammerConfig;
    }
    
    // save slammer config file
    static public void saveSlammerConfig() {
        if (slammerConfig == null || slammerConfigFile == null)
            return;
        try {
            slammerConfig.save(slammerConfigFile);
        } catch (IOException e) {
            log.severe(langConfig.getString("slammer.config").replace("%plugin%", "Slammer").replace("%path%", slammerConfigFile.toString()));
        }
    }
    
    // load lang config file
    public FileConfiguration loadLangConfig() {
	if (langConfig == null) {
	    if (langConfigFile == null)
		langConfigFile = new File(this.getDataFolder(), slammerConfig.getString("language") + ".yml");
	    if (langConfigFile.exists()) {
		langConfig = YamlConfiguration.loadConfiguration(langConfigFile);
	    } else {
		InputStream defConfigStream = getResource(slammerConfig.getString("language") + ".yml");
		langConfig = YamlConfiguration.loadConfiguration(defConfigStream);
	    }
	}
	return langConfig;
    }
    
    // permission handler
    static public boolean checkPermission(String permission, Player player) {
        if (!player.hasPermission(permission)) {
            player.sendMessage(ChatColor.RED + langConfig.getString("slammer.permission"));
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