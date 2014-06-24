package com.gmail.chernobyl169.feudalism.command.donor;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.chernobyl169.feudalism.FeudalismPlugin;

public class QSetHomeCommand implements CommandExecutor {

	private FeudalismPlugin plugin;
	
	public QSetHomeCommand(FeudalismPlugin plugin) { this.plugin = plugin; }
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("qsethome")) {
			if (!(sender instanceof Player)) {
				sender.sendMessage(plugin.notPlayerString());
				return true;
			}
			if (!sender.hasPermission("kingdoms.donor")) {
				sender.sendMessage(plugin.noAccessString());
				return true;
			}
			Player player = (Player)sender;
			if (player.getHealth() < player.getMaxHealth()) {
				sender.sendMessage(ChatColor.RED + "Can't set home; you must be at full health!" + ChatColor.RESET);
				return true;
			}
			if (player.getFoodLevel() < 20) {
				sender.sendMessage(ChatColor.RED + "Can't set home; you must be at full hunger!" + ChatColor.RESET);
				return true;
			}
			if (player.getLevel() < 15) {
				sender.sendMessage(ChatColor.RED + "Can't set home; you don't have enough levels!" + ChatColor.RESET);
				return true;				
			}
			Location loc = player.getLocation();
			if (loc.getWorld() != plugin.getWorld()) {
				sender.sendMessage(ChatColor.RED + "Can't set home; you must be in the overworld!" + ChatColor.RESET);
				return true;
			}
			sender.sendMessage("Home spawn point set!");
			player.setLevel(player.getLevel() - 15);
			player.setBedSpawnLocation(loc, true);
			return true;
		}
		return false;
	}

}
