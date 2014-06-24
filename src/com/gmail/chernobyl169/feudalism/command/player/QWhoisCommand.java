package com.gmail.chernobyl169.feudalism.command.player;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.chernobyl169.feudalism.FeudalismPlugin;
import com.gmail.chernobyl169.feudalism.Quadrant;
import com.gmail.chernobyl169.feudalism.User;

public class QWhoisCommand implements CommandExecutor {

	private FeudalismPlugin plugin;

	public QWhoisCommand(FeudalismPlugin plugin) { this.plugin = plugin; }
	
	private void helpText(CommandSender sender) {
		sender.sendMessage("Usage: " + ChatColor.GOLD + "/whois <user>" + ChatColor.RESET);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("qwhois")) {
			if (sender.hasPermission("kingdoms.player")) {
				User u = null;
				if (sender instanceof Player) u = plugin.getUser(((Player)sender).getName());
				if (u == null) { 
					sender.sendMessage(plugin.noAccessString());
					return true;
				}
				if (args.length != 1) {
					helpText(sender);
					return true;
				}
				if (plugin.isNew(args[0])) {
					sender.sendMessage(ChatColor.RED + "Player not found!" + ChatColor.RESET);
					return true;
				}
				String start = u.getName().equalsIgnoreCase(args[0]) ? "You are" : args[0] + " is";
				for (Quadrant q : Quadrant.values()) {
					sender.sendMessage(start + " currently " + plugin.rankOf(q, args[0]) + " in " + ChatColor.AQUA + q.longName() + ChatColor.GOLD + "[" + q.toString().toUpperCase() + "]" + ChatColor.RESET + ".");
				}
				return true;					
			}
			sender.sendMessage(plugin.noAccessString());
			return true;
		}
		return false;
	}
}
