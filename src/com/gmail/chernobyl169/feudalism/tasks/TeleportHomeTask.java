package com.gmail.chernobyl169.feudalism.tasks;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.scheduler.BukkitRunnable;

public class TeleportHomeTask extends BukkitRunnable {

	private Player player;
	private int x, y, z;
	
	public TeleportHomeTask(Player player, int x, int y, int z) {
		this.player = player;
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	@Override
	public void run() {
		Location loc = player.getLocation();
		if (player.getBedSpawnLocation() == null) {
			player.sendMessage(ChatColor.RED + "Teleport cancelled; you have no home!" + ChatColor.RESET);
			return;
		}
		if (loc.getBlockX() != x || loc.getBlockY() != y || loc.getBlockZ() != z) {
			player.sendMessage(ChatColor.RED + "Teleport cancelled; you moved!" + ChatColor.RESET);
			return;
		}
		if (player.getHealth() < player.getMaxHealth()) {
			player.sendMessage(ChatColor.RED + "Teleport cancelled; not at full health!" + ChatColor.RESET);
			return;
		}
		if (player.getFoodLevel() < 20) {
			player.sendMessage(ChatColor.RED + "Teleport cancelled; not at full hunger!" + ChatColor.RESET);
			return;
		}
		if (player.getLevel() < 15) {
			player.setLevel(0);
		} else {
			player.setLevel(player.getLevel() - 15);
		}
		player.setExp(0);
		player.setTotalExperience(0);
		player.teleport(player.getBedSpawnLocation(), TeleportCause.PLUGIN);
	}

}
