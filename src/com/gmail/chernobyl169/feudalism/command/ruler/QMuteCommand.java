package com.gmail.chernobyl169.feudalism.command.ruler;

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

public class QMuteCommand implements CommandExecutor {

	private FeudalismPlugin plugin;
	
	public QMuteCommand(FeudalismPlugin plugin) { this.plugin = plugin; }
	
	private void helpText(CommandSender sender) {
		sender.sendMessage("Usage: " + ChatColor.GOLD + "/mute <player>" + ChatColor.RESET);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("qmute")) {
			if (!(sender instanceof Player)) {
				sender.sendMessage(plugin.notPlayerString());
				return true;
			}
			if (!sender.hasPermission("kingdoms.ruler")) {
				sender.sendMessage(plugin.noAccessString());
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
			User target = users.iterator().next();
			if (target.getRank() == 4) {
				sender.sendMessage(ChatColor.RED + "You can't mute a ruler!" + ChatColor.RESET);
				return true;
			}
			target.mute();
			if (target.isMuted()) {
				sender.sendMessage("Muted " + target.getName() + " for five minutes.");
				target.sendMessage(ChatColor.RED + "You've been muted for five minutes." + ChatColor.RESET);
			} else {
				sender.sendMessage("Unmuted " + target.getName() + ".");
				target.sendMessage("You've been unmuted.");
			}
			return true;
		}
		return false;
	}

}
