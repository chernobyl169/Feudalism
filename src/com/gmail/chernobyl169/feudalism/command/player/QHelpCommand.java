package com.gmail.chernobyl169.feudalism.command.player;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import com.gmail.chernobyl169.feudalism.FeudalismPlugin;

public class QHelpCommand implements CommandExecutor {

	private FeudalismPlugin plugin;

	private String version;
	
	public QHelpCommand(FeudalismPlugin plugin) {
		this.plugin = plugin;
		version = String.format("%s --[%s%s %s%s by %schernobyl169%s]--%s",
			ChatColor.GREEN, ChatColor.WHITE, plugin.getDescription().getName(),
			plugin.getDescription().getVersion(), ChatColor.GREEN,
			ChatColor.AQUA, ChatColor.GREEN, ChatColor.RESET);
		help[0][0] = version;
		help[3][2] = String.format("start %d blocks from spawn in the four cardinal directions", plugin.getSafeRadius());
		help[3][3] = String.format("and are %d blocks wide. In these areas, " + ChatColor.RED + "PvP" + ChatColor.RESET + " is enabled", plugin.getBadlandsRadius() * 2);
	}
	
	private String[] help[] = {
			{	"",
				"Try " + ChatColor.GOLD + "/help <number/topic>" + ChatColor.RESET + ".",
				ChatColor.GOLD + "Help Topics" + ChatColor.RESET,
				"1) rules - 2) basics - 3) kingdoms",
				"4) ranks - 5) chat - 6) commands",
				"7) signs - 8) locks - 9) shops - 10) trades",
			},
			{	ChatColor.GOLD + "Help: Rules" + ChatColor.RESET,
				"Rule #1: " + ChatColor.AQUA + "Respect." + ChatColor.RESET,
				"Rule #2: No hacking, no cheating, no x-ray.",
				"Rule #3: No griefing your own kingdom.",
				"Rule #4: Your ruler's word is final."
			},
			{	ChatColor.GOLD + "Help: Basics" + ChatColor.RESET,
				"The overworld is split into four " + ChatColor.AQUA + "Kingdoms" + ChatColor.RESET + ".",
				"Each kingdom has certain " + ChatColor.AQUA + "rights" + ChatColor.RESET + " to farming actions.",
				"Only " + ChatColor.AQUA + "members of a kingdom" + ChatColor.RESET + " can build there, or",
				"breed and plant the things that the kingdom has rights to.",
				"There are also " + ChatColor.AQUA + "badlands" + ChatColor.RESET + " between the kingdoms.",
				"You can " + ChatColor.AQUA + "apply to join" + ChatColor.RESET + " a kingdom with " + ChatColor.GOLD + "/q apply" + ChatColor.RESET + ", or",
				"live in the badlands under no ruler."
			},
			{	ChatColor.GOLD + "Help: Kingdoms" + ChatColor.RESET,
				"The four kingdoms split the world into quadrants. The badlands",
				"",
				"",
				"for all players, farming is blocked, and building is allowed.",
				"All players have a " + ChatColor.AQUA + "rank" + ChatColor.RESET + " with each kingdom. Members",
				"have a positive rank, and others can have a negative rank.",
				"The kingdoms all have a " + ChatColor.AQUA + "status" + ChatColor.RESET + " with each other.",
				"These represent friendship or hostility between kingdoms."
			},
			{	ChatColor.GOLD + "Help: Ranks" + ChatColor.RESET,
				ChatColor.AQUA + "The Ruler" + ChatColor.RESET + " has the final say, and moderates the server.",
				ChatColor.GOLD + "Dignified" + ChatColor.RESET + " members can manage player ranks.",
				ChatColor.YELLOW + "Trusted" + ChatColor.RESET + " members may place lava and TNT, and use villagers.",
				ChatColor.GREEN + "Respected" + ChatColor.RESET + " members have basic build access to the kingdom.",
				ChatColor.BLUE + "Welcome" + ChatColor.RESET + " players are free to visit, but cannot build.",
				ChatColor.DARK_RED + "Unwelcome" + ChatColor.RESET + " players cannot open doors or use beds.",
				ChatColor.DARK_RED + "Hated" + ChatColor.RESET + " players will be PvP enabled in that kingdom.",
				ChatColor.RED + "Wanted" + ChatColor.RESET + " players can be hunted for their head."
			},
			{	ChatColor.GOLD + "Help: Chat" + ChatColor.RESET,
				"Regular chat is seen by all players. Your membership is",
				"listed next to your name in chat, and the color indicates",
				"your current rank. You can view a list of all online",
				"members with '" + ChatColor.GOLD + "!" + ChatColor.RESET + "', and any chat starting with '" + ChatColor.GOLD + "!" + ChatColor.RESET + "' will only",
				"be visible to other kingdom members. Kingdom chat has a",
				"purple quadrant and rank-colored names.",
				"You can also see a list of all online rulers with '" + ChatColor.GOLD + "@" + ChatColor.RESET + "'.",
				"Use " + ChatColor.GOLD + "/tell" + ChatColor.RESET + " and " + ChatColor.GOLD + "/reply" + ChatColor.RESET + " to send private messages."
			},
			{	ChatColor.GOLD + "Help: Commands" + ChatColor.RESET,
				"Try " + ChatColor.GOLD + "/help <command>" + ChatColor.RESET + ".",
				"tell - reply - who - whois",
				"zone - quad - id"
			},
			{	ChatColor.GOLD + "Help: Signs" + ChatColor.RESET,
				"Wall signs have special functions to help secure goods.",
				"A sign starting with " + ChatColor.AQUA + "[Lock]" + ChatColor.RESET + " secures blocks and items.",
				"A sign starting with " + ChatColor.AQUA + "[Shop]" + ChatColor.RESET + " sells goods for gold or diamond.",
				"A sign starting with " + ChatColor.AQUA + "[Bounty]" + ChatColor.RESET + " buys goods from players.",
				"A sign starting with " + ChatColor.AQUA + "[Trade]" + ChatColor.RESET + " creates a secure trade chest.",
				"If you place a special sign, you will know that it worked",
				"when your name appears on the last line of the sign."
			},
			{	ChatColor.GOLD + "Help: Locks" + ChatColor.RESET,
				"Locking a block secures it from use or breakage.",
				"Place a wall sign, and put " + ChatColor.AQUA + "[Lock]" + ChatColor.RESET + " for the first",
				"line of text. The block the sign hangs from will be",
				"secured. A lock also secures doors from above, and",
				"buttons or levers from above and below.",
				"You can also allow kingdom members to pass a lock by",
				"putting a rank on the second line, e.g. " + ChatColor.AQUA + "Trusted" + ChatColor.RESET + "."
			},
			{	ChatColor.GOLD + "Help: Shops" + ChatColor.RESET,
				"A wall sign above a chest can create a " + ChatColor.AQUA + "[Shop]" + ChatColor.RESET + " or " + ChatColor.AQUA + "[Bounty]" + ChatColor.RESET + ".",
				"Punch the sign to see the shop's current status. You can",
				"trade by holding the item the shop accepts, then right-",
				"clicking the shop sign. Prices are in gold or diamond:",
				ChatColor.GOLD + "n" + ChatColor.RESET + " = " + ChatColor.AQUA + "gold nugget" + ChatColor.RESET + "; " + ChatColor.GOLD + "g" + ChatColor.RESET + " = " + ChatColor.AQUA + "gold ingot" + ChatColor.RESET + "; " + ChatColor.GOLD + "G" + ChatColor.RESET + " = " + ChatColor.AQUA + "gold block" + ChatColor.RESET + ";",
				ChatColor.GOLD + "d" + ChatColor.RESET + " = " + ChatColor.AQUA + "diamond" + ChatColor.RESET + "; " + ChatColor.GOLD + "D" + ChatColor.RESET + " = " + ChatColor.AQUA + "diamond block" + ChatColor.RESET
			},
			{	ChatColor.GOLD + "Help: Trade" + ChatColor.RESET,
				"A sign stuck to a chest that starts with " + ChatColor.AQUA + "[Trade]" + ChatColor.RESET + " makes",
				"a chest one other player can access. A player can hit",
				"the trade sign to claim or release the trade, and you",
				"can pre-load a player's name on the third line."
			},
			{	"Usage: " + ChatColor.GOLD + "/t <user> <message>" + ChatColor.RESET,
				"Send a private message."
			},
			{	"Usage: " + ChatColor.GOLD + "/r <user> <message>" + ChatColor.RESET,
				"Reply to a private message."
			},
			{	"Usage: " + ChatColor.GOLD + "/who <user>" + ChatColor.RESET,
				"See a player's membership."
			},
			{	"Usage: " + ChatColor.GOLD + "/whois <user>" + ChatColor.RESET,
				"See a player's ranks."
			},
			{	"Usage: " + ChatColor.GOLD + "/z" + ChatColor.RESET,
				"See your current location."
			},
			{	"Usage: " + ChatColor.GOLD + "/q {command}" + ChatColor.RESET,
				"Kingdom commands. Try just " + ChatColor.GOLD + "/q" + ChatColor.RESET + "."
			},
			{	"Usage: " + ChatColor.GOLD + "/id" + ChatColor.RESET,
				"See the shop name of the item you're holding."
			},
			{	ChatColor.BLUE + ChatColor.UNDERLINE.toString() + "http://map.dr-coffee.net" + ChatColor.RESET
			},
			{	ChatColor.RED + "Teamspeak offline, sorry!" + ChatColor.RESET
			},
			{	ChatColor.BLUE + ChatColor.UNDERLINE.toString() + "http://pmc.la/RgxPr" + ChatColor.RESET
			},
			{	ChatColor.BLUE + ChatColor.UNDERLINE.toString() + "http://drcoffee.reddit.com" + ChatColor.RESET
			},
			{	ChatColor.GOLD + "Help: Donors" + ChatColor.RESET,
				"Donors get a few minor perks. You can donate with",
				ChatColor.GOLD + "/buy" + ChatColor.RESET + " or visit" + ChatColor.BLUE + ChatColor.UNDERLINE.toString() + "http://dr-coffee.buycraft.net" + ChatColor.RESET,
				"to support the server. Donors will have access to",
				"extra commands, and appear yellow in public chat."
			}
	};
	
	private String[] donorHelp[] = {
			{	"Usage: " + ChatColor.GOLD + "/afk" + ChatColor.RESET,
				"Display a silly AFK message. You can be kicked",
				"for using it too frequently."
			},
			{	"Usage: " + ChatColor.GOLD + "/home" + ChatColor.RESET,
				"Teleport to your bed spawn. You can only use",
				"this if you are at full life and hunger, when",
				"you are in the overworld. Teleportation will",
				"drain you of all XP."
			},
			{	"Usage: " + ChatColor.GOLD + "/sethome" + ChatColor.RESET,
				"Set your bed spawn to your current location.",
				"You can only use this if you are at full life",
				"and hunger, when you are in the overworld.",
				"Setting your spawn location costs 15 levels."
			}
	};
	private void helpText(CommandSender sender, String page) {
		int p = 0;
		switch (page) {
		case "version":
			sender.sendMessage(version);
			return;
		case "rules":
		case "1":
			p = 1;
			break;
		case "basics":
		case "2":
			p = 2;
			break;
		case "kingdoms":
		case "3":
			p = 3;
			break;
		case "ranks":
		case "4":
			p = 4;
			break;
		case "chat":
		case "5":
			p = 5;
			break;
		case "commands":
		case "6":
			p = 6;
			break;
		case "signs":
		case "7":
			p = 7;
			break;
		case "locks":
		case "8":
			p = 8;
			break;
		case "shops":
		case "9":
			p = 9;
			break;
		case "trades":
		case "10":
			p = 10;
			break;
		case "tell":
		case "t":
			p = 11;
			break;
		case "reply":
		case "r":
			p = 12;
			break;
		case "who":
			p = 13;
			break;
		case "whois":
			p = 14;
			break;
		case "zone":
		case "z":
			p = 15;
			break;
		case "quad":
		case "q":
			p = 16;
			break;
		case "id":
			p = 17;
			break;
		case "map":
			p = 18;
			break;
		case "ts3":
			p = 19;
			break;
		case "vote":
			p = 20;
			break;
		case "reddit":
			p = 21;
			break;
		case "donors":
			p = 22;
			break;
		case "afk":
			p = 100;
			break;
		case "home":
			p = 101;
			break;
		case "sethome":
			p = 102;
			break;
		default:
			break;
		}
		boolean donor = sender.hasPermission("kingdoms.donor");
		if (p > 99) {
			if (donor) {
				for (String s : donorHelp[p - 100]) {
					sender.sendMessage(s);
				}
			} else {
				for (String s : help[0]) {
					sender.sendMessage(s);
				}
				p = 0;
			}
		} else { 
			for (String s : help[p]) {
				sender.sendMessage(s);
			}
		}
		if (donor) {
			if (p == 0)
				sender.sendMessage("map - ts3 - vote - reddit");
			if (p == 6) {
				sender.sendMessage("afk - home - sethome");
			}
		} else {
			if (p == 0)
				sender.sendMessage("donors - map - ts3 - vote - reddit");
		}
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("qhelp")) {
			if (sender.hasPermission("kingdoms.player")) {
				String page = "";
				if (args.length > 0) {
					page = args[0].toLowerCase();
				}
				helpText(sender, page);
				return true;
			}
			sender.sendMessage(plugin.noAccessString());
			return true;
		}
		return false;
	}

}
