package com.gmail.chernobyl169.feudalism.listener;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.gmail.chernobyl169.feudalism.FeudalismPlugin;
import com.gmail.chernobyl169.feudalism.Quadrant;
import com.gmail.chernobyl169.feudalism.User;
import com.gmail.chernobyl169.feudalism.tasks.AppsListTask;
import com.gmail.chernobyl169.feudalism.tasks.WelcomeNewUserTask;

public class LoginListener implements Listener {

	private final FeudalismPlugin plugin;
	
	public LoginListener(FeudalismPlugin plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler(priority=EventPriority.LOWEST)
	public void onPreLogin(AsyncPlayerPreLoginEvent event) {
		if (event.getLoginResult() != AsyncPlayerPreLoginEvent.Result.ALLOWED) { return; }
		plugin.setLastLoginAddress(event.getName(), event.getAddress().getHostAddress());
	}
	
	@EventHandler
	public void onLogin(PlayerJoinEvent event) {
		Player p = event.getPlayer();
		User player = plugin.playerLogin(p);
		if (plugin.isNew(p.getName())) {
			plugin.clearMember(p.getName());
			p.teleport(plugin.getNubSpawn());
			p.setBedSpawnLocation(plugin.getNubSpawn(), true);
			new WelcomeNewUserTask(plugin, p).runTask(plugin);
		}
		if (player.getRank() > 2) {
			new AppsListTask(p, plugin).runTask(plugin);
		}
		for (Quadrant q : Quadrant.values()) {
			if (player.getFavor(q) == 3 && q != player.getQuad()) {
				p.sendMessage("You are " + ChatColor.RED + "wanted" + ChatColor.RESET + " in " + ChatColor.AQUA + q.longName() + ChatColor.RESET + ".");
			}
		}
	}
	
	@EventHandler
	public void onLogout(PlayerQuitEvent event) {
		User user = plugin.getUser(event.getPlayer().getName());
		for (User u : plugin.getUsers()) {
			if (u.getReplyTarget() == user) u.setReplyTarget(null);
		}
		plugin.playerLogout(event.getPlayer());
	}
}
