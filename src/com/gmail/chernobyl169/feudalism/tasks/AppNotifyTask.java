package com.gmail.chernobyl169.feudalism.tasks;

import org.bukkit.ChatColor;

import com.gmail.chernobyl169.feudalism.FeudalismPlugin;
import com.gmail.chernobyl169.feudalism.Quadrant;

public class AppNotifyTask extends NotifyTask {
	
	public AppNotifyTask(FeudalismPlugin plugin, String user, Quadrant quad) {
		super(plugin, ChatColor.AQUA + user + ChatColor.RESET + " has applied to join your kingdom!", quad);
	}

}
