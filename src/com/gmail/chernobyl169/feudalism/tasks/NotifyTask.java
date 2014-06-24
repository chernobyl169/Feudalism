package com.gmail.chernobyl169.feudalism.tasks;

import java.util.Iterator;

import org.bukkit.scheduler.BukkitRunnable;

import com.gmail.chernobyl169.feudalism.FeudalismPlugin;
import com.gmail.chernobyl169.feudalism.Quadrant;
import com.gmail.chernobyl169.feudalism.User;

public class NotifyTask extends BukkitRunnable {

	private FeudalismPlugin plugin;
	private final String message;
	private final Quadrant quad;
	
	public NotifyTask(FeudalismPlugin plugin, String message, Quadrant quad) {
		this.plugin = plugin;
		this.message = message;
		this.quad = quad;
	}
	
	@Override
	public void run() {
		Iterator<User> it = plugin.getUsers().iterator();
		while (it.hasNext()) {
			User u = it.next();
			if (u.getQuad() == quad && u.getRank() > 2) {
				u.sendMessage(message);
			}
		}
	}
}
