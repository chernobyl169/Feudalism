package com.gmail.chernobyl169.feudalism.tasks;

import org.bukkit.scheduler.BukkitRunnable;

import com.gmail.chernobyl169.feudalism.FeudalismPlugin;

public class FavorUnlockTask extends BukkitRunnable {

	private FeudalismPlugin plugin;
	private final String user;
	
	public FavorUnlockTask(FeudalismPlugin plugin, String user) {
		this.plugin = plugin;
		this.user = user;
	}
	
	@Override
	public void run() {
		plugin.favorUnlock(user);
	}

}
