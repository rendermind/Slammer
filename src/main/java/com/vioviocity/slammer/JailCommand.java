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

public class JailCommand implements CommandExecutor {
    
    private Slammer plugin;
    public JailCommand(Slammer plugin) {
        this.plugin = plugin;
    }

    static public List <String> jailed = new ArrayList <String>();
    
    public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
        if (!(sender instanceof Player))
            return true;
        
        // initialize core variables
        Player player = (Player) sender;
        Set <String> jails = Collections.EMPTY_SET;
        
        // load jails
        if (Slammer.jailConfig.isConfigurationSection("slammer.jail"))
            jails = Slammer.jailConfig.getConfigurationSection("slammer.jail").getKeys(false);
        
        // command handler
        String cmd = command.getName().toLowerCase();
        if (cmd.equals("jail")) {
            // invalid args
            if (args.length < 1 || args.length > 2)
                return false;
            
            // jail [list]
            if (args.length == 1) {
                if(args[0].equalsIgnoreCase("list")) {
                    // check permission
                    if (!Slammer.checkPermission("slammer.jail.list", player))
                        return true;
                    
                    if (jails.isEmpty()) {
                        player.sendMessage(ChatColor.RED + "Jails have not been set.");
                        return true;
                    }
                    
                    // display list of jails
                    String jailList = "";
                    for (String each : jails)
                        jailList += each + ", ";
                    jailList = jailList.substring(0, jailList.length() - 2);
                    player.sendMessage(ChatColor.GREEN + "Jails: " + ChatColor.WHITE + jailList);
                    return true;
                }
            }
            
            // jail [set|del] (jail)
            if (args.length == 2) {
                //check permission
                if (!Slammer.checkPermission("slammer.jail.set", player))
                    return true;
                
                //initialize variables
                String jailName = args[1];
                String path = "slammer.jail." + jailName.toLowerCase() + '.';
                
                // jail [set] (jail)
                if (args[0].equalsIgnoreCase("set")) {
                    
                    // save jail
                    Slammer.jailConfig.set(path + "world", player.getLocation().getWorld().getName());
                    Slammer.jailConfig.set(path + "x", player.getLocation().getX());
                    Slammer.jailConfig.set(path + "y", player.getLocation().getY());
                    Slammer.jailConfig.set(path + "z", player.getLocation().getZ());
                    Slammer.jailConfig.set(path + "yaw", player.getLocation().getYaw());
                    Slammer.jailConfig.set(path + "pitch", player.getLocation().getPitch());
                    Slammer.saveJailConfig();
                    player.sendMessage(ChatColor.GREEN + "Jail " + jailName + " set.");
                    return true;
                }
                
                // jail [del] (jail)
                if (args[0].equalsIgnoreCase("del")) {
                    
                    // check jails
                    for (String each : jails) {
                        if (jailName.equalsIgnoreCase(each)) {
                            
                            // delete jail
                            Slammer.jailConfig.set("slammer.jail." + jailName, null);
                            Slammer.saveJailConfig();
                            player.sendMessage(ChatColor.RED + "Jail " + each + " deleted.");
                            return true;
                        }
                    }
                    
                    // jail not found
                    player.sendMessage(ChatColor.RED + "Jail does not exist.");
                    return true;
                }
            }
            
            // jail (player) (jail)
            if (args.length == 2) {
                // check permission
                if (!Slammer.checkPermission("slammer.jail", player))
                    return false;
                
                // initialize variables
                String playerName = args[0];
                String jailName = args[1];
                String path = "slammer.jail." + jailName.toLowerCase() + '.';
                jailed = Slammer.jailConfig.getStringList("slammer.jailed");
                
                // check if already jailed
                if (Slammer.checkJailed(playerName)) {
                    player.sendMessage(ChatColor.RED + Slammer.whoJailed(playerName) + " is already jailed.");
                    return true;
                }
                
                // check players
                for (Player each : plugin.getServer().getOnlinePlayers()) {
                    if (each.getName().toLowerCase().contains(playerName)) {
                        
                        // check jails
                        for (String another : jails) {
                            if (jailName.equalsIgnoreCase(another)) {
                                
                                // send player to jail
                                Location jail = player.getLocation();
                                jail.setWorld(plugin.getServer().getWorld(Slammer.jailConfig.getString(path + "world")));
                                jail.setX(Slammer.jailConfig.getDouble(path + 'x'));
                                jail.setY(Slammer.jailConfig.getDouble(path + 'y'));
                                jail.setZ(Slammer.jailConfig.getDouble(path + 'z'));
                                jail.setYaw((float) Slammer.jailConfig.getDouble(path + "yaw"));
                                jail.setPitch((float) Slammer.jailConfig.getDouble(path + "pitch"));
                                each.teleport(jail);
                                
                                // add player to jail list
                                jailed.add(each.getName());
                                Slammer.jailConfig.set("slammer.jailed", jailed);
                                Slammer.saveJailConfig();
                                
                                player.sendMessage(ChatColor.RED + each.getName() + " has been jailed to " + another + '.');
				each.sendMessage(ChatColor.RED + player.getName() + " has jailed you.");
                                return true;
                            }
                        }
                        
                        // jail not found
                        player.sendMessage(ChatColor.RED + jailName + " does not exist.");
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