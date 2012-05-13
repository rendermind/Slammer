package com.vioviocity.slammer;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class UnjailCommand implements CommandExecutor {
    
    private Slammer plugin;
    public UnjailCommand(Slammer plugin) {
        this.plugin = plugin;
    }
    
    public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
        if (!(sender instanceof Player))
            return true;
        
        // initialize core variables
        Player player = (Player) sender;
        
        // command handler
        String cmd = command.getName().toLowerCase();
        
        if (cmd.equals("unjail")) {
            // invalid args
            if (args.length < 1 || args.length > 1)
                return false;
            
            // unjail (player)
            if (args.length == 1) {
                // check permission
                if (!Slammer.checkPermission("slammer.jail", player))
                    return false;
                    
                // initialize variables
                String playerName = args[0];
                JailCommand.jailed = Slammer.jailConfig.getStringList("slammer.jailed");
                
                // check players
                for (String each : JailCommand.jailed) {
                    if (each.toLowerCase().contains(playerName)) {
                        
                        // remove player from jail list
                        JailCommand.jailed.remove(each);
                        Slammer.jailConfig.set("slammer.jailed", JailCommand.jailed);
                        Slammer.saveJailConfig();
                        
                        player.sendMessage(ChatColor.GREEN + each + " has been set free.");
                        return true;
                    }
                }
                
                // player not found
                player.sendMessage(ChatColor.RED + playerName + " is not jailed.");
                return true;
            }
        }
        
        // end of command
        return false;
    }
}