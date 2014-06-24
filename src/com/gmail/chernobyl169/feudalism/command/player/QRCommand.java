package com.gmail.chernobyl169.feudalism.command.player;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.chernobyl169.feudalism.FeudalismPlugin;
import com.gmail.chernobyl169.feudalism.User;

public class QRCommand implements CommandExecutor {

	private FeudalismPlugin plugin;
	
	public QRCommand(FeudalismPlugin plugin) { this.plugin = plugin; }
	
	private void helpText(CommandSender sender) {
		sender.sendMessage("Usage: " + ChatColor.GOLD + "/r <message>" + ChatColor.RESET);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("qr")) {
			if (!(sender instanceof Player)) {
				sender.sendMessage(plugin.notPlayerString());
				return true;
			}
			if (args.length == 0) {
				helpText(sender);
				return true;
			}
			User user = plugin.getUser(sender.getName());
			User target = user.getReplyTarget();
			if (target == null) {
				sender.sendMessage("You have nobody to reply to.");
				return true;
			}
			StringBuffer message = new StringBuffer();
			message.append(args[0]);
			if (args.length > 1) {
				for (int i = 1; i < args.length; i++) {
					message.append(' ');
					message.append(args[i]);
				}
			}
//			Set<User> spies = new HashSet<User>();
//			for (User u : plugin.getUsers()) {
//				if (u.getRank() == 4) { spies.add(u); }
//			}
			target.sendMessage(ChatColor.GOLD + "[ <- " + user.getName() + "] " + ChatColor.RESET + message);
			target.setReplyTarget(user);
			user.sendMessage(ChatColor.GOLD + "[ -> " + target.getName() + "] " + ChatColor.RESET + message);
//			Iterator<User> it = spies.iterator();
//			while (it.hasNext()) {
//				User u = it.next();
//				if (u != user) { u.sendMessage(ChatColor.GRAY + "[" + user.getName() + "] /r " + message + ChatColor.RESET); }
//			}
			return true;
		}
		return false;
	}

}
