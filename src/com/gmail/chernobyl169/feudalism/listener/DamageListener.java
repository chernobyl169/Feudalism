package com.gmail.chernobyl169.feudalism.listener;

import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.Sound;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.Cow;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Hanging;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Item;
import org.bukkit.entity.Pig;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.ItemDespawnEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.gmail.chernobyl169.feudalism.FeudalismPlugin;
import com.gmail.chernobyl169.feudalism.Quadrant;
import com.gmail.chernobyl169.feudalism.User;
import com.gmail.chernobyl169.feudalism.tasks.NotifyAllTask;
import com.gmail.chernobyl169.feudalism.tasks.NotifyTask;

public class DamageListener implements Listener {

	private final FeudalismPlugin plugin;
	private final Random random;
	
	public DamageListener(FeudalismPlugin plugin) {
		this.plugin = plugin;
		random = new Random();
	}
	
	public boolean denyPvP(Player source, Player target) {
		if (source == target) return false; // You can always hurt yourself.
		
		User us = plugin.getUser(source.getName()), ut = plugin.getUser(target.getName());
		Quadrant sq = us.getQuad(), tq = ut.getQuad();
		
		Location sl = source.getLocation(), tl = target.getLocation();
		Quadrant slq = Quadrant.quadOf(sl.getBlockX(), sl.getBlockZ()), tlq = Quadrant.quadOf(tl.getBlockX(), tl.getBlockZ());
		boolean sb = plugin.isBadlands(sl.getBlockX(), sl.getBlockZ()), tb = plugin.isBadlands(tl.getBlockX(), tl.getBlockZ()); 

		if (sq != tq) {
			if (ut.getFavor(sq) == 3) { // If the target is wanted...
				return false; // allow wanted hunting
			}
			if (us.getFavor(tq) == 3) { // If the source is wanted...
				return false; // allow self-defense
			}
		}

		if (sl.getWorld() != plugin.getWorld()) return true; // Deny nether/end
		if (sb && tb) return false; // Allow badlands
		
		if ((sq == tq) && (sq != null)) return true; // Deny same-kingdom members		
		
		if (sb) { // Attacking from badlands, allow if:
			if (tlq == sq) { // Target is in source's quad and..
				if (ut.getFavor(sq) > 1) return false; // Target hated in source quad
				if (plugin.isAtWar(sq, tq)) return false; // Source at war with target
			}
			if (tlq == tq) { // Target is at home and...
				if (us.getFavor(tq) > 1) return false; // Source hated in target quad
				if (plugin.isAtWar(tq, sq)) return false; // Target at war with source
			}
			return true; // Otherwise, deny attacking from badlands
		}
		if (tb) { // Attacking into badlands, allow if:
			if (slq == tq) { // Source is in target's quad and..
				if (us.getFavor(tq) > 1) return false; // Source hated in target quad
				if (plugin.isAtWar(tq, sq)) return false; // Target at war with source
			}
			if (slq == sq) { // Source is at home and..
				if (ut.getFavor(sq) > 1) return false; // Target hated in source quad
				if (plugin.isAtWar(sq, tq)) return false; // Source at war with target
			}
			return true; // Otherwise, deny attacking into badlands
		}
		if (slq == sq) { // Attacking from home, allow if:
			if (tlq == sq) { // Target is in that quad and..
				if (ut.getFavor(sq) > 1) return false; // Target hated in this quad
				if (plugin.isAtWar(sq, tq)) return false; // Source at war with target
			}
			if (tlq == tq) { // Target is at home and..
				if (plugin.isAtWar(tq, sq)) return false; // Target at war with source
			}
			return true; // Otherwise, deny attacking from home
		}
		if (slq == tq) { // Attacking from target's home, allow if:
			if (tlq == tq) { // Target is home and..
				if (us.getFavor(tq) > 1) return false; // Source is hated in this quad
				if (plugin.isAtWar(tq, sq)) return false; // Target is at war with source
			}
			if (tlq == sq) { // Target is in source's home and..
				if (us.getFavor(tq) > 1) return false; // Source is hated in this quad
				if (ut.getFavor(sq) > 1) return false; // Target is hated in source's home
				if (plugin.isAtWar(sq, tq)) return false; // Source is at war with target
			}
			return true; // Otherwise, deny attacking from target's home
		}
		
		return true; // Deny default (grant asylum)
	}
		
	@SuppressWarnings("deprecation")
	@EventHandler(ignoreCancelled=true)
	public void onDeath(PlayerDeathEvent event) {
		Player killed = event.getEntity();
		Player killer = killed.getKiller();
		if (killed.getBedSpawnLocation() == null) { killed.setBedSpawnLocation(plugin.getEmbassySpawn()); }
		if (killer == null) return;
		if (killer == killed) return;
		User ud = plugin.getUser(killed.getName()), uk = plugin.getUser(killer.getName());
		Quadrant qd = ud.getQuad(), qk = uk.getQuad();
		if (qd == qk) return;
		if (ud.getFavor(qk) == 3) {
			if (qd == null || plugin.getStatus(qk, qd) < 0) {
				ItemStack head = new ItemStack(Material.SKULL_ITEM, 1, (short) 0, (byte)SkullType.PLAYER.ordinal());
				ItemMeta meta = head.getItemMeta();
				SkullMeta sm = (SkullMeta)meta;
				sm.setOwner(killed.getName());
				head.setItemMeta(sm);
				event.getDrops().add(head);
			}
			plugin.raiseFavor(qk, ud.getName());
			plugin.raiseFavor(qk, ud.getName());
			killed.sendMessage(String.format("You are now " + ChatColor.DARK_RED + "unwelcome" + ChatColor.RESET + " in " + ChatColor.AQUA + qk.longName() + ChatColor.RESET + "."));
			new NotifyTask(plugin, killed.getName() + " has been slain, and is now " + ChatColor.DARK_RED + "unwelcome" + ChatColor.RESET +".", qk).runTask(plugin);
		}
		if (plugin.isAtWar(qk, qd) && ud.isKing(qd)) {
			plugin.neutralizeStatus(qk, qd);
			new NotifyTask(plugin, ChatColor.AQUA + qk.longName() + ChatColor.RESET + " and " + ChatColor.AQUA + qd.longName() + ChatColor.RESET + " are now " + ChatColor.BLUE + " neutral" + ChatColor.RESET + ".", qk).runTask(plugin);
			new NotifyTask(plugin, ChatColor.AQUA + qd.longName() + ChatColor.RESET + " and " + ChatColor.AQUA + qk.longName() + ChatColor.RESET + " are now " + ChatColor.BLUE + " neutral" + ChatColor.RESET + ".", qd).runTask(plugin);
			new NotifyAllTask(plugin, ud.getName() + " has been defeated!").runTask(plugin);
		}		
	}
	
	@EventHandler(ignoreCancelled=true)
	public void onDespawn(ItemDespawnEvent event) { // Lore items cannot despawn
		if ((event.getEntity()).getItemStack().getItemMeta().hasLore()) {
			event.setCancelled(true);
		}
	}
	
/*	@EventHandler(ignoreCancelled=true)
	public void onCombust(EntityCombustEvent event) {
		if (event.getEntityType() == EntityType.DROPPED_ITEM) { // Lore items are invulnerable to combustion
			if (((Item)event.getEntity()).getItemStack().getItemMeta().hasLore()) {
				event.setCancelled(true);
			}
		}
	}
*/	
	
	@EventHandler(ignoreCancelled=true, priority=EventPriority.LOW)
	public void onDamage(EntityDamageEvent event) {
		if (event.getCause() == DamageCause.VOID) return;  
		
		if (event.getEntityType() == EntityType.DROPPED_ITEM) { // Lore items cannot be exploded
			if (((Item)event.getEntity()).getItemStack().getItemMeta().hasLore()) {
				event.setCancelled(true); 
			}
		}
		if (event.getEntityType() == EntityType.HORSE) {
			Horse horse = (Horse)event.getEntity();
			ItemStack saddle = horse.getInventory().getSaddle();
			if (saddle != null && saddle.getType() == Material.SADDLE && saddle.getItemMeta().hasLore() && saddle.getItemMeta().hasDisplayName()) {
				if (saddle.getItemMeta().getDisplayName().equals("Sulami's Saddle")) {
					event.setCancelled(true);
					if (random.nextFloat() < 0.25) {
						Entity rider = horse.getPassenger();
						if (rider != null && rider instanceof Player) {
							horse.eject();
							Player player = (Player)rider;
							player.damage(event.getDamage(), horse);
							player.playSound(horse.getLocation(), Sound.HORSE_IDLE, 2, 1);
							player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 120, 1));
							player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 120, 2));
							player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 120, 2));
						}
					}
				}
			}
		}
	}
	
	@EventHandler(ignoreCancelled=true)
	public void onDamage(EntityDamageByEntityEvent event) {		
		Entity target = event.getEntity();		
		Entity damager = event.getDamager();
		Player source = null;
		
		if (damager instanceof EnderPearl) {
			if (target instanceof Hanging) event.setCancelled(true);
			return;
		} // Allow EP damages, except for knocking down hanging items
		
		if (damager instanceof Projectile) {
			org.bukkit.projectiles.ProjectileSource ps = ((Projectile)damager).getShooter();
			if (ps instanceof Entity) damager = (Entity)ps;
		} 
		if (damager instanceof Player) {
			source = (Player)damager;
		}
		
		if (source != null) {
			if (target instanceof Player) {
				if (denyPvP(source, (Player)target)) event.setCancelled(true);
				return;
			}
			if (target.getLocation().getWorld() != plugin.getWorld()) { return; }
			if (!plugin.canAct(target.getLocation().getBlockX(), target.getLocation().getBlockZ(), source.getName())) {
				if (!plugin.canGrief(target.getLocation(), source.getName())) {
					boolean ok = true;
					if (target instanceof Sheep) { ok = false; }
					if (target instanceof Cow) { ok = false; }
					if (target instanceof Pig) { ok = false; }
					if (target instanceof Chicken) { ok = false; }
					if (target instanceof Horse) { ok = false; }
					if (target instanceof Villager) { ok = false; }
					if (target instanceof Hanging) { ok = false; }
					if (!ok) {
						event.setCancelled(true);
						source.sendMessage(plugin.noPermissionString());
					}
				} else
					plugin.griefAlert(Quadrant.quadOf(target.getLocation().getBlockX(), target.getLocation().getBlockZ()), source.getName());
			}
		} else {
			if (target instanceof Hanging) event.setCancelled(true);
		}
	}
}
