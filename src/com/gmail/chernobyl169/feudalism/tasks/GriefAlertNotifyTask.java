package com.gmail.chernobyl169.feudalism.tasks;

import org.bukkit.ChatColor;

import com.gmail.chernobyl169.feudalism.FeudalismPlugin;
import com.gmail.chernobyl169.feudalism.Quadrant;

public class GriefAlertNotifyTask extends NotifyTask {

	public GriefAlertNotifyTask(FeudalismPlugin plugin, String griefer, Quadrant q) {
		super(plugin, ChatColor.RED + griefer + ChatColor.RESET + " is griefing your kingdom!", q);
	}
}
