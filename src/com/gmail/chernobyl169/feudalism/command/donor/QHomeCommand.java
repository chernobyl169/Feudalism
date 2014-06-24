package com.gmail.chernobyl169.feudalism.command.donor;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import com.gmail.chernobyl169.feudalism.FeudalismPlugin;
import com.gmail.chernobyl169.feudalism.tasks.TeleportHomeTask;

public class QHomeCommand implements CommandExecutor {

	private FeudalismPlugin plugin;
	
	public QHomeCommand(FeudalismPlugin plugin) { this.plugin = plugin; }
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("qhome")) {
			if (!(sender instanceof Player)) {
				sender.sendMessage(plugin.notPlayerString());
				return true;
			}
			if (sender.hasPermission("kingdoms.ruler")) {
				Player player = (Player)sender;
				plugin.getUser(player.getName()).setBackTarget(player.getLocation());
				player.teleport(player.getBedSpawnLocation(), TeleportCause.PLUGIN);
				return true;
			}
			if (!sender.hasPermission("kingdoms.donor")) {
				sender.sendMessage(plugin.noAccessString());
				return true;
			}
			Player player = (Player)sender;
			if (player.getBedSpawnLocation() == null) {
				sender.sendMessage(ChatColor.RED + "Can't teleport; you have no home!" + ChatColor.RESET);
				return true;
			}
			if (player.getHealth() < player.getMaxHealth()) {
				sender.sendMessage(ChatColor.RED + "Can't teleport; you must be at full health!" + ChatColor.RESET);
				return true;
			}
			if (player.getFoodLevel() < 20) {
				sender.sendMessage(ChatColor.RED + "Can't teleport; you must be at full hunger!" + ChatColor.RESET);
				return true;
			}
			Location loc = player.getLocation();
			if (loc.getWorld() != plugin.getWorld()) {
				sender.sendMessage(ChatColor.RED + "Can't teleport; you must be in the overworld!" + ChatColor.RESET);
				return true;
			}
//			if (player.getLevel() < 15) {
//				sender.sendMessage(ChatColor.RED + "Can't teleport; you don't have enough levels!" + ChatColor.RESET);
//				return true;				
//			}
			sender.sendMessage("Teleporting to your bed, don't move.");
			new TeleportHomeTask(player, loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()).runTaskLater(plugin, 100);
			return true;
		}
		return false;
	}

}
