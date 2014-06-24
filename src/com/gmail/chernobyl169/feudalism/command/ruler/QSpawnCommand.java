package com.gmail.chernobyl169.feudalism.command.ruler;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import com.gmail.chernobyl169.feudalism.FeudalismPlugin;

public class QSpawnCommand implements CommandExecutor {

	private FeudalismPlugin plugin;
	
	public QSpawnCommand(FeudalismPlugin plugin) { this.plugin = plugin; }
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("qspawn")) {
			if (!(sender instanceof Player)) {
				sender.sendMessage(plugin.notPlayerString());
				return true;
			}
			if (!sender.hasPermission("kingdoms.ruler")) {
				sender.sendMessage(plugin.noPermissionString());
				return true;
			}
			Player player = (Player)sender;
			plugin.getUser(player.getName()).setBackTarget(player.getLocation());
			player.teleport(plugin.getEmbassySpawn(), TeleportCause.PLUGIN);
			return true;			
		}
		return false;
	}

}
