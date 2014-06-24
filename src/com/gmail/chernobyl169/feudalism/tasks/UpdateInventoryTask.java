package com.gmail.chernobyl169.feudalism.tasks;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class UpdateInventoryTask extends BukkitRunnable {

	private final Player player;
	
	public UpdateInventoryTask(Player player) {
		this.player = player;
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void run() {
		player.updateInventory();
	}

}
