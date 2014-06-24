package com.gmail.chernobyl169.feudalism.tasks;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.gmail.chernobyl169.feudalism.FeudalismPlugin;

public class WelcomeNewUserTask extends BukkitRunnable {

	private final Player player;
	private FeudalismPlugin plugin;
	private String welcomeString, welcomeOtherString;
	
	public WelcomeNewUserTask(FeudalismPlugin plugin, Player player) {
		this.player = player;
		this.plugin = plugin;
		welcomeString = ChatColor.LIGHT_PURPLE + "Welcome to Dr. Coffee's Good-Time Minecraft," + ChatColor.RESET + " " + player.getName() + ChatColor.LIGHT_PURPLE + "! Stay a while, and have a cup.";
		welcomeOtherString = ChatColor.LIGHT_PURPLE + "Welcome our newest user," + ChatColor.RESET + " " + player.getName() + ChatColor.LIGHT_PURPLE + "!";
	}
	
	@Override
	public void run() {
		for (Player p : plugin.getServer().getOnlinePlayers()) {
			if (p == player) {
				p.sendMessage(welcomeString);
			} else {
				p.sendMessage(welcomeOtherString);
			}
		}
	}

}
