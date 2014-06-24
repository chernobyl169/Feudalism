package com.gmail.chernobyl169.feudalism.command.player;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.gmail.chernobyl169.feudalism.FeudalismPlugin;
import com.gmail.chernobyl169.feudalism.Quadrant;
import com.gmail.chernobyl169.feudalism.User;
import com.gmail.chernobyl169.feudalism.tasks.AppNotifyTask;
import com.gmail.chernobyl169.feudalism.tasks.AppsListTask;
import com.gmail.chernobyl169.feudalism.tasks.MemberListTask;
import com.gmail.chernobyl169.feudalism.tasks.NotifyAllTask;
import com.gmail.chernobyl169.feudalism.tasks.NotifyTask;

public class QuadCommand implements TabExecutor {

	private final FeudalismPlugin plugin;
	
	private final String selfStatus = ChatColor.DARK_RED + "Your kingdom doesn't have a status with itself." + ChatColor.RESET;
	private final String[] status = {
		ChatColor.RED + "at war" + ChatColor.RESET,
		ChatColor.DARK_RED + "hostile" + ChatColor.RESET,
		ChatColor.BLUE + "peaceful" + ChatColor.RESET,
		ChatColor.AQUA + "friendly" + ChatColor.RESET,
		ChatColor.GOLD + "allied" + ChatColor.RESET
	};
	
	public QuadCommand(FeudalismPlugin plugin) {
		this.plugin = plugin;
	}

	private String statusOf(Quadrant q, Quadrant of) {
		return status[plugin.getStatus(q, of) + 2];
	}
	
	private void sendCommandDesc(CommandSender sender, String command, String description) {
		sender.sendMessage(ChatColor.GOLD + "/q " + command + ": " + ChatColor.RESET + description);
	}
	private void sendCommandUsage(CommandSender sender, String command, String usage) {
		sender.sendMessage("Usage: " + ChatColor.GOLD + "/q " + command + " " + usage + ChatColor.RESET);
	}
	
	private void kingList(CommandSender sender) {
		sendCommandDesc(sender, "upgrade|degrade", "Adjust kingdom relations");
		sendCommandDesc(sender, "name", "Rename your kingdom");
	}
	private void memberList(CommandSender sender) {
		sendCommandDesc(sender, "list", "List current kingdom members");
		sendCommandDesc(sender, "status", "View kingdom relations");
	}
	private void nonMemberList(CommandSender sender) {
		sendCommandDesc(sender, "apply", "Apply to join a kingdom");
	}
	private void manageList(CommandSender sender) {
		sendCommandDesc(sender, "apps", "List applicants to your kingdom");
		sendCommandDesc(sender, "deny", "Deny an applicant to your kingdom");
		sendCommandDesc(sender, "add|remove", "Manage your kingdom's members");
		sendCommandDesc(sender, "promote|demote", "Adjust a player's rank");
	}
	private void adminList(CommandSender sender) {
		sender.sendMessage("-- " + ChatColor.RED + "Admin Commands" + ChatColor.RESET + " --");
		sendCommandDesc(sender, "spawn", "Set spawn size");
		sendCommandDesc(sender, "safe", "Set buffer size");
		sendCommandDesc(sender, "badlands", "Set badlands size");
		sendCommandDesc(sender, "assign", "Assign quad ruler");
		sendCommandDesc(sender, "right", "Assign farming rights");
		sendCommandDesc(sender, "set", "Set a player's membership");
		sendCommandDesc(sender, "setpromote|setdemote", "Adjust a player's home rank");
		sendCommandDesc(sender, "lore", "Add lore to an item");
		sendCommandDesc(sender, "iname", "Rename an item");
		sendCommandDesc(sender, "ench", "Enchant an item");
		sendCommandDesc(sender, "nubspawn|embspawn", "Set the spawn points");
		sendCommandDesc(sender, "signenter|signexit", "Set spawn punchsigns");
	}
	
	private void helpText(CommandSender sender) {
		if (sender instanceof Player) {
			if (sender.hasPermission("kingdoms.player")) {
				User u = plugin.getUser(((Player)sender).getName());
				if (u.getQuad() != null) {
					memberList(sender);
					if (u.getRank() > 2) {
						manageList(sender);
					}
					if (u.getRank() == 4) {
						kingList(sender);
					}
				} else {
					nonMemberList(sender);
				}
			}
		}
		if (sender.hasPermission("kingdoms.admin")) {
			adminList(sender);
		}
	}
	
	private boolean setRight(Quadrant q, String right) {
		switch (right.toLowerCase()) {
		case "cow":
			plugin.getRights().setCow(q);
			break;
		case "sheep":
			plugin.getRights().setSheep(q);
			break;
		case "pig":
			plugin.getRights().setPig(q);
			break;
		case "chicken":
			plugin.getRights().setChicken(q);
			break;
		case "horse":
			plugin.getRights().setHorse(q);
			break;
		case "sugar":
			plugin.getRights().setSugar(q);
			break;
		case "melon":
			plugin.getRights().setMelon(q);
			break;
		case "pumpkin":
			plugin.getRights().setPumpkin(q);
			break;
		case "wheat":
			plugin.getRights().setWheat(q);
			break;
		case "carrot":
			plugin.getRights().setCarrot(q);
			break;
		case "potato":
			plugin.getRights().setPotato(q);
			break;
		case "wart":
			plugin.getRights().setWart(q);
			break;
		case "clay":
			plugin.getRights().setClay(q);
			break;
		case "potion":
			plugin.getRights().setPotion(q);
			break;
		case "wither":
			plugin.getRights().setWither(q);
			break;
		case "fish":
			plugin.getRights().setFish(q);
			break;
		default:
			return false;
		}
		return true;
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("quad")) {
			if (args.length == 0) {
				helpText(sender);
				return true;
			}
			User u = null;
			if (sender instanceof Player) u = plugin.getUser(((Player)sender).getName());
			switch (args[0]) {
			// Non-member commands
			case "apply":
				if (sender.hasPermission("kingdoms.player")) {
					if (u == null) { 
						sender.sendMessage(plugin.notPlayerString());
						return true;
					}
					if (u.getQuad() != null) {
						helpText(sender);
						return true;
					}
					if (args.length != 2) {
						sendCommandUsage(sender, "apply", "[ne|se|nw|sw|clear]");
						sender.sendMessage("Use " + ChatColor.GOLD + "/zone" + ChatColor.RESET + " to see where you currently are.");
						return true;
					}
					if (args[1].equalsIgnoreCase("clear")) {
						plugin.clearMember(u.getName());
						sender.sendMessage("Pending application cancelled, if you had one.");
						return true;
					}
					Quadrant q = Quadrant.toQuadrant(args[1]);
					if (q != null) {
						plugin.setApplicant(q, u.getName());
						sender.sendMessage("You have applied to join " + ChatColor.AQUA + q.toString() + ChatColor.RESET + ".");
						new AppNotifyTask(plugin, u.getName(), q).runTask(plugin);
						return true;
					}
					sendCommandUsage(sender, "apply", "[ne|se|nw|sw|clear]");
					sender.sendMessage("Use " + ChatColor.GOLD + "/zone" + ChatColor.RESET + " to see where you currently are.");
					return true;
				}
				sender.sendMessage(plugin.noAccessString());
				return true;
			// Member commands
			case "list":
				if (sender.hasPermission("kingdoms.player")) {
					if (u != null) {
						if (u.getQuad() != null) {
							new MemberListTask((Player)sender, plugin).runTask(plugin);
							return true;
						}
						helpText(sender);
						return true;
					}
					sender.sendMessage(plugin.notPlayerString());
					return true;
				}
				sender.sendMessage(plugin.noAccessString());
				return true;
			case "status":
				if (sender.hasPermission("kingdoms.player")) {
					if (u != null) {
						if (u.getQuad() != null) {
							for (Quadrant q : Quadrant.values()) {
								if (q != u.getQuad()) {
									sender.sendMessage(ChatColor.AQUA + u.getQuad().longName() + ChatColor.GOLD + "[" + u.getQuad().toString().toUpperCase() + "]"
										+ ChatColor.RESET + " is " + statusOf(u.getQuad(), q) + " with " + ChatColor.AQUA + q.longName() + ChatColor.GOLD + "[" + q.toString().toUpperCase() + "]"
										+ ChatColor.RESET + ".");
									sender.sendMessage(ChatColor.AQUA + q.longName() + ChatColor.GOLD + "[" + q.toString().toUpperCase() + "]" + ChatColor.RESET + " is "
										+ statusOf(q, u.getQuad()) + " with " + ChatColor.AQUA + u.getQuad().longName() + ChatColor.GOLD + "[" + u.getQuad().toString().toUpperCase() + "]"
										+ ChatColor.RESET + ".");
								}
							}
							return true;
						}
						helpText(sender);
						return true;
					}
					sender.sendMessage(plugin.notPlayerString());
					return true;
				}
				sender.sendMessage(plugin.noAccessString());
				return true;
			// Admin commands
			case "ench":
				if (sender.hasPermission("kingdoms.admin")) {
					if (u != null) {
						if (args.length != 3) {
							sendCommandUsage(sender, "ench", "<enchant> <lv>");
							return true;
						}
						int level = 0;
						try {
							level = Integer.parseInt(args[2]);
						} catch (NumberFormatException e) {
							sendCommandUsage(sender, "ench", "<enchant> <lv>");
							return true;
						}
						Enchantment ench = null;
						switch(args[1]) {
						case "silk":
							ench = Enchantment.SILK_TOUCH;
							break;
						case "kb":
						case "knockback":
							ench = Enchantment.KNOCKBACK;
							break;
						case "eff":
						case "efficiency":
							ench = Enchantment.DIG_SPEED;
							break;
						case "fa":
						case "fire":
							ench = Enchantment.FIRE_ASPECT;
							break;
						case "unb":
						case "unbreaking":
							ench = Enchantment.DURABILITY;
							break;
						case "loot":
						case "looting":
							ench = Enchantment.LOOT_BONUS_MOBS;
							break;
						case "fort":
						case "fortune":
							ench = Enchantment.LOOT_BONUS_BLOCKS;
							break;
						case "inf":
						case "infinity":
							ench = Enchantment.ARROW_INFINITE;
							break;
						case "pow":
						case "power":
							ench = Enchantment.ARROW_DAMAGE;
							break;
						case "flame":
							ench = Enchantment.ARROW_FIRE;
							break;
						case "punch":
							ench = Enchantment.ARROW_KNOCKBACK;
							break;
						case "sharp":
						case "sharpness":
							ench = Enchantment.DAMAGE_ALL;
							break;
						case "boa":
						case "arth":
						case "bane":
						case "arthropods":
							ench = Enchantment.DAMAGE_ARTHROPODS;
							break;
						case "smite":
							ench = Enchantment.DAMAGE_UNDEAD;
							break;
						case "aqua":
						case "affinity":
							ench = Enchantment.WATER_WORKER;
							break;
						case "resp":
						case "respiration":
							ench = Enchantment.OXYGEN;
							break;
						case "prot":
						case "protection":
							ench = Enchantment.PROTECTION_ENVIRONMENTAL;
							break;
						case "projprot":
							ench = Enchantment.PROTECTION_PROJECTILE;
							break;
						case "fireprot":
							ench = Enchantment.PROTECTION_FIRE;
							break;
						case "feather":
						case "ff":
						case "falling":
							ench = Enchantment.PROTECTION_FALL;
							break;
						case "blastprot":
							ench = Enchantment.PROTECTION_EXPLOSIONS;
							break;
						case "thorns":
							ench = Enchantment.THORNS;
							break;
						default:
							sendCommandUsage(sender, "ench", "<enchant> <lv>");
							return true;							
						}
						
						ItemStack item = ((Player)sender).getItemInHand();
						if (item != null) {
							ItemMeta meta = item.getItemMeta();
							if (level > 0) {
								meta.addEnchant(ench, level, true);
							} else {
								meta.removeEnchant(ench);
							}
							item.setItemMeta(meta);
							sender.sendMessage("Item enchants updated.");
							return true;							
						}
						sender.sendMessage("Hold an item in hand to adjust it.");
						return true;
					}
					sender.sendMessage(plugin.notPlayerString());
					return true;
				}
				helpText(sender);
				return true;
			case "lore":
				if (sender.hasPermission("kingdoms.admin")) {
					if (u != null) {
						if (args.length < 2) {
							sendCommandUsage(sender, "lore", "[clear|<lore>]");
							return true;
						}
						ItemStack item = ((Player)sender).getItemInHand();
						if (item != null) {
							ItemMeta meta = item.getItemMeta();
							StringBuffer newName = new StringBuffer();
							newName.append(args[1]);
							if (args.length > 2) {
								for (int i = 2; i < args.length; i++) {
									newName.append(' ');
									newName.append(args[i]);
								}
							} else if (args[1].equalsIgnoreCase("clear")) {
								meta.setLore(null);
								sender.sendMessage("Item lore removed.");
								return true;
							}
							List<String> lores = null;
							if (meta.hasLore()) {
								lores = meta.getLore();
							} else {
								lores = new LinkedList<String>();
							}
							lores.add(newName.toString());
							meta.setLore(lores);
							item.setItemMeta(meta);
							sender.sendMessage("Item lore added.");
							return true;							
						}
						sender.sendMessage("Hold an item in hand to adjust it.");
						return true;
					}
					sender.sendMessage(plugin.notPlayerString());
					return true;
				}
				helpText(sender);
				return true;
			case "iname":
				if (sender.hasPermission("kingdoms.admin")) {
					if (u != null) {
						if (args.length < 2) {
							sendCommandUsage(sender, "iname", "<name>");
							return true;
						}
						ItemStack item = ((Player)sender).getItemInHand();
						if (item != null) {
							StringBuffer newName = new StringBuffer();
							newName.append(args[1]);
							if (args.length > 2) {
								for (int i = 2; i < args.length; i++) {
									newName.append(' ');
									newName.append(args[i]);
								}
							}
							ItemMeta meta = item.getItemMeta();
							meta.setDisplayName(newName.toString());
							item.setItemMeta(meta);
							sender.sendMessage("Item renamed.");
							return true;
						}
						sender.sendMessage("Hold an item in hand to adjust it.");
						return true;
					}
					sender.sendMessage(plugin.notPlayerString());
					return true;
				}
				helpText(sender);
				return true;
			case "spawn":
				if (sender.hasPermission("kingdoms.admin")) {
					if (args.length != 2) {
						sendCommandUsage(sender, "spawn", "<size>");
						return true;
					}
					int rad = 0;
					try { rad = Integer.parseInt(args[1]); }
					catch (NumberFormatException e) {
						sendCommandUsage(sender, "spawn", "<size>");
						return true;
					}
					plugin.setSpawnRadius(rad);
					sender.sendMessage(String.format("Protected spawn radius set to %d.", rad));
					return true;
				}
				helpText(sender);
				return true;
			case "badlands":
				if (sender.hasPermission("kingdoms.admin")) {
					if (args.length != 2) {
						sendCommandUsage(sender, "badlands", "<size>");
						return true;
					}
					int rad = 0;
					try { rad = Integer.parseInt(args[1]); }
					catch (NumberFormatException e) {
						sendCommandUsage(sender, "badlands", "<size>");
						return true;
					}
					plugin.setBadlandsRadius(rad);
					sender.sendMessage(String.format("Badlands radius set to %d.", rad));
					return true;
				}
				helpText(sender);
				return true;
			case "safe":
				if (sender.hasPermission("kingdoms.admin")) {
					if (args.length != 2) {
						sendCommandUsage(sender, "safe", "<size>");
						return true;
					}
					int rad = 0;
					try { rad = Integer.parseInt(args[1]); }
					catch (NumberFormatException e) {
						sendCommandUsage(sender, "safe", "<size>");
						return true;
					}
					plugin.setSafeRadius(rad);
					sender.sendMessage(String.format("Safe zone radius set to %d.", rad));
					return true;
				}
				helpText(sender);
				return true;
			case "right":
				if (sender.hasPermission("kingdoms.admin")) {
					if (args.length != 3) {
						sendCommandUsage(sender, "right", "[nw|sw|ne|se] <resource>");
						sender.sendMessage("Resources: cow chicken sheep pig horse sugar melon fish");
						sender.sendMessage("  pumpkin wheat carrot potato wart clay potion wither");
						return true;
					}
					Quadrant q = Quadrant.toQuadrant(args[1]);
					if (q != null) {
						if (setRight(q, args[2])) {
							sender.sendMessage("Farm rights assigned.");
							return true;
						}
						sender.sendMessage("No such resource!");
						return true;
					}
					sendCommandUsage(sender, "right", "[nw|sw|ne|se] <resource>");
					return true;
				}
				helpText(sender);
				return true;
			case "assign":
				if (sender.hasPermission("kingdoms.admin")) {
					if (args.length != 3) {
						sendCommandUsage(sender, "assign", "[nw|sw|ne|se] <user>");
						return true;
					}
					Quadrant q = Quadrant.toQuadrant(args[1]);
					if (q != null) {
						plugin.setKing(q, args[2]);
						sender.sendMessage(q.toString().toUpperCase() + " ruler set to " + args[2] + ".");
						return true;
					}
					sendCommandUsage(sender, "assign", "[nw|sw|ne|se] <user>");
					return true;
				}
				helpText(sender);
				return true;
			case "set":
				if (sender.hasPermission("kingdoms.admin")) {
					if (args.length != 3) {
						sendCommandUsage(sender, "set", "[nw|sw|ne|se|clear] <user>");
						return true;
					}
					Quadrant q = Quadrant.toQuadrant(args[1]);
					if (q == null) {
						plugin.clearMember(args[2]);
						sender.sendMessage("Membership cleared for " + args[2] + ".");
						User target = plugin.getUser(args[2]);
						if (target != null) {
							target.sendMessage("You are now not a member of any kingdom.");
						}
						return true;
					}
					plugin.clearMember(args[2]);
					plugin.setMember(q, args[2]);
					sender.sendMessage("Membership for " + args[2] + " set to " + args[1] + ".");
					User target = plugin.getUser(args[2]);
					if (target != null) {
						target.sendMessage("You are now a member of " + ChatColor.AQUA + q.longName() + ChatColor.RESET + ".");
					}
					return true;
				}
				helpText(sender);
				return true;
			case "setpromote":
				if (sender.hasPermission("kingdoms.admin")) {
					if (args.length != 2) {
						sendCommandUsage(sender, "setpromote", "<user>");
						return true;
					}
					Quadrant q = plugin.membership(args[1]);
					if (q == null) {
						sender.sendMessage(args[1] + " is not a member of any kingdom.");
						return true;
					}
					plugin.raiseFavor(q, args[1]);
					String fS = "now " + plugin.rankOf(q, args[1]) + " in " + ChatColor.AQUA + q.longName() + ChatColor.RESET + ".";
					sender.sendMessage(args[1] + " is " + fS);
					User target = plugin.getUser(args[1]);
					if (target != null) {
						target.sendMessage("You are now " + fS);
					}
					return true;
				}
				helpText(sender);
				return true;
			case "setdemote":
				if (sender.hasPermission("kingdoms.admin")) {
					if (args.length != 2) {
						sendCommandUsage(sender, "setpromote", "<user>");
						return true;
					}
					Quadrant q = plugin.membership(args[1]);
					if (q == null) {
						sender.sendMessage(args[1] + " is not a member of any kingdom.");
						return true;
					}
					plugin.lowerFavor(q, args[1]);
					String fS = "now " + plugin.rankOf(q, args[1]) + " in " + ChatColor.AQUA + q.longName() + ChatColor.RESET + ".";
					sender.sendMessage(args[1] + " is " + fS);
					User target = plugin.getUser(args[1]);
					if (target != null) {
						target.sendMessage("You are now " + fS);
					}
					return true;
				}
				helpText(sender);
				return true;
			case "nubspawn":
				if (sender.hasPermission("kingdoms.admin")) {
					Location loc = ((Player)sender).getLocation();
					plugin.setNubSpawn(loc);
					sender.sendMessage("Spawn entry point set!");
					return true;
				}
				helpText(sender);
				return true;				
			case "embspawn":
				if (sender.hasPermission("kingdoms.admin")) {
					Location loc = ((Player)sender).getLocation();
					plugin.setEmbassySpawn(loc);
					sender.sendMessage("Spawn exit point set!");
					return true;
				}
				helpText(sender);
				return true;				
			case "signenter":
				if (sender.hasPermission("kingdoms.admin")) {
					Block b = ((Player)sender).getTargetBlock(null, 5);
					if (b.getType() != Material.WALL_SIGN) {
						sender.sendMessage("You must target a wall sign.");
						return true;
					}
					Location loc = b.getLocation();
					plugin.setEnterSign(loc);
					sender.sendMessage("Teleport in to spawn sign set!");
					return true;
				}
				helpText(sender);
				return true;				
			case "signexit":
				if (sender.hasPermission("kingdoms.admin")) {
					Block b = ((Player)sender).getTargetBlock(null, 5);
					if (b.getType() != Material.WALL_SIGN) {
						sender.sendMessage("You must target a wall sign.");
						return true;
					}
					Location loc = b.getLocation();
					plugin.setExitSign(loc);
					sender.sendMessage("Teleport out of spawn sign set!");
					return true;
				}
				helpText(sender);
				return true;				
			// Dignitary commands
			case "apps":
				if (sender.hasPermission("kingdoms.player")) {
					if (u != null) {
						if (u.getRank() > 2) {
							new AppsListTask((Player)sender, plugin).runTask(plugin);
							return true;
						}
						helpText(sender);
						return true;
					}
					sender.sendMessage(plugin.notPlayerString());
					return true;
				}
				sender.sendMessage(plugin.noAccessString());
				return true;
			case "deny":
				if (sender.hasPermission("kingdoms.player")) {
					if (u != null) {
						if (u.getRank() > 2) {
							if (args.length != 2) {
								sendCommandUsage(sender, "deny", "<user>");
								return true;
							}
							if (plugin.isApplicant(u.getQuad(), args[1])) {
								plugin.clearMember(args[1]);
								User target = plugin.getUser(args[1]);
								if (target != null) {
									target.sendMessage(String.format("Your application to %s was denied.", ChatColor.AQUA + u.getQuad().longName() + ChatColor.RESET));
								}
								new NotifyTask(plugin, "Application of " + args[1] + " was denied.", u.getQuad()).runTask(plugin);
								return true;
							} else {
								sender.sendMessage(args[1] + " hasn't applied to join your kingdom!");
								return true;
							}
						}
						helpText(sender);
						return true;
					}
					sender.sendMessage(plugin.notPlayerString());
					return true;
				}
				sender.sendMessage(plugin.noAccessString());
				return true;
			case "add":
				if (sender.hasPermission("kingdoms.player")) {
					if (u != null) {
						if (u.getRank() > 2) {
							if (args.length != 2) {
								sendCommandUsage(sender, "add", "<user>");
								return true;
							}
							if (plugin.membership(args[1]) != null) {
								sender.sendMessage(args[1] + " is already a member of a kingdom.");
								return true;
							}
							if (plugin.isApplicant(u.getQuad(), args[1])) {
								plugin.setMember(u.getQuad(), args[1]);
								User target = plugin.getUser(args[1]);
								if (target != null) {
									target.sendMessage(String.format("You are now a member of %s!", ChatColor.AQUA + u.getQuad().longName() + ChatColor.RESET));
								}
								new NotifyTask(plugin, "Application of " + args[1] + " was accepted.", u.getQuad()).runTask(plugin);
							} else {
								sender.sendMessage(args[1] + " hasn't applied to join your kingdom.");
							}
							return true;
						}
						helpText(sender);
						return true;
					}
					sender.sendMessage(plugin.notPlayerString());
					return true;
				}
				sender.sendMessage(plugin.noAccessString());
				return true;
			case "remove":
				if (sender.hasPermission("kingdoms.player")) {
					if (u != null) {
						if (u.getRank() > 2) {
							if (args.length != 2) {
								sendCommandUsage(sender, "remove", "<user>");
								return true;
							}
							if (plugin.isKing(u.getQuad(), args[1])) {
								sender.sendMessage("You can't remove the ruler!");
								return true;
							}
							if (plugin.isMember(u.getQuad(), args[1])) {
								plugin.clearMember(args[1]);
								User target = plugin.getUser(args[1]);
								if (target != null) {
									target.sendMessage(String.format("You are no longer a member of  %s.", ChatColor.AQUA + u.getQuad().longName() + ChatColor.RESET));
								}
								new NotifyTask(plugin, args[1] + " was removed from the kingdom.", u.getQuad()).runTask(plugin);
								return true;
							} else {
								sender.sendMessage(args[1] + " isn't a member of your kingdom!");
								return true;
							}
						}
						helpText(sender);
						return true;
					}
					sender.sendMessage(plugin.notPlayerString());
					return true;
				}
				sender.sendMessage(plugin.noAccessString());
				return true;
			case "promote":
				if (sender.hasPermission("kingdoms.player")) {
					if (u != null) {
						if (u.getRank() > 2) {
							if (args.length != 2) {
								sendCommandUsage(sender, "promote", "<user>");
								return true;
							}
							if (plugin.isNew(args[1])) {
								sender.sendMessage(args[1] + " has never played. Did you mistype?");
								return true;
							}
							if (plugin.isKing(u.getQuad(), args[1])) {
								sender.sendMessage("You can't promote the ruler!");
								return true;
							}
							if (plugin.isFavorLocked(args[1])) {
								sender.sendMessage(args[1] + " has been adjusted too recently. Please wait.");
								return true;
							}
							plugin.raiseFavor(u.getQuad(), args[1]);
							User target = plugin.getUser(args[1]);
							if (target != null) {
								target.sendMessage(String.format("You are now %s in %s.", plugin.rankOf(u.getQuad(), args[1]), ChatColor.AQUA + u.getQuad().longName() + ChatColor.RESET));
							}
							new NotifyTask(plugin, String.format("%s is now %s in your kingdom.", args[1], plugin.rankOf(u.getQuad(), args[1])), u.getQuad()).runTask(plugin);
							return true;
						}
						helpText(sender);
						return true;
					}
					sender.sendMessage(plugin.notPlayerString());
					return true;
				}
				sender.sendMessage(plugin.noAccessString());
				return true;
			case "demote":
				if (sender.hasPermission("kingdoms.player")) {
					if (u != null) {
						if (u.getRank() > 2) {
							if (args.length != 2) {
								sendCommandUsage(sender, "demote", "<user>");
								return true;
							}
							if (plugin.isNew(args[1])) {
								sender.sendMessage(args[1] + " has never played. Did you mistype?");
								return true;
							}
							if (plugin.isKing(u.getQuad(), args[1])) {
								sender.sendMessage("You can't demote the ruler!");
								return true;
							}
							if (plugin.isFavorLocked(args[1])) {
								sender.sendMessage(args[1] + " has been adjusted too recently. Please wait.");
								return true;
							}
							plugin.lowerFavor(u.getQuad(), args[1]);
							User target = plugin.getUser(args[1]);
							if (target != null) {
								target.sendMessage(String.format("You are now %s in %s.", plugin.rankOf(u.getQuad(), args[1]), ChatColor.AQUA + u.getQuad().longName() + ChatColor.RESET));
							}
							new NotifyTask(plugin, String.format("%s is now %s in your kingdom.", args[1], plugin.rankOf(u.getQuad(), args[1])), u.getQuad()).runTask(plugin);
							return true;
						}
						helpText(sender);
						return true;
					}
					sender.sendMessage(plugin.notPlayerString());
					return true;
				}
				sender.sendMessage(plugin.noAccessString());
				return true;
			// King-only commands
			case "upgrade":
				if (sender.hasPermission("kingdoms.player")) {
					if (u != null) {
						if (u.isKing(u.getQuad())) {
							if (args.length != 2) {
								sendCommandUsage(sender, "upgrade", "[nw|ne|sw|se]");
								return true;
							}
							Quadrant of = Quadrant.toQuadrant(args[1]);
							if (of == null) {
								sendCommandUsage(sender, "upgrade", "[nw|ne|sw|se]");
								return true;
							}
							if (u.getQuad() == of) {
								sender.sendMessage(selfStatus);
								return true;
							}
							if (plugin.isStatusLocked(u.getQuad())) {
								sender.sendMessage("Kingdom status was updated too recently. Please wait.");
								return true;
							}
							if (plugin.getStatus(u.getQuad(), of) == -2) {
								new NotifyAllTask(plugin, ChatColor.AQUA + u.getQuad().longName() + ChatColor.RESET + " has called off its war on " + ChatColor.AQUA + of.longName() + ChatColor.RESET + "!").runTask(plugin);
							}
							plugin.raiseStatus(u.getQuad(), of);
							new NotifyTask(plugin, "Your kingdom is now " + statusOf(u.getQuad(), of) + " with " + ChatColor.AQUA + of.longName() + ChatColor.RESET + ".", u.getQuad()).runTask(plugin);
							new NotifyTask(plugin, ChatColor.AQUA + u.getQuad().longName() + ChatColor.RESET + " is now " + statusOf(u.getQuad(), of) + " with your kingdom.", of).runTask(plugin);
							return true;
						}
						helpText(sender);
						return true;
					}
					sender.sendMessage(plugin.notPlayerString());
					return true;
				}
				sender.sendMessage(plugin.noAccessString());
				return true;
			case "degrade":
				if (sender.hasPermission("kingdoms.player")) {
					if (u != null) {
						if (u.isKing(u.getQuad())) {
							if (args.length != 2) {
								sendCommandUsage(sender, "degrade", "[nw|ne|sw|se]");
								return true;
							}
							Quadrant of = Quadrant.toQuadrant(args[1]);
							if (of == null) {
								sendCommandUsage(sender, "degrade", "[nw|ne|sw|se]");
								return true;
							}
							if (u.getQuad() == of) {
								sender.sendMessage(selfStatus);
								return true;
							}
							if (plugin.isStatusLocked(u.getQuad())) {
								sender.sendMessage("Kingdom status was updated too recently. Please wait.");
								return true;
							}
							plugin.lowerStatus(u.getQuad(), of);
							if (plugin.getStatus(u.getQuad(), of) == -2) {
								new NotifyAllTask(plugin, ChatColor.AQUA + u.getQuad().longName() + ChatColor.RESET + " has declared " + ChatColor.RED + "war" + ChatColor.RESET + " on " + ChatColor.AQUA + of.longName() + ChatColor.RESET + "!").runTask(plugin);
							} else {
								new NotifyTask(plugin, "Your kingdom is now " + statusOf(u.getQuad(), of) + " with " + ChatColor.AQUA + of.longName() + ChatColor.RESET + ".", u.getQuad()).runTask(plugin);
								new NotifyTask(plugin, ChatColor.AQUA + u.getQuad().longName() + ChatColor.RESET + " is now " + statusOf(u.getQuad(), of) + " with your kingdom.", of).runTask(plugin);
							}
							return true;
						}
						helpText(sender);
						return true;
					}
					sender.sendMessage(plugin.notPlayerString());
					return true;
				}
				sender.sendMessage(plugin.noAccessString());
				return true;
			case "name":
				if (sender.hasPermission("kingdoms.player")) {
					if (u != null) {
						if (u.isKing(u.getQuad())) {
							if (args.length < 2) {
								sendCommandUsage(sender, "name", "<name>");
								return true;
							}
							StringBuffer newName = new StringBuffer();
							newName.append(args[1]);
							if (args.length > 2) {
								for (int i = 2; i < args.length; i++) {
									newName.append(' ');
									newName.append(args[i]);
								}
							}
							plugin.setQuadName(u.getQuad(), newName.toString());
							sender.sendMessage("Your kingdom is named " + ChatColor.AQUA + newName.toString() + ChatColor.RESET +".");
							return true;
						}
						helpText(sender);
						return true;
					}
					sender.sendMessage(plugin.notPlayerString());
					return true;
				}
				sender.sendMessage(plugin.noAccessString());
				return true;
			default:
				helpText(sender);
				return true;
			}
		}
		return false;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		List<String> list = new LinkedList<String>();
		if (command.getName().equalsIgnoreCase("quad")) {
			switch(args.length) {
			case 0:
				return null;
			case 1:
				if (sender instanceof Player) {
					if (sender.hasPermission("kingdoms.player")) {
						User u = plugin.getUser(((Player)sender).getName());
						if (u.getQuad() != null) {
							list.add("list");
							list.add("status");
							if (u.getRank() > 2) {
								list.add("apps");
								list.add("deny");
								list.add("add");
								list.add("remove");
								list.add("promote");
								list.add("demote");
							}
							if (u.getRank() == 4) {
								list.add("upgrade");
								list.add("degrade");
								list.add("name");
							}
						} else {
							list.add("apply");
						}
					}
				}
				if (sender.hasPermission("kingdoms.admin")) {
					list.add("spawn");
					list.add("safe");
					list.add("badlands");
					list.add("assign");
					list.add("right");
					list.add("set");
					list.add("setpromote");
					list.add("setdemote");
					list.add("lore");
					list.add("iname");
					list.add("ench");
					list.add("nubspawn");
					list.add("embspawn");
					list.add("signenter");
					list.add("signexit");
				}
				break;
			case 2:
				if (sender instanceof Player) {
					if (sender.hasPermission("kingdoms.player")) {
						User u = plugin.getUser(((Player)sender).getName());
						if (u.getQuad() != null) {
							if (u.getRank() > 2) {
								if (args[0].equalsIgnoreCase("add")) return null;
								if (args[0].equalsIgnoreCase("deny")) return null;
								if (args[0].equalsIgnoreCase("remove")) return null;
								if (args[0].equalsIgnoreCase("promote")) return null;
								if (args[0].equalsIgnoreCase("demote")) return null;
							}
							if (u.getRank() == 4) {
								if (args[0].equalsIgnoreCase("upgrade") || args[0].equalsIgnoreCase("degrade")) {
									for (Quadrant q : Quadrant.values()) {
										if (u.getQuad() != q) list.add(q.toString().toLowerCase());
									}
								}
							}
						} else {
							if (args[0].equalsIgnoreCase("apply")) {
								for (Quadrant q : Quadrant.values()) {
									list.add(q.toString().toLowerCase());
								}
							}
						}
					}
				}
				if (sender.hasPermission("kingdoms.admin")) {
					if (args[0].equalsIgnoreCase("assign") || args[0].equalsIgnoreCase("right")) {
						for (Quadrant q : Quadrant.values()) {
							list.add(q.toString().toLowerCase());
						}
					}
					if (args[0].equalsIgnoreCase("set")) {
						for (Quadrant q : Quadrant.values()) {
							list.add(q.toString().toLowerCase());
						}
						list.add("clear");
					}
					if (args[0].equalsIgnoreCase("setpromote") || args[0].equalsIgnoreCase("setdemote")) return null;
					if (args[0].equalsIgnoreCase("ench")) {
						list.add("silk");
						list.add("kb");
						list.add("knockback");
						list.add("fa");
						list.add("fire");
						list.add("eff");
						list.add("efficiency");
						list.add("unb");
						list.add("unbreaking");
						list.add("loot");
						list.add("looting");
						list.add("fort");
						list.add("fortune");
						list.add("inf");
						list.add("infinity");
						list.add("pow");
						list.add("power");
						list.add("flame");
						list.add("punch");
						list.add("sharp");
						list.add("sharpness");
						list.add("boa");
						list.add("arth");
						list.add("bane");
						list.add("arthropods");
						list.add("smite");
						list.add("aqua");
						list.add("affinity");
						list.add("resp");
						list.add("respiration");
						list.add("prot");
						list.add("protection");
						list.add("projprot");
						list.add("fireprot");
						list.add("feather");
						list.add("ff");
						list.add("falling");
						list.add("blastprot");
						list.add("thorns");
					}
				}
				break;
			case 3:
				if (sender.hasPermission("kingdoms.admin")) {
					if (args[0].equalsIgnoreCase("assign") || args[0].equalsIgnoreCase("set")) return null;
					if (args[0].equalsIgnoreCase("right")) {
						list.add("cow");
						list.add("chicken");
						list.add("sheep");
						list.add("pig");
						list.add("horse");
						list.add("sugar");
						list.add("melon");
						list.add("pumpkin");
						list.add("wheat");
						list.add("carrot");
						list.add("potato");
						list.add("wart");
						list.add("clay");
						list.add("fish");
						list.add("potion");
						list.add("wither");
					}
				}
				break;
			}
		}
		String arg = args[args.length - 1].toLowerCase(); 
		if (!arg.isEmpty()) {
			Iterator<String> it = list.iterator();
			while (it.hasNext()) {
				String st = it.next();
				if (!st.startsWith(arg)) {
					it.remove();
				}
			}
		}
		return list;
	}

}
