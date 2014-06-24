package com.gmail.chernobyl169.feudalism.tasks;

import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import com.gmail.chernobyl169.feudalism.FeudalismPlugin;

public class UnvanishTask extends BukkitRunnable {

	private final Player player;
	private FeudalismPlugin plugin;
	
	public UnvanishTask(FeudalismPlugin plugin, Player player) {
		this.player = player;
		this.plugin = plugin;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void run() {
		List<Player> list = null;
		for (MetadataValue v : player.getMetadata("vanished")) {
			if (v.getOwningPlugin() == plugin) {
				list = (List<Player>)v.value();
			}
		}
		if (list != null) {
			for (Player p : list) {
				p.showPlayer(player);
			}
		}
		player.removeMetadata("vanished", plugin);
	}

}
