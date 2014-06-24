package com.gmail.chernobyl169.feudalism.listener;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import com.gmail.chernobyl169.feudalism.FeudalismPlugin;
import com.gmail.chernobyl169.feudalism.Quadrant;
import com.gmail.chernobyl169.feudalism.User;

public class ChatListener implements Listener {

	private final String[] helpCommands = {
		"/?", "/help"
	};
	
	private final String[] versionCommands = {
		 "/ver", "/about", "/pl"
	};
	
	private final String [] overrideTells = {
		"/tell ", "/msg ", "/w "
	};
	
	private FeudalismPlugin plugin;
	
	public ChatListener(FeudalismPlugin plugin) {
		this.plugin = plugin;
	}
	
	private ChatColor colorOfFavor(User user) {
		int favor = user.getRank();
		switch (favor) {
		case 0:
			return ChatColor.BLUE;
		case 1:
			return ChatColor.GREEN;
		case 2:
			return ChatColor.YELLOW;
		case 3:
			return ChatColor.GOLD;
		case 4:
			return ChatColor.AQUA;
		default:
			return ChatColor.RESET;
		}
	}
	
	@EventHandler(ignoreCancelled=true)
	public void onCommandPreprocess(PlayerCommandPreprocessEvent event) {
		String clc = event.getMessage().toLowerCase();
		if (event.getPlayer() != null) {
			for (String c : versionCommands) {
				if (clc.startsWith(c)) {
					event.setMessage("/qhelp version");
					return;
				}
			}
			for (String c : helpCommands) {
				if (clc.startsWith(c)) {
					event.setMessage("/qhelp" + clc.substring(c.length()));
					return;
				}
			}
			for (String c : overrideTells) {
				if (clc.startsWith(c)) {
					event.setMessage("/t " + clc.substring(c.length()));
					return;
				}
			}
			if (clc.startsWith("/me ") && plugin.getUser(event.getPlayer().getName()).isMuted()) {
				event.setCancelled(true);
				event.getPlayer().sendMessage(ChatColor.RED + "You've been muted." + ChatColor.RESET);
			}
		}
	}

	@EventHandler(ignoreCancelled=true)
	public void onChat(AsyncPlayerChatEvent event) {
		final Player player = event.getPlayer();
		Set<Player> list = event.getRecipients();
		try { list.add(player); } catch (UnsupportedOperationException e) { return; }
		final User user = plugin.getUser(player.getName());
		final Quadrant q = user.getQuad();
		if (q == null) event.setFormat("[--] %s: %s");
		final String message = event.getMessage();
		if (message.equals("connected with an iPhone using MineChat")) {
			event.setMessage("I'm just asking for trouble. I'm using MineChat!");
		}
		char c = message.charAt(0);
		switch (c) {
		case '!':
			if (q != null) {
				if (message.equals("!")) {
					event.setCancelled(true);
					Iterator<User> it = plugin.getUsers().iterator();
					List<String> names = new LinkedList<String>();
					while (it.hasNext()) {
						final User u = it.next();
						if (u.getQuad() == q) names.add(colorOfFavor(u) + u.getName() + ChatColor.RESET);
					}
					player.sendMessage("Online kingdom members:");
					StringBuffer sb = new StringBuffer();
					for (String n : names) { sb.append (n + " "); }
					player.sendMessage(sb.toString());
					return;
				}
				event.setMessage(message.substring(1));
				Iterator<Player> it = list.iterator();
				while (it.hasNext()) {
					final Player p = it.next();
					final User u = plugin.getUser(p.getName());
					if (u == null || u.getQuad() != q) it.remove();
				}
				event.setFormat(ChatColor.LIGHT_PURPLE + "(" + q.toString().toUpperCase() + ") " + colorOfFavor(user) + "%s" + ChatColor.RESET + ": %s");
			}
			break;
		case '@':
			if (message.equals("@")) {
				event.setCancelled(true);
				Iterator<User> i = plugin.getUsers().iterator();
				List<User> kings = new LinkedList<User>();
				while (i.hasNext()) {
					final User k = i.next();
					if (k.getRank() == 4) kings.add(k);
				}
				if (kings.size() == 0) {
					player.sendMessage("No rulers online.");
					return;
				}
				player.sendMessage("Online kingdom rulers:");
				for (User ku : kings) { player.sendMessage(" " + ChatColor.DARK_GREEN + ku.getName() + ChatColor.GOLD + " [" + ku.getQuad().toString().toUpperCase() + "]" + ChatColor.RESET); }
				return;
			}
			if (q == null) break;
			if (user.getRank() != 4) {
				event.setFormat(colorOfFavor(user) + "[" + q.toString().toUpperCase() + "] " + ChatColor.RESET + "%s: %s");
				return;
			}
			event.setMessage(message.substring(1));
			Iterator<Player> i = list.iterator();
			while (i.hasNext()) {
				final Player p = i.next();
				final User u = plugin.getUser(p.getName());
				if (u == null || u.getRank() != 4) i.remove();
			}
			event.setFormat(ChatColor.DARK_RED + "[" + q.toString().toUpperCase() + "] " + ChatColor.DARK_GREEN + "%s" + ChatColor.RESET + ": %s");
			break;
		default:
			if (q == null) break;
			if (user.getRank() == 4) {
				event.setFormat(colorOfFavor(user) + "[" + q.toString().toUpperCase() + "] "+ ChatColor.DARK_GREEN + "%s" + ChatColor.RESET + ": %s");
			} else {
				if (player.hasPermission("kingdoms.donor")) {
					event.setFormat(colorOfFavor(user) + "[" + q.toString().toUpperCase() + "] "+ ChatColor.YELLOW + "%s" + ChatColor.RESET + ": %s");					
				} else {
					event.setFormat(colorOfFavor(user) + "[" + q.toString().toUpperCase() + "] " + ChatColor.RESET + "%s: %s");
				}
			}
			break;
		}
		if (user.isMuted()) {
			event.setCancelled(true);
			player.sendMessage(ChatColor.RED + "You've been muted." + ChatColor.RESET);
		}
	}
}
