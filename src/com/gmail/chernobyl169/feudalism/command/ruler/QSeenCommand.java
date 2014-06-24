package com.gmail.chernobyl169.feudalism.command.ruler;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.chernobyl169.feudalism.FeudalismPlugin;

public class QSeenCommand implements CommandExecutor {

	private FeudalismPlugin plugin;
	
	public QSeenCommand(FeudalismPlugin plugin) { this.plugin = plugin; }
	
	private void helpText(CommandSender sender) {
		sender.sendMessage("Usage: " + ChatColor.GOLD + "/seen <player>" + ChatColor.RESET);
	}
	
	private String timeFormat(long rawTime) {
		rawTime /= 1000;
		long seconds = rawTime % 60;
		rawTime -= seconds;
		rawTime /= 60;
		long minutes = rawTime % 60;
		rawTime -= minutes;
		rawTime /= 60;
		long hours = rawTime % 24;
		rawTime -= hours;
		rawTime /= 24;
		long days = rawTime;
		
		StringBuffer buf = new StringBuffer();
		if (days > 0) { buf.append(days + "d "); }
		if (hours > 0) { buf.append(hours + "h "); }
		if (minutes > 0) { buf.append(minutes + "m "); }
		if (seconds > 0) { buf.append(seconds + "s"); }
		
		return buf.toString();
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("qseen")) {
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
			String ip = plugin.getIP(args[0]);
			if (ip == null) {
				sender.sendMessage(ChatColor.DARK_RED + "User not found!" + ChatColor.RESET);
				return true;
			}
			sender.sendMessage("User information for " + ChatColor.GOLD + args[0] + ChatColor.RESET + ":");
			sender.sendMessage("Login IP: " + ChatColor.AQUA + ip + ChatColor.RESET);
			if (plugin.getUser(args[0]) == null) {
				sender.sendMessage("Last login: " + ChatColor.AQUA + timeFormat(plugin.getSeen(args[0])) + ChatColor.RESET + " ago");
			} else {
				sender.sendMessage(ChatColor.AQUA + "Online now!" + ChatColor.RESET);
			}
			return true;
		}
		return false;
	}

}
