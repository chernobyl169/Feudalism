package com.gmail.chernobyl169.feudalism.tasks;

import java.util.Iterator;

import org.bukkit.scheduler.BukkitRunnable;

import com.gmail.chernobyl169.feudalism.FeudalismPlugin;
import com.gmail.chernobyl169.feudalism.User;

public class NotifyAllTask extends BukkitRunnable {

	private FeudalismPlugin plugin;
	private final String message;
	
	public NotifyAllTask(FeudalismPlugin plugin, String message) {
		this.plugin = plugin;
		this.message = message;
	}
	@Override
	public void run() {
		Iterator<User> it = plugin.getUsers().iterator();
		while (it.hasNext()) {
			it.next().sendMessage(message);
		}
	}

}
