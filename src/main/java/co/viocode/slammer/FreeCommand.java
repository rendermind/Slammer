package co.viocode.slammer;

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

		// check if player
		Boolean isPlayer = true;
		if (!(sender instanceof Player))
			isPlayer = false;

		// initialize variables
		Player player = null;
		if (isPlayer)
			player = (Player) sender;

		// command handler
		String cmd = command.getName().toLowerCase();
		if (cmd.equals("free")) {

			// invalid args
			if (args.length < 1 || args.length > 1)
				return false;

			// <command> (player)
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
						sender.sendMessage(ChatColor.GREEN + Slammer.langConfig.getString("free.freed").replace("%name%", each));
						return true;
					}
				}

				// player not found
				sender.sendMessage(ChatColor.RED + Slammer.langConfig.getString("free.not_jailed").replace("%name%", playerName));
				return true;
			}
		}

		// end of command
		return false;
	}
}