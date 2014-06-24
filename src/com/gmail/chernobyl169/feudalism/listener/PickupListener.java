package com.gmail.chernobyl169.feudalism.listener;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.minecart.HopperMinecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.meta.ItemMeta;

import com.gmail.chernobyl169.feudalism.FeudalismPlugin;
import com.gmail.chernobyl169.feudalism.Quadrant;
import com.gmail.chernobyl169.feudalism.User;
import com.gmail.chernobyl169.feudalism.locking.BlockChecker;

public class PickupListener implements Listener {

	private final FeudalismPlugin plugin;
	
	public PickupListener(FeudalismPlugin plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler(ignoreCancelled=true)
	public void onItemMove(InventoryMoveItemEvent event) {
		if (event.getSource().getHolder() instanceof BlockState && event.getDestination().getHolder() instanceof HopperMinecart) {
			if (BlockChecker.isProtected(((BlockState)event.getSource().getHolder()).getBlock()))
				event.setCancelled(true);
		}
	}
	
	@EventHandler(ignoreCancelled=true)
	public void onEggLay(ItemSpawnEvent event) {
		if (event.getEntity().getItemStack().getType() == Material.EGG) {
			Location loc = event.getLocation();
			for (Entity e : event.getEntity().getNearbyEntities(1, 1, 1)) {
				if (e instanceof Chicken) {
					if (loc.getWorld() == plugin.getWorld() && (!plugin.isBadlands(loc.getBlockX(), loc.getBlockZ()))) {
						if (plugin.getRights().chicken() == Quadrant.quadOf(loc.getBlockX(), loc.getBlockZ())) return;
					}				
					event.setCancelled(true);
					return;
				}
			}
		}
	}
	
	@EventHandler(ignoreCancelled=true)
	public void onPickup(PlayerPickupItemEvent event) {
		Player player = event.getPlayer();
		Location loc = player.getLocation();
		User user = plugin.getUser(player.getName());
		if (loc.getWorld() == plugin.getWorld() && !plugin.isBadlands(loc.getBlockX(), loc.getBlockZ())) {
			Quadrant q = Quadrant.quadOf(loc.getBlockX(), loc.getBlockZ());
			if (user.getQuad() != q && user.getFavor(q) == 3) event.setCancelled(true);
		}
		if (!event.isCancelled()) {
			ItemMeta meta = event.getItem().getItemStack().getItemMeta();
			if (meta.hasLore() && meta.hasDisplayName()) {
				for (Player p : plugin.getServer().getOnlinePlayers()) {
					p.sendMessage(player.getName() + " has acquired " + ChatColor.RED + meta.getDisplayName() + ChatColor.RESET + "!");
					p.playSound(p.getLocation(), Sound.WITHER_SPAWN, 1, 2.5f);
				}
			}
		}
	}
}
