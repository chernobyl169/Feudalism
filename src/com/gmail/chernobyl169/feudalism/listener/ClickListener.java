package com.gmail.chernobyl169.feudalism.listener;

import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.Cow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Horse;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Pig;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.SmallFireball;
import org.bukkit.entity.Villager;
import org.bukkit.entity.minecart.HopperMinecart;
import org.bukkit.entity.minecart.PoweredMinecart;
import org.bukkit.entity.minecart.StorageMinecart;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerLeashEntityEvent;
import org.bukkit.event.player.PlayerEggThrowEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.gmail.chernobyl169.feudalism.FeudalismPlugin;
import com.gmail.chernobyl169.feudalism.Quadrant;
import com.gmail.chernobyl169.feudalism.User;
import com.gmail.chernobyl169.feudalism.locking.BlockChecker;
import com.gmail.chernobyl169.feudalism.locking.Lock;
import com.gmail.chernobyl169.feudalism.locking.LockSign;
import com.gmail.chernobyl169.feudalism.locking.Shop;
import com.gmail.chernobyl169.feudalism.locking.Shop.ShopResult;
import com.gmail.chernobyl169.feudalism.locking.Trade;
import com.gmail.chernobyl169.feudalism.tasks.UpdateInventoryTask;
import com.gmail.chernobyl169.feudalism.tasks.VanishTask;

public class ClickListener implements Listener {

	private final FeudalismPlugin plugin;
	private final Random random;

	public ClickListener(FeudalismPlugin plugin) {
		this.plugin = plugin;
		random = new Random();
	}
	
	@EventHandler(ignoreCancelled=true)
	public void onEggThrow(PlayerEggThrowEvent event) {
		Player player = event.getPlayer();
		if (player.hasPermission("kingdoms.admin")) { return; }
		Location loc = event.getEgg().getLocation();
		if (loc.getWorld() != plugin.getWorld()) {
			event.setHatching(false);
			player.sendMessage(plugin.noPermissionString());			
			return;
		}
		if (!plugin.canFarm(plugin.getRights().chicken(), loc, player.getName())) {
			event.setHatching(false);
			player.sendMessage(plugin.noPermissionString());
			return;
		}
	}
	
	@EventHandler(ignoreCancelled=true)
	public void onPlayerLeash(PlayerLeashEntityEvent event) {
		Player player = event.getPlayer();
		if (player.hasPermission("kingdoms.admin")) { return; }
		User user = plugin.getUser(player.getName());
		Location loc = event.getEntity().getLocation();
		if (loc.getWorld() != plugin.getWorld()) {
			if (user.getQuad() == null) {
				event.setCancelled(true);
				player.sendMessage(plugin.noPermissionString());
			}
			return;
		}
		if (!plugin.canAct(loc.getBlockX(), loc.getBlockZ(), player.getName())) {
			if (!plugin.canGrief(loc, player.getName())) {
				if (!plugin.canVisitorAct(Quadrant.quadOf(loc.getBlockX(), loc.getBlockZ()), player.getName())) {
					event.setCancelled(true);
					player.sendMessage(plugin.noPermissionString());
					return;
				}
			} else {
				if (!plugin.kingsOnline(Quadrant.quadOf(loc.getBlockX(), loc.getBlockZ()), user.getQuad())) {
					event.setCancelled(true);
					player.sendMessage(plugin.warGriefBlockedString());
					return;
				}
				plugin.griefAlert(Quadrant.quadOf(loc.getBlockX(), loc.getBlockZ()), player.getName());
			}
		}
	}
	
	@EventHandler(ignoreCancelled=true)
	public void onConsume(PlayerItemConsumeEvent event) {
		ItemStack food = event.getItem();
		if (food.getType() == Material.COOKED_BEEF && food.getItemMeta().hasLore() && food.getItemMeta().hasDisplayName()) {
			if (food.getItemMeta().getDisplayName().equals("Kheed's Steak")) {
				event.setCancelled(true);
				Player player = event.getPlayer();
				player.setFoodLevel(20);
				player.damage(4.0);
				player.removePotionEffect(PotionEffectType.FIRE_RESISTANCE);
				player.setFireTicks(80);
				player.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 600, 2));
				player.playSound(player.getLocation(), Sound.BURP, 3, 0.8f);
				player.playSound(player.getLocation(), Sound.FIRE, 1, 1);
				new UpdateInventoryTask(player).runTask(plugin);
			}
		}
	}
	
	// This could probably use some optimization.
	@EventHandler
	public void onInteractBlock(PlayerInteractEvent event) {
		Block block = event.getClickedBlock();
		Player player = event.getPlayer();
		ItemStack item = event.getItem();
		if (player.hasPermission("kingdoms.admin")) { return; }
		User user = plugin.getUser(player.getName());
		if (event.getAction() == Action.PHYSICAL) {
			switch (block.getType()) {
			case WOOD_PLATE:
			case STONE_PLATE:
			case IRON_PLATE:
			case GOLD_PLATE:
			case STRING:
			case TRIPWIRE:
				return;
			default:
				event.setCancelled(true);
				return;
			}
		}
		if (event.getAction() == Action.LEFT_CLICK_BLOCK && block.getType() == Material.WALL_SIGN) {
			if (plugin.isEnterSign(block.getLocation())) {
				event.setCancelled(true);
				player.teleport(plugin.getNubSpawn(), TeleportCause.PLUGIN);
				return;
			}
			if (plugin.isExitSign(block.getLocation())) {
				event.setCancelled(true);
				player.teleport(plugin.getEmbassySpawn(), TeleportCause.PLUGIN);
				if (player.getBedSpawnLocation() == null) { player.setBedSpawnLocation(plugin.getEmbassySpawn()); }
				return;
			}
			Shop s = BlockChecker.getShopSign(block);
			if (s != null && s.isValid()) {
				if (plugin.canShop(block.getLocation(), player.getName())) {
					player.sendMessage(String.format(
						"This shop %s %s%d %s%s for %s%d %s%s.",
						s.isBounty() ? "buys" : "sells",
						ChatColor.YELLOW.toString(),
						s.getQuantity(),
						s.getItem().shopName(),
						ChatColor.RESET.toString(),
						ChatColor.YELLOW.toString(),
						s.getAmount(),
						s.getCurrency().getName(),
						ChatColor.RESET.toString()
						));
					if (s.hasRoom()) {
						if (s.isEmpty()) {
							player.sendMessage(plugin.shopEmptyString());
						} else {
							player.sendMessage(String.format(
								"There is enough stock for %s%d%s transactions.",
								ChatColor.AQUA.toString(),
								s.isBounty() ?  s.amountRemaining()/s.getAmount() : s.quantityRemaining()/s.getQuantity(),
								ChatColor.RESET.toString()
								));
						}
					} else {
						player.sendMessage(plugin.shopFullString());
					}
					return;
				}
				event.setCancelled(true);
				player.sendMessage(plugin.noPermissionString());
				return;
			}
			Trade t = BlockChecker.getTradeSign(block);
			if (t != null && t.isValid() && !t.ownedBy(player.getName())) {
				if (t.getOther() == null) {
					Sign sign = (Sign)block.getState();
					sign.setLine(2, player.getName());
					sign.update();
				} else if (t.isOther(player.getName())) {
					Sign sign = (Sign)block.getState();
					sign.setLine(2, "");
					sign.update();
				}
				return;
			}
		}
		if (event.getAction() == Action.LEFT_CLICK_BLOCK && event.getMaterial() == Material.SADDLE) {
			if (item.getItemMeta().hasLore() && item.getItemMeta().hasDisplayName() && item.getItemMeta().getDisplayName().equals("Sulami's Saddle")) {
				if (block.getWorld() != plugin.getWorld()) {
					if (user.getQuad() == null) {
						return;
					}
					if (block.getType() == Material.OBSIDIAN) {
						if (block.getWorld() == plugin.getServer().getWorld("world_nether")) {
							if (user.getQuad() != Quadrant.quadOf(block.getX(), block.getZ())) {
								return;
							}
						} else if (block.getWorld() == plugin.getServer().getWorld("world_the_end")) {
							return;
						}
					}
				} else {
					if (!plugin.canAct(block.getX(), block.getZ(), user.getName())) {
						if (!plugin.canGrief(block.getLocation(), user.getName())) {
							if (!plugin.canVisitorAct(Quadrant.quadOf(block.getX(), block.getZ()), user.getName())) {
								return;
							}
						} else {
							if (!plugin.kingsOnline(Quadrant.quadOf(block.getX(), block.getZ()), user.getQuad())) {
								return;
							}
							plugin.griefAlert(Quadrant.quadOf(block.getX(), block.getZ()), player.getName());
						}
					}
				}
				LockSign lock = BlockChecker.getLockOf(block);
				if (lock != null && lock.isValid() && !lock.ownedBy(user.getName())) return;
				switch (block.getType()) {
				case SUGAR_CANE:
				case SUGAR_CANE_BLOCK:
					Location loc = block.getLocation();
					if ((block.getWorld() != plugin.getWorld()) ||
							(plugin.isBadlands(block.getX(), block.getZ())) ||
							(plugin.getRights().sugar() != Quadrant.quadOf(block.getX(), block.getZ()))) {
						event.setCancelled(true);
						while (loc.getBlock().getType() == Material.SUGAR_CANE_BLOCK) {
							loc.setY(loc.getY() - 1);
						}
						loc.setY(loc.getY() + 1);
						loc.getBlock().setType(Material.AIR);
					}
					return;
				case BEDROCK:
				case ENDER_PORTAL_FRAME:
					return;
				default:
					break;
				}
				block.setType(Material.AIR);
				if (new Random().nextFloat() < 0.25) {
					player.damage(4.0);
				}
				return;
			}
		}
		if (event.getMaterial() == Material.BONE && event.getItem().getItemMeta().hasLore() && event.getItem().getItemMeta().hasDisplayName()) {
			ItemStack bone = event.getItem();
			if (bone.getItemMeta().getDisplayName().equals("Bone of Marius")) {
				switch (event.getAction()) {
				case LEFT_CLICK_AIR:
					return;
				case LEFT_CLICK_BLOCK:
					block.getWorld().strikeLightning(random.nextFloat() < 0.1 ? player.getLocation() : block.getLocation());
					break;
				case RIGHT_CLICK_AIR:
				case RIGHT_CLICK_BLOCK:
					player.launchProjectile(SmallFireball.class);
					player.removePotionEffect(PotionEffectType.FIRE_RESISTANCE);
					player.setFireTicks(140);
					player.damage(2.0);
					if (player.getFoodLevel() > 17) { player.setFoodLevel(17); }
					break;
				default:
					break;
				}
			}
		}
		if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) { return; }
		if (player.getLocation().getWorld() == plugin.getWorld() && event.getMaterial() == Material.FEATHER && item.getItemMeta().hasLore() && item.getItemMeta().hasDisplayName()) {
			if (item.getItemMeta().getDisplayName().equals("Ghech's Feather")) {
				event.setCancelled(true);
				player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 300, 1));
				player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 2400, 1));
				player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 2400, 1));
				player.playSound(player.getLocation(), Sound.LEVEL_UP, 1, 0.5f);
				player.playSound(player.getLocation(), Sound.AMBIENCE_THUNDER, 1, 1);
				player.damage(10.0);
				player.setFoodLevel(0);
				plugin.getWorld().setTime(0);
				plugin.getWorld().setStorm(false);
				return;
			}
		}
		if (event.getAction() != Action.RIGHT_CLICK_BLOCK) { return; }
		if (event.getMaterial() == Material.MAP && event.getItem().getItemMeta().hasLore() && event.getItem().getItemMeta().hasDisplayName()) {
			ItemStack map = event.getItem();
			if (map.getItemMeta().getDisplayName().equals("Camreto's Map")) {
				boolean canBreak = true;
				if (block.getWorld() != plugin.getWorld()) {
					if (user.getQuad() == null) {
						canBreak = false;
					}
				} else {
					if (!plugin.canAct(block.getX(), block.getZ(), user.getName())) {
						if (!plugin.canGrief(block.getLocation(), user.getName())) {
							if (!plugin.canVisitorAct(Quadrant.quadOf(block.getX(), block.getZ()), user.getName())) {
								canBreak = false;
							}
						} else {
							if (!plugin.kingsOnline(Quadrant.quadOf(block.getX(), block.getZ()), user.getQuad())) {
								canBreak = false;
							}
							plugin.griefAlert(Quadrant.quadOf(block.getX(), block.getZ()), player.getName());
						}
					}
				}
				if (canBreak) {
					event.setCancelled(true);
					Location bl = block.getLocation();
					block.getWorld().createExplosion(bl.getX(), bl.getY(), bl.getZ(), 4f, false, true);
					player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 80, 1));
					player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 300, 0));
					player.playSound(player.getLocation(), Sound.GLASS, 1, 1);
					new VanishTask(plugin, player).runTask(plugin);
					return;
				}
			}
		}
		LockSign lock = BlockChecker.getLockOf(block);
		if (lock != null && lock.isValid()) {
			Quadrant lq = Quadrant.quadOf(block.getX(), block.getZ());
			if (plugin.isBadlands(block.getX(), block.getZ())) lq = null;
			if (plugin.getWorld() != block.getWorld()) lq = null;
			if (!lock.ownedBy(user.getName()) && (!user.isKing(user.getQuad()) || (lq != null && user.getQuad() != lq))) {
				if (!(block.getType() == Material.WALL_SIGN && lock instanceof Shop && plugin.canShop(block.getLocation(), user.getName()))) {
					if (block.getType() == Material.CHEST) {
						if (lock instanceof Trade) {
							if (!((Trade)lock).isOther(user.getName())) {
								event.setCancelled(true);
								event.setUseInteractedBlock(Result.DENY);
								player.sendMessage(plugin.noPermissionString());
							}
							return;
						}
					}
					if (lock instanceof Lock) {
						if (user.getQuad() == lq && lq != null) {
							if (!((Lock)lock).canAccess(user.getRank())) {
								event.setCancelled(true);
								event.setUseInteractedBlock(Result.DENY);
								player.sendMessage(plugin.noPermissionString());
							}
							return;
						}
					}
					event.setCancelled(true);
					event.setUseInteractedBlock(Result.DENY);
					player.sendMessage(plugin.noPermissionString());
					return;
				} else {
					item = player.getItemInHand();
					if (item != null && item.getType() != Material.AIR) {
						Shop shop = (Shop)lock;
						ShopResult res = null;
						if (shop.isBounty()) {
							if (shop.getItem().itemMatch(item)) {
								res = shop.transact(player);
							}
						} else {
							if (shop.getCurrency().material() == item.getType()) {
								res = shop.transact(player);
							}
						}
						if (res != null) {
							switch(res) {
							case OVER:
								player.sendMessage(ChatColor.RED + "You dropped what you couldn't carry." + ChatColor.RESET);
							case OK:
								player.sendMessage(String.format(
									"You %s %s%d %s%s for %s%d %s%s.",
									shop.isBounty() ? "sold" : "bought",
									ChatColor.YELLOW.toString(),
									shop.getQuantity(),
									shop.getItem().shopName(),
									ChatColor.RESET.toString(),
									ChatColor.YELLOW.toString(),
									shop.getAmount(),
									shop.getCurrency().getName(),
									ChatColor.RESET.toString()
									));
								break;
							case EMPTY:
								player.sendMessage(plugin.shopEmptyString());
								break;
							case FULL:
								player.sendMessage(plugin.shopFullString());
								break;
							case SHORT:
								player.sendMessage(plugin.notEnoughShopString());
								break;
							}
						}
					}
					event.setCancelled(true);
					return;
				}
			}
		}
		switch (event.getMaterial()) {
		case BOAT:
		case MINECART:
			return;
		case FLINT_AND_STEEL:
		case EXPLOSIVE_MINECART:
		case FIREBALL:
			if (user.getRank() < 2) {
				event.setCancelled(true);
				player.sendMessage(plugin.noPermissionString());
			}
		default:
			break;
		}
		if (block.getWorld() != plugin.getWorld()) {
			if (user.getQuad() == null) {
				event.setCancelled(true);
				player.sendMessage(plugin.noPermissionString());
			}
			return;
		}
		if (!plugin.canAct(block.getX(), block.getZ(), player.getName())) {
			if (event.getMaterial() == Material.ITEM_FRAME) {
				event.setCancelled(true);
				player.sendMessage(plugin.noPermissionString());
				return;
			}
			if (plugin.canGrief(block.getLocation(), player.getName())) {
				if (!plugin.kingsOnline(Quadrant.quadOf(block.getX(), block.getZ()), user.getQuad())) {
					event.setCancelled(true);
					player.sendMessage(plugin.warGriefBlockedString());
					return;
				}
				plugin.griefAlert(Quadrant.quadOf(block.getX(), block.getZ()), player.getName());
				return;
			}
			if (!plugin.isSpawn(block.getX(), block.getZ()) && plugin.canVisitorAct(Quadrant.quadOf(block.getX(), block.getZ()), player.getName())) return;
			switch (block.getType()) {
			case CHEST:
			case TRAPPED_CHEST:
			case FURNACE:
			case BURNING_FURNACE:
			case BEACON:
			case BREWING_STAND:
			case DISPENSER:
			case DROPPER:
			case HOPPER:
			case ANVIL:
			case JUKEBOX:
			case CAKE_BLOCK:
			case CAULDRON:
			case STONE_BUTTON:
			case WOOD_BUTTON:
			case LEVER:
				event.setUseInteractedBlock(Result.DENY);
				player.sendMessage(plugin.noPermissionString());
				break;
			case WOOD_DOOR:
			case WOODEN_DOOR:
			case BED_BLOCK:
			case BED:
				if (user.getQuad() == Quadrant.quadOf(block.getX(), block.getZ())) break;
				if (user.getFavor(Quadrant.quadOf(block.getX(), block.getZ())) > 0) {
					event.setUseInteractedBlock(Result.DENY);
					event.setCancelled(true);
					player.sendMessage(plugin.noPermissionString());
					return;
				}
				if (plugin.getStatus(Quadrant.quadOf(block.getX(), block.getZ()), user.getQuad()) < 0) {
					event.setUseInteractedBlock(Result.DENY);
					event.setCancelled(true);
					player.sendMessage(plugin.noPermissionString());
					return;
				}
				break;
			default:
				break;
			}
		}
	}
		
	// This could probably use some optimization.
	@EventHandler(ignoreCancelled=true)
	public void onInteractEntity(PlayerInteractEntityEvent event) {
		Entity entity = event.getRightClicked();
		Player player = event.getPlayer();
		if (player.hasPermission("kingdoms.admin")) { return; }
		Location loc = entity.getLocation();
		Material item = player.getItemInHand().getType();
		User user = plugin.getUser(player.getName());
		if (loc.getWorld() != plugin.getWorld()) {
			boolean fail = false;
			if (entity instanceof Villager) fail = true;
			if (entity instanceof Chicken) {
				switch (item) {
				case SEEDS:
				case PUMPKIN_SEEDS:
				case MELON_SEEDS:
				case NETHER_WARTS:
					fail = true;
				default:
					break;
				}
			}
			if (entity instanceof Sheep) {
				switch(item) {
				case SHEARS:
				case WHEAT:
					fail = true;
				default:
					break;
				}
			}
			if (entity instanceof Cow) {
				switch (item) {
				case WHEAT:
				case BUCKET:
				case BOWL:
				case SHEARS:
					fail = true;
				default:
					break;
				}
			}
			if (entity instanceof Horse) {
				switch (item) {
				case GOLDEN_CARROT:
				case GOLDEN_APPLE:
					fail = true;
				default:
					break;
				}
			}
			if (entity instanceof Pig) {
				switch (item) {
				case SADDLE:
					if (player.getItemInHand().hasItemMeta()) {
						ItemMeta meta = player.getItemInHand().getItemMeta();
						if (meta.hasLore() && meta.hasDisplayName()) {
							if (meta.getDisplayName().equals("Sulami's Saddle")) {
								fail = true;
							}
						}
					}
					break;
				case CARROT_ITEM:
					fail = true;
				default:
					break;
				}
			}

			if (fail) {
				event.setCancelled(true);
				player.sendMessage(plugin.noPermissionString());
			}
			return;
		}
		if (entity instanceof Villager) {
			if (!plugin.canAct(loc.getBlockX(), loc.getBlockZ(), player.getName())) {
				event.setCancelled(true);
				player.sendMessage(plugin.noPermissionString());
				return;
			}
			if (plugin.isBadlands(loc.getBlockX(), loc.getBlockZ())) return;
			if (user.getRank() < 2) {
				event.setCancelled(true);
				player.sendMessage(plugin.noPermissionString());
			}
		}
		if (entity instanceof Sheep) {
			switch (item) {
			case SHEARS:
			case WHEAT:
				if (!plugin.canFarm(plugin.getRights().sheep(), loc, player.getName())) {
					event.setCancelled(true);
					player.sendMessage(plugin.noPermissionString());
					return;
				}
			default:
				break;
			}
		}
		if (entity instanceof Cow) {
			switch (item) {
			case WHEAT:
			case BUCKET:
			case BOWL:
			case SHEARS:
				if (!plugin.canFarm(plugin.getRights().cow(), loc, player.getName())) {
					event.setCancelled(true);
					player.sendMessage(plugin.noPermissionString());
					return;
				}
			default:
				break;
			}
		}
		if (entity instanceof Horse) {
			switch (item) {
			case GOLDEN_CARROT:
			case GOLDEN_APPLE:
				if (!plugin.canFarm(plugin.getRights().horse(), loc, player.getName())) {
					event.setCancelled(true);
					player.sendMessage(plugin.noPermissionString());
					return;
				}
			default:
				break;
			}
		}
		if (entity instanceof Pig) {
			if (item == Material.CARROT_ITEM) {
				if (!plugin.canFarm(plugin.getRights().pig(), loc, player.getName())) {
					event.setCancelled(true);
					player.sendMessage(plugin.noPermissionString());
					return;
				}
			}
			if (item == Material.SADDLE && player.getItemInHand().hasItemMeta()) {
				ItemMeta meta = player.getItemInHand().getItemMeta();
				if (meta.hasLore() && meta.hasDisplayName()) {
					if (meta.getDisplayName().equals("Sulami's Saddle")) {
						event.setCancelled(true);
						player.sendMessage(plugin.noPermissionString());
						return;
					}
				}
			}
		}
		if (entity instanceof Chicken) {
			switch (item) {
			case SEEDS:
			case PUMPKIN_SEEDS:
			case MELON_SEEDS:
			case NETHER_WARTS:
				if (!plugin.canFarm(plugin.getRights().chicken(), loc, player.getName())) {
					event.setCancelled(true);
					player.sendMessage(plugin.noPermissionString());
					return;
				}
			default:
				break;
			}
		}
		if (entity instanceof StorageMinecart && !plugin.canAct(loc.getBlockX(), loc.getBlockZ(), player.getName())) {
			event.setCancelled(true);
			player.sendMessage(plugin.noPermissionString());
			return;
		}
		if (entity instanceof HopperMinecart && !plugin.canAct(loc.getBlockX(), loc.getBlockZ(), player.getName())) {
			event.setCancelled(true);
			player.sendMessage(plugin.noPermissionString());
			return;
		}
		
		if (entity instanceof PoweredMinecart && !plugin.canAct(loc.getBlockX(), loc.getBlockZ(), player.getName())) {
			event.setCancelled(true);
			player.sendMessage(plugin.noPermissionString());
			return;
		}
		if (entity instanceof ItemFrame && !plugin.canAct(loc.getBlockX(), loc.getBlockZ(), player.getName())) {
			event.setCancelled(true);
			player.sendMessage(plugin.noPermissionString());
			return;
		}
	}
}
