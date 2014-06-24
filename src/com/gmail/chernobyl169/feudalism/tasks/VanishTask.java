package com.gmail.chernobyl169.feudalism.tasks;

import java.util.LinkedList;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import com.gmail.chernobyl169.feudalism.FeudalismPlugin;

public class VanishTask extends BukkitRunnable {

	private final Player player;
	private FeudalismPlugin plugin;
	
	public VanishTask(FeudalismPlugin plugin, Player player) {
		this.player = player;
		this.plugin = plugin;
	}
	
	@Override
	public void run() {
		List<Player> list = new LinkedList<Player>();
		for (Player p : plugin.getServer().getOnlinePlayers()) {
			p.hidePlayer(player);
			list.add(p);
		}
		player.setMetadata("vanished", new FixedMetadataValue(plugin, list));
		new UnvanishTask(plugin, player).runTaskLater(plugin, 300);
	}

}
