package com.gmail.chernobyl169.feudalism.tasks;

import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.gmail.chernobyl169.feudalism.FeudalismPlugin;

public class AppsListTask extends BukkitRunnable {

	private final Player player;
	private final FeudalismPlugin plugin;
	
	public AppsListTask(Player player, FeudalismPlugin plugin) {
		this.player = player;
		this.plugin = plugin;
	}
	
	@Override
	public void run() {
		List<String> list = plugin.getApplicants(plugin.membership(player.getName()));
		if (list.size() == 0) {
			player.sendMessage("You have no awaiting applicants.");
			return;
		}
		player.sendMessage("Awaiting applicants:");
		StringBuffer sb = new StringBuffer();
		for (String n : list) { sb.append (n + " "); }
		player.sendMessage(sb.toString());
	}

}
