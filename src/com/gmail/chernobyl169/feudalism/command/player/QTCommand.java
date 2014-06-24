package com.gmail.chernobyl169.feudalism.command.player;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.chernobyl169.feudalism.FeudalismPlugin;
import com.gmail.chernobyl169.feudalism.User;

public class QTCommand implements CommandExecutor {

	private FeudalismPlugin plugin;
	
	public QTCommand(FeudalismPlugin plugin) { this.plugin = plugin; }
	
	private void helpText(CommandSender sender) {
		sender.sendMessage("Usage: " + ChatColor.GOLD + "/t <user> <message>" + ChatColor.RESET);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("qt")) {
			if (!(sender instanceof Player)) {
				sender.sendMessage(plugin.notPlayerString());
				return true;
			}
			if (args.length < 2) {
				helpText(sender);
				return true;
			}
			Set<User> users = new HashSet<User>();
//			Set<User> spies = new HashSet<User>();
			for (User u : plugin.getUsers()) {
				if (u.getName().toLowerCase().contains(args[0].toLowerCase())) { users.add(u); }
//				if (u.getRank() == 4) { spies.add(u); }
			}
			if (users.size() == 0) {
				sender.sendMessage(plugin.noPlayerMatchString());
				return true;
			}
			Iterator<User> it = users.iterator();
			StringBuffer message = new StringBuffer();
			message.append(args[1]);
			if (args.length > 2) {
				for (int i = 2; i < args.length; i++) {
					message.append(' ');
					message.append(args[i]);
				}
			}				
			while (it.hasNext()) {
				User u = it.next();
				u.sendMessage(ChatColor.GOLD + "[ <- " + sender.getName() + "] " + ChatColor.RESET + message);
				u.setReplyTarget(plugin.getUser(sender.getName()));
				sender.sendMessage(ChatColor.GOLD + "[ -> " + u.getName() + "] " + ChatColor.RESET + message);
			}
//			User user = plugin.getUser(sender.getName()); 
//			it = spies.iterator();
//			while (it.hasNext()) {
//				User u = it.next();
//				if (user != u) { u.sendMessage(ChatColor.GRAY + "[" + sender.getName() + "] /t " + args[0] + " " + message + ChatColor.RESET); }
//			}
			return true;
		}
		return false;
	}

}
