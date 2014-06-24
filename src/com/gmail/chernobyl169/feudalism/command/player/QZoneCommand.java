package com.gmail.chernobyl169.feudalism.command.player;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.chernobyl169.feudalism.FeudalismPlugin;
import com.gmail.chernobyl169.feudalism.Quadrant;

public class QZoneCommand implements CommandExecutor {

	private FeudalismPlugin plugin;
	
	public QZoneCommand(FeudalismPlugin plugin) { this.plugin = plugin; }
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("qzone")) {
			if (sender.hasPermission("kingdoms.player")) {
				Player p = null;
				if (sender instanceof Player) p = (Player)sender;
				if (p == null) { 
					sender.sendMessage(plugin.noAccessString());
					return true;
				}
				Location loc = p.getLocation();
				if (p.getWorld() != plugin.getWorld()) {
					sender.sendMessage("You're not in the overworld.");
					return true;
				}
				if (plugin.isBadlands(loc.getBlockX(), loc.getBlockZ())) {
					sender.sendMessage("(" + loc.getBlockX() + ", " + loc.getBlockZ() + ")");
					sender.sendMessage("You're in the " + ChatColor.RED + "badlands" + ChatColor.RESET + ".");
					return true;
				}
				Quadrant q = Quadrant.quadOf(loc.getBlockX(), loc.getBlockZ());
				sender.sendMessage("(" + loc.getBlockX() + ", " + loc.getBlockZ() + ")");
				sender.sendMessage("You're in " + ChatColor.AQUA + q.longName() + ChatColor.GOLD +"[" + q.toString().toUpperCase() + "]" + ChatColor.RESET + ".");
				return true;
			}
			sender.sendMessage(plugin.noAccessString());
			return true;
		}
		return false;
	}

}
