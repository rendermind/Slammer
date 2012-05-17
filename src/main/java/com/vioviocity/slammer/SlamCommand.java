package com.vioviocity.slammer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SlamCommand implements CommandExecutor {
    
    private Slammer plugin;
    public SlamCommand(Slammer plugin) {
        this.plugin = plugin;
    }

    static public Set <String> slammed = Collections.EMPTY_SET;
    
    public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
        if (!(sender instanceof Player))
            return true;
        
        // initialize core variables
        Player player = (Player) sender;
        Set <String> slammers = Collections.EMPTY_SET;
        
        // load slammers
        if (Slammer.slammerConfig.isConfigurationSection("slammer"))
            slammers = Slammer.slammerConfig.getConfigurationSection("slammer").getKeys(false);
        
        // command handler
        String cmd = command.getName().toLowerCase();
        if (cmd.equals("slam")) {
            // invalid args
            if (args.length < 1 || args.length > 2)
                return false;
            
            // slam [list]
            if (args.length == 1) {
                if(args[0].equalsIgnoreCase("list")) {
                    // check permission
                    if (!Slammer.checkPermission("slammer.list", player))
                        return true;
                    
                    if (slammers.isEmpty()) {
                        player.sendMessage(ChatColor.RED + "Slammers have not been set.");
                        return true;
                    }
                    
                    // display list of slammers
                    String slammerList = "";
                    for (String each : slammers)
                        slammerList += each + ", ";
                    slammerList = slammerList.substring(0, slammerList.length() - 2);
                    player.sendMessage(ChatColor.GREEN + "Slammers: " + ChatColor.WHITE + slammerList);
                    return true;
                }
            }
            
            // slam [set|del] (slammer)
            if (args.length == 2) {
                //check permission
                if (!Slammer.checkPermission("slammer.set", player))
                    return true;
                
                //initialize variables
                String slammerName = args[1];
                String path = "slammer." + slammerName.toLowerCase() + '.';
                
                // jail [set] (jail)
                if (args[0].equalsIgnoreCase("set")) {
                    
                    // save jail
                    Slammer.slammerConfig.set(path + "world", player.getLocation().getWorld().getName());
                    Slammer.slammerConfig.set(path + "x", player.getLocation().getX());
                    Slammer.slammerConfig.set(path + "y", player.getLocation().getY());
                    Slammer.slammerConfig.set(path + "z", player.getLocation().getZ());
                    Slammer.slammerConfig.set(path + "yaw", player.getLocation().getYaw());
                    Slammer.slammerConfig.set(path + "pitch", player.getLocation().getPitch());
                    Slammer.saveSlammerConfig();
                    player.sendMessage(ChatColor.GREEN + "Slammer " + slammerName + " set.");
                    return true;
                }
                
                // slam [del] (jail)
                if (args[0].equalsIgnoreCase("del")) {
                    
                    // check slammers
                    for (String each : slammers) {
                        if (slammerName.equalsIgnoreCase(each)) {
                            
                            // delete slammers
                            Slammer.slammerConfig.set("slammer." + slammerName, null);
                            Slammer.saveSlammerConfig();
                            player.sendMessage(ChatColor.RED + "Slammer " + each + " deleted.");
                            return true;
                        }
                    }
                    
                    // jail not found
                    player.sendMessage(ChatColor.RED + "Slammer does not exist.");
                    return true;
                }
            }
            
            // slam (player) (slammer)
            if (args.length == 2) {
                // check permission
                if (!Slammer.checkPermission("slammer.slam", player))
                    return false;
                
                // initialize variables
                String playerName = args[0];
                String slammerName = args[1];
                String slammerPath = "slammer." + slammerName.toLowerCase() + '.';
                slammed = Slammer.slammerConfig.getConfigurationSection("player").getKeys(false);
                
                // check if already slammed
                if (Slammer.checkSlammed(playerName)) {
                    player.sendMessage(ChatColor.RED + Slammer.whoJailed(playerName) + " is already slammed.");
                    return true;
                }
                
                // check players
                for (Player each : plugin.getServer().getOnlinePlayers()) {
                    if (each.getName().toLowerCase().contains(playerName.toLowerCase())) {
                        
                        // check slammers
                        for (String another : slammers) {
                            if (slammerName.equalsIgnoreCase(another)) {
				
				// save player location
				Location prior = player.getLocation();
				String playerPath = "player." + each.getName() + ".location.";
				Slammer.slammerConfig.set(playerPath + "world", prior.getWorld().getName());
				Slammer.slammerConfig.set(playerPath + 'x', prior.getX());
				Slammer.slammerConfig.set(playerPath + 'y', prior.getY());
				Slammer.slammerConfig.set(playerPath + 'z', prior.getZ());
				Slammer.slammerConfig.set(playerPath + "yaw", prior.getYaw());
				Slammer.slammerConfig.set(playerPath + "pitch", prior.getPitch());
                                
				// save player slammer
				Slammer.slammerConfig.set("player." + each.getName() + ".slammer", another);
				
                                // teleport player to slammer
                                Location slammer = player.getLocation();
                                slammer.setWorld(plugin.getServer().getWorld(Slammer.slammerConfig.getString(slammerPath + "world")));
                                slammer.setX(Slammer.slammerConfig.getDouble(slammerPath + 'x'));
                                slammer.setY(Slammer.slammerConfig.getDouble(slammerPath + 'y'));
                                slammer.setZ(Slammer.slammerConfig.getDouble(slammerPath + 'z'));
                                slammer.setYaw((float) Slammer.slammerConfig.getDouble(slammerPath + "yaw"));
                                slammer.setPitch((float) Slammer.slammerConfig.getDouble(slammerPath + "pitch"));
                                each.teleport(slammer);
                                
                                // add player to slammer list
                                slammed.add(each.getName());
                                Slammer.slammerConfig.set("player", slammed);
                                Slammer.saveSlammerConfig();
                                
                                player.sendMessage(ChatColor.RED + each.getName() + " has been slammed to " + another + '.');
				each.sendMessage(ChatColor.RED + player.getName() + " has slammed you.");
                                return true;
                            }
                        }
                        
                        // slammer not found
                        player.sendMessage(ChatColor.RED + slammerName + " does not exist.");
                        return true;
                    }
                }
                
                // player not found
                player.sendMessage(ChatColor.RED + playerName + " is not online.");
                return true;
            }
        }
        
        // end of command
        return false;
    }
}