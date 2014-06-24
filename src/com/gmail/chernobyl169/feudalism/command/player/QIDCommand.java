package com.gmail.chernobyl169.feudalism.command.player;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.gmail.chernobyl169.feudalism.FeudalismPlugin;
import com.gmail.chernobyl169.feudalism.locking.ShopItem;

public class QIDCommand implements CommandExecutor {

	private FeudalismPlugin plugin;
	
	public QIDCommand(FeudalismPlugin plugin) { this.plugin = plugin; }
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("qid")) {
			if (!(sender instanceof Player)) {
				sender.sendMessage(plugin.notPlayerString());
				return true;
			}
			Player player = (Player)sender;
			ItemStack is = player.getItemInHand();
			if (is == null || is.getType() == Material.AIR) {
				sender.sendMessage(ChatColor.RED + "No item in hand." + ChatColor.RESET);
				return true;
			}
			ShopItem si = ShopItem.getByItem(is);
			if (si == null) {
				sender.sendMessage(ChatColor.RED + "Can't sell this in a shop." + ChatColor.RESET);
				return true;
			}
			sender.sendMessage("Shops call this " + ChatColor.YELLOW + si.shopName() + ChatColor.RESET + ".");
			return true;
		}
		return false;
	}

}
