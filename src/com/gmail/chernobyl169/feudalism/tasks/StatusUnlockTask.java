package com.gmail.chernobyl169.feudalism.tasks;

import org.bukkit.scheduler.BukkitRunnable;

import com.gmail.chernobyl169.feudalism.FeudalismPlugin;
import com.gmail.chernobyl169.feudalism.Quadrant;

public class StatusUnlockTask extends BukkitRunnable {
	
	private FeudalismPlugin plugin;
	private final Quadrant q;
	
	public StatusUnlockTask(FeudalismPlugin plugin, Quadrant q) {
		this.plugin = plugin;
		this.q = q;
	}

	@Override
	public void run() {
		plugin.statusUnlock(q);
	}

}
