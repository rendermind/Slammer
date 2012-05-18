package com.vioviocity.slammer;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FreeCommand implements CommandExecutor {
    
    private Slammer plugin;
    public FreeCommand(Slammer plugin) {
        this.plugin = plugin;
    }
    
    public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
        if (!(sender instanceof Player))
            return true;
        
        // initialize core variables
        Player player = (Player) sender;
        
        // command handler
        String cmd = command.getName().toLowerCase();
        
        if (cmd.equals("free")) {
            // invalid args
            if (args.length < 1 || args.length > 1)
                return false;
            
            // free (player)
            if (args.length == 1) {
                // check permission
                if (!Slammer.checkPermission("slammer.slam", player))
                    return false;
                    
                // initialize variables
                String playerName = args[0];
                SlamCommand.slammed = Slammer.slammerConfig.getConfigurationSection("player").getKeys(false);
                
                // check players
                for (String each : SlamCommand.slammed) {
                    if (each.toLowerCase().contains(playerName.toLowerCase())) {
                        
			// find player online
			for (Player another : plugin.getServer().getOnlinePlayers()) {
			    if (another.getName().toLowerCase().contains(playerName.toLowerCase())) {
				
				// teleport player to prior location
				Location prior = another.getLocation();
				String playerPath = "player." + each + ".location.";
				prior.setWorld(plugin.getServer().getWorld(Slammer.slammerConfig.getString(playerPath + "world")));
				prior.setX(Slammer.slammerConfig.getDouble(playerPath + 'x'));
				prior.setY(Slammer.slammerConfig.getDouble(playerPath + 'y'));
				prior.setZ(Slammer.slammerConfig.getDouble(playerPath + 'z'));
				prior.setYaw((float) Slammer.slammerConfig.getDouble(playerPath + "yaw"));
				prior.setPitch((float) Slammer.slammerConfig.getDouble(playerPath + "pitch"));
				another.teleport(prior);
			    }
			}
			
                        // remove player from slammer list
                        Slammer.slammerConfig.set("player." + each, null);
                        Slammer.saveSlammerConfig();
                        
                        player.sendMessage(ChatColor.GREEN + each + " has been set free.");
                        return true;
                    }
                }
                
                // player not found
                player.sendMessage(ChatColor.RED + playerName + " is not slammed.");
                return true;
            }
        }
        
        // end of command
        return false;
    }
}