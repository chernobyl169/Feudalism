package com.gmail.chernobyl169.feudalism.command.ruler;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import com.gmail.chernobyl169.feudalism.FeudalismPlugin;
import com.gmail.chernobyl169.feudalism.User;

public class QTPCommand implements CommandExecutor {

	private FeudalismPlugin plugin;
	
	public QTPCommand(FeudalismPlugin plugin) { this.plugin = plugin; }
	
	private void helpText(CommandSender sender) {
		sender.sendMessage("Usage: " + ChatColor.GOLD + "/qtp <player>" + ChatColor.RESET);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("qtp")) {
			if (!(sender instanceof Player)) {
				sender.sendMessage(plugin.notPlayerString());
				return true;
			}
			if (!sender.hasPermission("kingdoms.ruler")) {
				sender.sendMessage(plugin.noPermissionString());
				return true;
			}
			if (args.length != 1) {
				helpText(sender);
				return true;
			}
			Set<User> users = new HashSet<User>();
			Iterator<User> it = plugin.getUsers().iterator();
			while (it.hasNext()) {
				User u = it.next();
				if (u.getName().toLowerCase().contains(args[0].toLowerCase())) { users.add(u); }
			}
			if (users.size() == 0) {
				sender.sendMessage(plugin.noPlayerMatchString());
				return true;
			}
			if (users.size() != 1) {
				sender.sendMessage(plugin.ambiguousPlayerMatchString());
				return true;
			}
			User target = users.iterator().next(), source = plugin.getUser(sender.getName());
			if (target == source) {
				sender.sendMessage(ChatColor.DARK_RED + "Can't teleport to yourself!" + ChatColor.RESET);
				return true;
			}
			Player player = (Player)sender;
			source.setBackTarget(player.getLocation());
			player.teleport(plugin.getServer().getPlayerExact(target.getName()), TeleportCause.PLUGIN);
			return true;
		}
		return false;
	}

}
