package com.gmail.chernobyl169.feudalism.listener;

import java.util.Iterator;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.PistonMoveReaction;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.inventory.InventoryHolder;

import com.gmail.chernobyl169.feudalism.FeudalismPlugin;
import com.gmail.chernobyl169.feudalism.Quadrant;
import com.gmail.chernobyl169.feudalism.User;
import com.gmail.chernobyl169.feudalism.locking.BlockChecker;
import com.gmail.chernobyl169.feudalism.locking.LockSign;
import com.gmail.chernobyl169.feudalism.locking.ShopItem;
import com.gmail.chernobyl169.feudalism.locking.Util;
import com.gmail.chernobyl169.feudalism.tasks.NotifyAllTask;
import com.gmail.chernobyl169.feudalism.tasks.NotifyTask;

public class BlockListener implements Listener {
	
	private final FeudalismPlugin plugin;
	
	public BlockListener(FeudalismPlugin plugin) {
		this.plugin = plugin;
	}
	
	private boolean canAct(BlockEvent event, String name) {
		return plugin.canAct(event.getBlock().getX(), event.getBlock().getZ(), name);
	}
	private boolean boundary(Location from, Location to) {
		boolean cross = false;
		if (plugin.isSpawn(from.getBlockX(), from.getBlockZ()))
			cross |= !plugin.isSpawn(to.getBlockX(), to.getBlockZ());
		else
			cross |= plugin.isSpawn(to.getBlockX(), to.getBlockZ());
		if (plugin.isBadlands(from.getBlockX(), from.getBlockZ())) {
			cross |= !plugin.isBadlands(to.getBlockX(), to.getBlockZ());
		} else {
			cross |= plugin.isBadlands(to.getBlockX(), to.getBlockZ());
			cross |= Quadrant.quadOf(from.getBlockX(), from.getBlockZ()) != Quadrant.quadOf(to.getBlockX(), to.getBlockZ());
		}
		return cross;
	}
	
	@EventHandler
	public void onRedstoneChange(BlockRedstoneEvent event) {
		Block b = event.getBlock();
		switch (b.getType()) {
		case STONE_BUTTON:
		case WOOD_BUTTON:
		case LEVER:
			return; // Don't lock redstone from buttons/levers
		default:
			LockSign lock = BlockChecker.getLockOf(b);
			if (lock != null && lock.isValid()) event.setNewCurrent(event.getOldCurrent());
		}
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler(ignoreCancelled=true)
	public void onSignWrite(SignChangeEvent event) {
		if (event.getPlayer().hasPermission("kingdoms.admin")) return;
		if (event.getLine(0).equalsIgnoreCase("[lock]")) {
			Location loc = event.getBlock().getLocation();
			if (loc.getWorld() != plugin.getWorld()) event.setLine(1, "");
			else if (plugin.isBadlands(loc.getBlockX(), loc.getBlockZ())) event.setLine(1, "");
			else if (Quadrant.quadOf(loc.getBlockX(), loc.getBlockZ()) != plugin.membership(event.getPlayer().getName())) event.setLine(1, "");
			event.setLine(3, Util.chopped(event.getPlayer().getName()));
		} else if (event.getLine(0).equalsIgnoreCase("[shop]") || event.getLine(0).equalsIgnoreCase("[bounty]")) {
			String line = Util.chopped(event.getPlayer().getName());
			ShopItem si = ShopItem.getByName(event.getLine(1)); 
			if (si != null) {
				if (event.getLine(0).equalsIgnoreCase("[shop]"))  {
					Quadrant q = plugin.membership(event.getPlayer().getName());
					Location loc = event.getBlock().getLocation();
					if (loc.getWorld() == plugin.getWorld()) {
						if (plugin.isBadlands(loc.getBlockX(), loc.getBlockZ())) {
							q = null;
						} else if (Quadrant.quadOf(loc.getBlockX(), loc.getBlockZ()) != q) {
							q = null;
						}
					}
					switch (si) {
					case CLAY:
					case CLAY_BLOCK:
					case BRICK:
					case BRICK_SLAB:
					case BRICK_STAIR:
					case CLAY_BRICK:
					case HARD_CLAY:
					case WHITE_CLAY:
					case ORANGE_CLAY:
					case MAGENTA_CLAY:
					case SKY_CLAY:
					case YELLOW_CLAY:
					case LIME_CLAY:
					case PINK_CLAY:
					case GRAY_CLAY:
					case SILVER_CLAY:
					case CYAN_CLAY:
					case PURPLE_CLAY:
					case BLUE_CLAY:
					case BROWN_CLAY:
					case GREEN_CLAY:
					case RED_CLAY:
					case BLACK_CLAY:
						if (plugin.getRights().clay() != q) line = "";
						break;
					case RAW_FISH:
					case RAW_SALMON:
					case RAW_CLOWNFISH:
					case RAW_PUFFERFISH:
					case FISH:
					case SALMON:
					case CLOWNFISH:
					case PUFFERFISH:
						if (plugin.getRights().fish() != q) line = "";
						break;
					case WITHER_SKULL:
					case NETHER_STAR:
					case BEACON:
						if (plugin.getRights().wither() != q) line = "";
					case AWKWARD_POTION:
					case THICK_POTION:
					case MUNDANE_POTION:
					case REGEN_POTION:
					case SPEED_POTION:
					case FIRE_POTION:
					case POISON_POTION:
					case HEAL_POTION:
					case SIGHT_POTION:
					case WEAK_POTION:
					case STRONG_POTION:
					case SLOW_POTION:
					case DAMAGE_POTION:
					case INVIS_POTION:
					case REGEN_2_POTION:
					case SPEED_2_POTION:
					case POISON_2_POTION:
					case HEAL_2_POTION:
					case STRONG_2_POTION:
					case DAMAGE_2_POTION:
					case REGEN_X_POTION:
					case SPEED_X_POTION:
					case FIRE_X_POTION:
					case POISON_X_POTION:
					case SIGHT_X_POTION:
					case WEAK_X_POTION:
					case STRONG_X_POTION:
					case SLOW_X_POTION:
					case INVIS_X_POTION:
					case REGEN_SPLASH:
					case SPEED_SPLASH:
					case FIRE_SPLASH:
					case POISON_SPLASH:
					case HEAL_SPLASH:
					case SIGHT_SPLASH:
					case WEAK_SPLASH:
					case STRONG_SPLASH:
					case SLOW_SPLASH:
					case DAMAGE_SPLASH:
					case INVIS_SPLASH:
					case REGEN_2_SPLASH:
					case SPEED_2_SPLASH:
					case POISON_2_SPLASH:
					case HEAL_2_SPLASH:
					case STRONG_2_SPLASH:
					case DAMAGE_2_SPLASH:
					case REGEN_X_SPLASH:
					case SPEED_X_SPLASH:
					case FIRE_X_SPLASH:
					case POISON_X_SPLASH:
					case SIGHT_X_SPLASH:
					case WEAK_X_SPLASH:
					case STRONG_X_SPLASH:
					case SLOW_X_SPLASH:
					case INVIS_X_SPLASH:
						if (plugin.getRights().potion() != q) line = "";
						break;
					case CARROT:
					case GOLDEN_CARROT:
					case CARROT_ON_STICK:
						if (plugin.getRights().carrot() != q) line = "";
						break;
					case WHEAT:
					case HAY_BALE:
					case BREAD:
					case COOKIE:
						if (plugin.getRights().wheat() != q) line = "";
						break;
					case POTATO:
					case BAKED_POTATO:
					case POISON_POTATO:
						if (plugin.getRights().potato() != q) line = "";
						break;
					case SUGAR_CANE:
					case SUGAR:
					case PAPER:
						if (plugin.getRights().sugar() != q) line = "";
						break;
					case MELON:
					case MELON_BLOCK:
					case GOLDEN_MELON:
						if (plugin.getRights().melon() != q) line = "";
						break;
					case PUMPKIN:
					case JACK_O_LANTERN:
						if (plugin.getRights().pumpkin() != q) line = "";
						break;
					case NETHER_WART:
						if (plugin.getRights().wart() != q) line = "";
						break;
					case LEATHER:
					case MILK_BUCKET:
					case STEAK:
					case COOKED_STEAK:
					case ITEM_FRAME:
						if (plugin.getRights().cow() != q) line = "";
						break;
					case FEATHER:
					case CHICKEN:
					case COOKED_CHICKEN:
					case EGG:
						if (plugin.getRights().chicken() != q) line = "";
						break;
					case PORKCHOP:
					case COOKED_PORKCHOP:
						if (plugin.getRights().pig() != q) line = "";
						break;
					case WHITE_WOOL:
					case ORANGE_WOOL:
					case MAGENTA_WOOL:
					case SKY_WOOL:
					case YELLOW_WOOL:
					case LIME_WOOL:
					case PINK_WOOL:
					case GRAY_WOOL:
					case SILVER_WOOL:
					case CYAN_WOOL:
					case PURPLE_WOOL:
					case BLUE_WOOL:
					case BROWN_WOOL:
					case GREEN_WOOL:
					case RED_WOOL:
					case BLACK_WOOL:
					case WHITE_CARPET:
					case ORANGE_CARPET:
					case MAGENTA_CARPET:
					case SKY_CARPET:
					case YELLOW_CARPET:
					case LIME_CARPET:
					case PINK_CARPET:
					case GRAY_CARPET:
					case SILVER_CARPET:
					case CYAN_CARPET:
					case PURPLE_CARPET:
					case BLUE_CARPET:
					case BROWN_CARPET:
					case GREEN_CARPET:
					case RED_CARPET:
					case BLACK_CARPET:
					case PAINTING:
					case BED:
						if (plugin.getRights().sheep() != q) line = "";
						break;
					default:
						break;
					}
				}
			} else {
				line = "";
			}
			if (!Util.shopMatch(event.getLine(2))) line = "";
			Block chest = event.getBlock().getRelative(BlockFace.DOWN);
			if (chest.getType() != Material.CHEST) line = "";
			event.setLine(3, line);
		} else if (event.getLine(0).equalsIgnoreCase("[trade]")) {
			Block chest = null;
			switch (event.getBlock().getData()) {
			case 3:
				chest = event.getBlock().getRelative(BlockFace.NORTH);
				break;
			case 2:
				chest = event.getBlock().getRelative(BlockFace.SOUTH);
				break;
			case 5:
				chest = event.getBlock().getRelative(BlockFace.WEST);
				break;
			case 4:
				chest = event.getBlock().getRelative(BlockFace.EAST);
				break;
			}
			if (chest == null || chest.getType() != Material.CHEST) {
				event.setLine(3, "");
			} else {
				event.setLine(3, Util.chopped(event.getPlayer().getName()));
			}
		}
	}
	
	@EventHandler(ignoreCancelled=true)
	public void onPistonPush(BlockPistonExtendEvent event) {
		Iterator<Block> it = event.getBlocks().iterator();
		boolean locked = false;
		while(it.hasNext() && locked == false) {
			if(BlockChecker.isProtected(it.next())) locked = true;
		}
		if (locked) {
			event.setCancelled(true);
			return;
		}
		if (event.getBlock().getWorld() != plugin.getWorld()) return;
		Block b = event.getBlock(), check = b.getRelative(event.getDirection(), event.getLength() + 1);
		if (boundary(b.getLocation(), check.getLocation())) { event.setCancelled(true); }
	}
	
	@EventHandler(ignoreCancelled=true)
	public void onPistonPull(BlockPistonRetractEvent event) {
		if (!event.isSticky()) return;
		Block b = event.getRetractLocation().getBlock().getRelative(event.getDirection());
		if (b.getType() == Material.AIR) return;
		if (BlockChecker.isProtected(b)) {
			event.setCancelled(true);
			return;
		}
		if (event.getBlock().getWorld() != plugin.getWorld()) return;
		if (b.getPistonMoveReaction() == PistonMoveReaction.BLOCK) return; 
		if (boundary(event.getRetractLocation(), b.getLocation())) { event.setCancelled(true); }
	}
	
	@EventHandler(ignoreCancelled=true)
	public void onFrameBreak(HangingBreakByEntityEvent event) {
		Entity frame = event.getEntity();
		if (frame instanceof ItemFrame) {
			Location loc = frame.getLocation();
			Entity cause = event.getRemover();
			Player player = null;
			if (cause instanceof Projectile) {
				org.bukkit.projectiles.ProjectileSource ps = ((Projectile)cause).getShooter();
				if (ps instanceof Entity) cause = (Entity)ps;
			}
			if (cause instanceof Player) {
				player = (Player)cause;
			}
			if (player != null) {
				if (player.hasPermission("kingdoms.admin")) { return; }
				User user = plugin.getUser(player.getName());
				if (frame.getWorld() != plugin.getWorld()) {
					if (user.getQuad() == null) {
						event.setCancelled(true);
						player.sendMessage(plugin.noPermissionString());
					}
					return;
				}
				if (!plugin.canAct(loc.getBlockX(), loc.getBlockZ(), player.getName())) {
					if (plugin.canGrief(loc, player.getName())) {
						if (!plugin.kingsOnline(Quadrant.quadOf(loc.getBlockX(), loc.getBlockZ()), user.getQuad())) {
							event.setCancelled(true);
							player.sendMessage(plugin.warGriefBlockedString());
						}
						plugin.griefAlert(Quadrant.quadOf(loc.getBlockX(), loc.getBlockZ()), player.getName());
						return;
					}
					event.setCancelled(true);
					player.sendMessage(plugin.noPermissionString());
				}
			} else {
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler(ignoreCancelled=true)
	public void onSpread(BlockSpreadEvent event) {
		if (event.getSource().getType() != Material.FIRE) return;
		Location from = event.getSource().getLocation();
		Location to = event.getBlock().getLocation();
		if (from.getWorld() != plugin.getWorld()) { return; }
		if (boundary(from, to)) event.setCancelled(true);
	}
	
	@EventHandler(ignoreCancelled=true)
	public void onIgnite(BlockIgniteEvent event) {
		Block b = event.getBlock();
		Location from = null, to = b.getLocation();
		if (BlockChecker.isProtected(b)) {
			event.setCancelled(true);
			return;
		}
		if (to.getWorld() != plugin.getWorld()) { return; }
		switch (event.getCause()) {
		case SPREAD:
		case LAVA:
			from = event.getIgnitingBlock().getLocation();
			break;
		case FLINT_AND_STEEL:
		case FIREBALL:
			Player player = event.getPlayer();
			if (player != null) from = player.getLocation();
			break;
		default:
			return;
		}
		if (from != null) {
			if (boundary(from, to)) event.setCancelled(true);
		}
	}
	
	@EventHandler(ignoreCancelled=true)
	public void onExplosion(EntityExplodeEvent event) {
		Location loc = event.getLocation();
		if (event.getEntity() != null) {
			switch (event.getEntityType()) {
			case CREEPER:
//			case FIREBALL:
				event.blockList().clear();
				return;
			default:
				break;
			}
		}
		boolean bound = loc.getWorld() == plugin.getWorld();
		Iterator<Block> it = event.blockList().iterator();
		while (it.hasNext()) {
			Block b = it.next();
			if (bound) {
				if (plugin.isSpawn(b.getX(), b.getZ())) it.remove();
				else if (boundary(b.getLocation(), loc)) it.remove();
			}
			if (BlockChecker.isProtected(b)) it.remove();
		}
	}
	
	@EventHandler(ignoreCancelled=true)
	public void onBucket(PlayerBucketFillEvent event) {
		Player player = event.getPlayer();
		Block block = event.getBlockClicked();
		if (player.hasPermission("kingdoms.admin")) { return; }
		User user = plugin.getUser(player.getName());
		if (block.getWorld() != plugin.getWorld()) {
			if (user.getQuad() == null) {
				event.setCancelled(true);
				player.sendMessage(plugin.noPermissionString());
				return;
			}
			if ((block.getType() == Material.LAVA) && (user.getRank() < 2)) {
				event.setCancelled(true);
				player.sendMessage(plugin.noPermissionString());
			}
			return;
		}
		if (plugin.isBadlands(block.getX(), block.getZ())) return;
		if ((block.getType() == Material.LAVA) && (user.getRank() < 2)) {
			event.setCancelled(true);
			player.sendMessage(plugin.noPermissionString());
		}
		if (!plugin.canAct(block.getX(), block.getZ(), player.getName())) {
			if (!plugin.canGrief(block.getLocation(), player.getName())) {
				if (!plugin.canVisitorAct(Quadrant.quadOf(block.getX(), block.getZ()), player.getName())) {
					event.setCancelled(true);
					player.sendMessage(plugin.noPermissionString());
					return;
				}
			} else {
				if (!plugin.kingsOnline(Quadrant.quadOf(block.getX(), block.getZ()), user.getQuad())) {
					event.setCancelled(true);
					player.sendMessage(plugin.warGriefBlockedString());
					return;
				}
				plugin.griefAlert(Quadrant.quadOf(block.getX(), block.getZ()), player.getName());
			}
		}
	}

	@EventHandler(ignoreCancelled=true)
	public void onBucket(PlayerBucketEmptyEvent event) {
		Player player = event.getPlayer();
		Block block = event.getBlockClicked().getRelative(event.getBlockFace());
		if (player.hasPermission("kingdoms.admin")) { return; }
		User user = plugin.getUser(player.getName());
		if (block.getWorld() != plugin.getWorld()) {
			if (user.getQuad() == null) {
				event.setCancelled(true);
				player.sendMessage(plugin.noPermissionString());
				return;
			}
			if ((event.getBucket() == Material.LAVA_BUCKET) && (user.getRank() < 2)) {
				event.setCancelled(true);
				player.sendMessage(plugin.noPermissionString());
			}
			return;
		}
		if (plugin.isBadlands(block.getX(), block.getZ())) return;
		if ((event.getBucket() == Material.LAVA_BUCKET) && (user.getRank() < 2)) {
			event.setCancelled(true);
			player.sendMessage(plugin.noPermissionString());
		}
		if (!plugin.canAct(block.getX(), block.getZ(), player.getName())) {
			if (!plugin.canGrief(block.getLocation(), player.getName())) {
				if (!plugin.canVisitorAct(Quadrant.quadOf(block.getX(), block.getZ()), player.getName())) {
					event.setCancelled(true);
					player.sendMessage(plugin.noPermissionString());
					return;
				}
			} else {
				if (!plugin.kingsOnline(Quadrant.quadOf(block.getX(), block.getZ()), user.getQuad())) {
					event.setCancelled(true);
					player.sendMessage(plugin.warGriefBlockedString());
					return;
				}
				plugin.griefAlert(Quadrant.quadOf(block.getX(), block.getZ()), player.getName());
			}
		}
	}
	
	@EventHandler(ignoreCancelled=true)
	public void onBreak(BlockBreakEvent event) {
		Player player = event.getPlayer();
		if (player.hasPermission("kingdoms.admin")) { return; }
		String name = player.getName();
		Block block = event.getBlock();
		User user = plugin.getUser(name);
		LockSign lock = BlockChecker.getLockOf(block);
		if (lock != null && lock.isValid() && !lock.ownedBy(user.getName())) {
			if (!user.isKing(user.getQuad()) || (block.getWorld() == plugin.getWorld() && Quadrant.quadOf(block.getX(), block.getZ()) != user.getQuad() && !plugin.isBadlands(block.getX(), block.getZ()))) {
				event.setCancelled(true);
				player.sendMessage(plugin.noPermissionString());
				return;
			}
		}
		if (block.getWorld() != plugin.getWorld()) {
			if (user.getQuad() == null) {
				event.setCancelled(true);
				player.sendMessage(plugin.noPermissionString());
				return;
			}
			if (block.getType() == Material.OBSIDIAN) {
				if (block.getWorld() == plugin.getServer().getWorld("world_nether")) {
					if (user.getQuad() != Quadrant.quadOf(block.getX(), block.getZ())) {
						event.setCancelled(true);
						player.sendMessage(plugin.noPermissionString());
						return;
					}
				} else if (block.getWorld() == plugin.getServer().getWorld("world_the_end")) {
					event.setCancelled(true);
					player.sendMessage(plugin.noPermissionString());
					return;
				}
			}
		} else {
			if (!plugin.canAct(block.getX(), block.getZ(), name)) {
				if (!plugin.canGrief(block.getLocation(), name)) {
					if (!plugin.canVisitorAct(Quadrant.quadOf(block.getX(), block.getZ()), name) || plugin.isSpawn(block.getX(), block.getZ())) {
						event.setCancelled(true);
						player.sendMessage(plugin.noPermissionString());
						return;
					}
				} else {
					if (!plugin.kingsOnline(Quadrant.quadOf(block.getX(), block.getZ()), user.getQuad())) {
						event.setCancelled(true);
						player.sendMessage(plugin.warGriefBlockedString());
						return;
					}
					switch (block.getType()) {
					case OBSIDIAN:
					case MOB_SPAWNER:
					case ENDER_PORTAL_FRAME:
						event.setCancelled(true);
						player.sendMessage(plugin.noPermissionString());
						return;
					case BEACON:
						Quadrant tq = Quadrant.quadOf(block.getX(), block.getZ());
						plugin.neutralizeStatus(user.getQuad(), tq);
						new NotifyTask(plugin, ChatColor.AQUA + tq.longName() + ChatColor.RESET + " and " + ChatColor.AQUA + user.getQuad().longName() + ChatColor.RESET + " are now " + ChatColor.BLUE + " neutral" + ChatColor.RESET + ".", tq).runTask(plugin);
						new NotifyTask(plugin, ChatColor.AQUA + user.getQuad().longName() + ChatColor.RESET + " and " + ChatColor.AQUA + tq.longName() + ChatColor.RESET + " are now " + ChatColor.BLUE + " neutral" + ChatColor.RESET + ".", user.getQuad()).runTask(plugin);
						new NotifyAllTask(plugin, ChatColor.AQUA + tq.longName() + ChatColor.RESET + " has lost a beacon!").runTask(plugin);
						return;
					default:
						plugin.griefAlert(Quadrant.quadOf(block.getX(), block.getZ()), player.getName());
						break;
					}
				}
			}
		}
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
				loc.getBlock().breakNaturally();
			}			
		default:
			break;
		}
	}
		
	// This could probably use some optimization.
	@EventHandler(ignoreCancelled=true)
	public void onPlace(BlockPlaceEvent event) {
		Player player = event.getPlayer();
		Block block = event.getBlock();
		Quadrant q = Quadrant.quadOf(block.getX(), block.getZ());
		if (player.hasPermission("kingdoms.admin")) { return; }
		User user = plugin.getUser(player.getName());
		if (block.getWorld() != plugin.getWorld()) {
			switch (block.getType()) {
			case SEEDS:
			case CROPS:
			case CARROT:
			case CARROT_ITEM:
			case SUGAR_CANE:
			case SUGAR_CANE_BLOCK:
			case POTATO:
			case POTATO_ITEM:
			case MELON_SEEDS:
			case MELON_STEM:
			case PUMPKIN_SEEDS:
			case PUMPKIN_STEM:
			case NETHER_WARTS:
			case NETHER_STALK:
				event.setCancelled(true);
				player.sendMessage(plugin.noPermissionString());
				break;
			case OBSIDIAN:
				if (block.getWorld() == plugin.getServer().getWorld("world_nether")) {
					if (user.getQuad() != Quadrant.quadOf(block.getX(), block.getZ())) {
						event.setCancelled(true);
						player.sendMessage(plugin.noPermissionString());
						return;
					}
				} else if (block.getWorld() == plugin.getServer().getWorld("world_the_end")) {
					event.setCancelled(true);
					player.sendMessage(plugin.noPermissionString());
					return;
				}
				break;
			case TNT:
				if (user.getRank() < 2) {
					event.setCancelled(true);
					player.sendMessage(plugin.noPermissionString());
					return;
				}
				break;
			case SIGN:
			case WALL_SIGN:
				if (BlockChecker.isProtected(event.getBlockAgainst())) {
					event.setCancelled(true);
					player.sendMessage(plugin.noPermissionString());
					return;
				}
				Block t = event.getBlockPlaced().getRelative(BlockFace.UP);
				if (t.getType() == Material.STONE_BUTTON || t.getType() == Material.WOOD_BUTTON || t.getType() == Material.LEVER) {
					if (BlockChecker.isProtected(t)) {
						event.setCancelled(true);
						player.sendMessage(plugin.noPermissionString());
						return;
					}
				}
				t = event.getBlockPlaced().getRelative(BlockFace.DOWN);
				switch (t.getType()) {
				case WOOD_DOOR:
				case WOOD_BUTTON:
				case STONE_BUTTON:
				case LEVER:
				case CHEST:
					if (BlockChecker.isProtected(t)) {
						event.setCancelled(true);
						player.sendMessage(plugin.noPermissionString());
						return;
					}
					break;
				default:
					break;
				}
				t = event.getBlockAgainst().getRelative(BlockFace.DOWN);
				if (t.getType() == Material.WOOD_DOOR && BlockChecker.isProtected(t)) {
					event.setCancelled(true);
					player.sendMessage(plugin.noPermissionString());
					return;					
				}
				break;
			case HOPPER:
				Block b = event.getBlock().getRelative(BlockFace.UP);
				if (b.getState() instanceof InventoryHolder) {
					LockSign lock = BlockChecker.getLockOf(b);
					if (!lock.ownedBy(user.getName())) {
						event.setCancelled(true);
						player.sendMessage(plugin.noPermissionString());
					}
				}
				break;
			default:
				if (user.getQuad() == null) {
					event.setCancelled(true);
					player.sendMessage(plugin.noPermissionString());
				}
				break;
			}
			return;
		}
		String name = player.getName();
		if (!canAct(event, name)) {
			if (!plugin.canGrief(block.getLocation(), name)) {
				if (!plugin.canVisitorAct(q, name) || plugin.isSpawn(block.getX(), block.getZ())) {
					event.setCancelled(true);
					player.sendMessage(plugin.noPermissionString());
					return;
				}
			} else {
				if (!plugin.kingsOnline(q, user.getQuad())) {
					event.setCancelled(true);
					player.sendMessage(plugin.warGriefBlockedString());
					return;
				}
				if (block.getType() == Material.OBSIDIAN) {
					event.setCancelled(true);
					player.sendMessage(plugin.noPermissionString());
					return;
				}
				plugin.griefAlert(Quadrant.quadOf(block.getX(), block.getZ()), player.getName());
			}
		}
		switch (block.getType()) {
		case TNT:
			if (!plugin.isBadlands(block.getX(), block.getZ())) {
				if (user.getRank() < 2) {
					event.setCancelled(true);
					player.sendMessage(plugin.noPermissionString());
					return;
				}
				if (plugin.canGrief(block.getLocation(), name)) {
					if (!plugin.kingsOnline(q, user.getQuad())) {
						event.setCancelled(true);
						player.sendMessage(plugin.warGriefBlockedString());
					}
				}
			}
			break;
		case HOPPER:
			Block b = event.getBlock().getRelative(BlockFace.UP);
			if (b.getState() instanceof InventoryHolder) {
				LockSign lock = BlockChecker.getLockOf(b);
				if (!lock.ownedBy(user.getName())) {
					event.setCancelled(true);
					player.sendMessage(plugin.noPermissionString());
				}
			}
			break;
		case SIGN:
		case WALL_SIGN:
			if (BlockChecker.isProtected(event.getBlockAgainst())) {
				event.setCancelled(true);
				player.sendMessage(plugin.noPermissionString());
				return;
			}
			Block t = event.getBlockPlaced().getRelative(BlockFace.UP);
			if (t.getType() == Material.STONE_BUTTON || t.getType() == Material.WOOD_BUTTON || t.getType() == Material.LEVER) {
				if (BlockChecker.isProtected(t)) {
					event.setCancelled(true);
					player.sendMessage(plugin.noPermissionString());
					return;
				}
			}
			t = event.getBlockPlaced().getRelative(BlockFace.DOWN);
			switch (t.getType()) {
			case WOOD_DOOR:
			case WOOD_BUTTON:
			case STONE_BUTTON:
			case LEVER:
			case CHEST:
				if (BlockChecker.isProtected(t)) {
					event.setCancelled(true);
					player.sendMessage(plugin.noPermissionString());
					return;
				}
				break;
			default:
				break;
			}
			t = event.getBlockAgainst().getRelative(BlockFace.DOWN);
			if (t.getType() == Material.WOOD_DOOR && BlockChecker.isProtected(t)) {
				event.setCancelled(true);
				player.sendMessage(plugin.noPermissionString());
				return;					
			}
			break;
		case SEEDS:
		case CROPS:
			if (!plugin.canFarm(plugin.getRights().wheat(), block.getLocation(), name)) {
				event.setCancelled(true);
				player.sendMessage(plugin.noPermissionString());
			}
			break;
		case CARROT_ITEM:
		case CARROT:
			if (!plugin.canFarm(plugin.getRights().carrot(), block.getLocation(), name)) {
				event.setCancelled(true);
				player.sendMessage(plugin.noPermissionString());
			}
			break;
		case SUGAR_CANE:
		case SUGAR_CANE_BLOCK:
			if (!plugin.canFarm(plugin.getRights().sugar(), block.getLocation(), name)) {
				event.setCancelled(true);
				player.sendMessage(plugin.noPermissionString());
			}
			break;
		case POTATO_ITEM:
		case POTATO:
			if (!plugin.canFarm(plugin.getRights().potato(), block.getLocation(), name)) {
				event.setCancelled(true);
				player.sendMessage(plugin.noPermissionString());
			}
			break;
		case MELON_SEEDS:
		case MELON_STEM:
			if (!plugin.canFarm(plugin.getRights().melon(), block.getLocation(), name)) {
				event.setCancelled(true);
				player.sendMessage(plugin.noPermissionString());
			}
			break;
		case PUMPKIN_SEEDS:
		case PUMPKIN_STEM:
			if (!plugin.canFarm(plugin.getRights().pumpkin(), block.getLocation(), name)) {
				event.setCancelled(true);
				player.sendMessage(plugin.noPermissionString());
			}
			break;
		case NETHER_WARTS:
		case NETHER_STALK:
			if (!plugin.canFarm(plugin.getRights().wart(), block.getLocation(), name)) {
				event.setCancelled(true);
				player.sendMessage(plugin.noPermissionString());
			}
			break;
		default:
			break;
		}
	}
}
