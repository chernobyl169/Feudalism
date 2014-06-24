package com.gmail.chernobyl169.feudalism.command.donor;

import java.util.Random;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gmail.chernobyl169.feudalism.FeudalismPlugin;
import com.gmail.chernobyl169.feudalism.User;
import com.gmail.chernobyl169.feudalism.tasks.NotifyAllTask;

public class QAFKCommand implements CommandExecutor {

	private FeudalismPlugin plugin;
	private final String[] strings = {
			"Be vewy vewy quiet. %s is AFK.",
			"%s made a decision of questionable soundness: AFK.",
			"%s would remind you that it stands for Away From Keyboard.",
			"%s is currently AFK. Please leave a message after the beep.",
			"%s is now AFK.",
			"%s is pretending to be AFK.",
			"What happens when you AFK? %s will find out upon return.",
			"%s is mysteriously AFK.",
			"Perhaps %s doesn't know that AFK won't prevent death.",
			"%s is suddenly AFK!",
			"%s uses AFK! It's super effective!",
			"Whatever %s does AFK, stays AFK.",
			"%s must really need a coffee break, to go AFK right now.",
			"Don't mind %s, they're just AFK for a moment.",
			"Dr. Coffee is not responsible for %s's death while AFK.",
			"%s goes AFK in a puff of smoke!",
			"%s should be back from AFK shortly. (Should.)",
			"Something something %s something something something AFK.",
			"AFK could be the leading cause of death for %s.",
			"%s could be mining diamonds, but instead is AFK.",
			"%s found something more important than this game, and is AFK."
	};
	private final Random random = new Random();
	
	public QAFKCommand(FeudalismPlugin plugin) { this.plugin = plugin; }
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("qafk")) {
			if (!(sender instanceof Player)) {
				sender.sendMessage(plugin.notPlayerString());
				return true;
			}
			if (!sender.hasPermission("kingdoms.donor")) {
				sender.sendMessage(plugin.noAccessString());
				return true;
			}
			User user = plugin.getUser(sender.getName());
			String message = String.format("%s was kicked for abusing AFK.", user.getName());
			if (user.afkBoot()) {
				((Player)sender).kickPlayer("AFK abuse");
				return true;
			} else {
				user.afk();
				message = String.format(strings[random.nextInt(strings.length)], user.getName());
			}
			new NotifyAllTask(plugin, message).runTask(plugin);
			return true;
		}
		return false;
	}

}
