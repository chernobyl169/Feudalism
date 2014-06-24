package com.gmail.chernobyl169.feudalism.command.ruler;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import com.gmail.chernobyl169.feudalism.FeudalismPlugin;
import com.gmail.chernobyl169.feudalism.User;

public class QBackCommand implements CommandExecutor {

	private FeudalismPlugin plugin;
	
	public QBackCommand(FeudalismPlugin plugin) { this.plugin = plugin; }
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("qback")) {
			if (!(sender instanceof Player)) {
				sender.sendMessage(plugin.notPlayerString());
				return true;
			}
			if (!sender.hasPermission("kingdoms.ruler")) {
				sender.sendMessage(plugin.noPermissionString());
				return true;
			}
			User user = plugin.getUser(sender.getName());
			Location loc = user.getBackTarget();
			if (loc == null) {
				sender.sendMessage(ChatColor.DARK_RED + "No point of return set." + ChatColor.RESET);
				return true;
			}
			Player player = (Player)sender;
			user.setBackTarget(player.getLocation());
			player.teleport(loc, TeleportCause.PLUGIN);
			return true;
		}
		return false;
	}

}
