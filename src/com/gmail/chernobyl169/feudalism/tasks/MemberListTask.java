package com.gmail.chernobyl169.feudalism.tasks;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.gmail.chernobyl169.feudalism.FeudalismPlugin;
import com.gmail.chernobyl169.feudalism.Quadrant;

public class MemberListTask extends BukkitRunnable {

	private final FeudalismPlugin plugin;
	private final Player player;
	private final Quadrant q;
	
	public MemberListTask(Player player, FeudalismPlugin plugin) {
		this.player = player;
		this.plugin = plugin;
		q = plugin.membership(player.getName());
	}
	
	private String rankColor(String name) {
		switch (plugin.getFavor(q, name)) {
		case 1:
			return ChatColor.GREEN.toString();
		case 2:
			return ChatColor.YELLOW.toString();
		case 3:
			return ChatColor.GOLD.toString();
		case 4:
			return ChatColor.AQUA.toString();
		default:
			return "";
		}
	}
	@Override
	public void run() {
		List<String> list = plugin.getMembers(q);
		player.sendMessage("Your kingdom members (" + list.size() + "):");
		StringBuffer sb = new StringBuffer();
		for (String n : list) {
			sb.append (rankColor(n) + n + ChatColor.RESET + " "); }
		player.sendMessage(sb.toString());
	}

}
