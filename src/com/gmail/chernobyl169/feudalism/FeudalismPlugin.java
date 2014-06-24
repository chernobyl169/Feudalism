package com.gmail.chernobyl169.feudalism;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import com.gmail.chernobyl169.feudalism.command.*;
import com.gmail.chernobyl169.feudalism.command.donor.*;
import com.gmail.chernobyl169.feudalism.command.player.*;
import com.gmail.chernobyl169.feudalism.command.ruler.*;
import com.gmail.chernobyl169.feudalism.listener.*;
import com.gmail.chernobyl169.feudalism.tasks.FavorUnlockTask;
import com.gmail.chernobyl169.feudalism.tasks.StatusUnlockTask;
import com.gmail.chernobyl169.feudalism.tasks.GriefAlertNotifyTask;
import com.gmail.chernobyl169.feudalism.tasks.GriefAlertTimeoutTask;

public class FeudalismPlugin extends JavaPlugin {
	
	private World world;

	private int spawnRadius, safeRadius, badlandsRadius;
	private Map<Quadrant, String> kings;
	private Map<String, User> users;
	private Set<String> favorLock;
	private Set<Quadrant> statusLock;
	private Set<Quadrant> griefAlert;

	private File memberFile, favorFile, logDataFile;
	private YamlConfiguration memberConfig, favorConfig, logDataConfig;
	
	private Rights rights;
	
	private final String memberFilename = "members.yml";
	private final String favorFilename = "favor.yml";
	private final String logDataFilename = "logs.yml";

	private final String[] ranks = {
			ChatColor.RED + "wanted" + ChatColor.RESET,
			ChatColor.DARK_RED + "hated" + ChatColor.RESET,
			ChatColor.DARK_RED + "unwelcome" + ChatColor.RESET,
			ChatColor.BLUE + "welcome" + ChatColor.RESET,
			ChatColor.GREEN + "respected" + ChatColor.RESET,
			ChatColor.YELLOW + "trusted" + ChatColor.RESET,
			ChatColor.GOLD + "dignified" + ChatColor.RESET,
			ChatColor.AQUA + "the ruler" + ChatColor.RESET
		};

	public String rankOf(Quadrant q, String name) {
		return ranks[isMember(q, name) ? (getFavor(q, name) + 3) : (3 - getFavor(q, name))];
	}
	
	public void logInfo(String string) { getLogger().info(string); }
	
	public String ambiguousPlayerMatchString() { return ChatColor.DARK_RED + "Matched more than one player." + ChatColor.RESET; }
	public String noPlayerMatchString() { return ChatColor.DARK_RED + "No matching player found." + ChatColor.RESET; }
	public String noAccessString() { return ChatColor.DARK_RED + "You do not have access to that command." + ChatColor.RESET; }
	public String noPermissionString() { return ChatColor.DARK_RED + "You don't have the right to do that!" + ChatColor.RESET; }
	public String notPlayerString() { return "Only a player can do that!"; }
	public String warGriefBlockedString() { return ChatColor.DARK_RED + "War grief is only allowed when both rulers are online." + ChatColor.RESET; }
	public String shopFullString() { return ChatColor.RED + "This shop is too full." + ChatColor.RESET; }
	public String shopEmptyString() { return ChatColor.RED + "This shop is empty." + ChatColor.RESET; }
	public String notEnoughShopString() { return ChatColor.RED + "You don't have enough." + ChatColor.RESET; }
		
	public World getWorld() { return world; }
	
	public String kingOf(Quadrant q) { return kings.get(q); }
	
	public void setLastLoginAddress(String pname, String address) {
		logDataConfig.set(pname.toLowerCase() + ".ip", address);
	}
	public User playerLogin(Player p) {
		logDataConfig.set(p.getName().toLowerCase() + ".seen", System.currentTimeMillis());
		saveLogDataConfig();
		User u = new User(this, p);
		users.put(p.getName().toLowerCase(), u);
		return u;
	}
	public void playerLogout(Player p) { users.remove(p.getName().toLowerCase()); }
	public User getUser(String name) { return users.get(name.toLowerCase()); }
	public Collection<User> getUsers() { return users.values(); }
	
	public long getSeen(String p) {
		long then = logDataConfig.getLong(p.toLowerCase() + ".seen");
		long now = System.currentTimeMillis();
		return now - then;
	}
	public String getIP(String p) {
		return logDataConfig.getString(p.toLowerCase() + ".ip");
	}
	
	// Co-ordinate absolute value.
	// Equalizes absolute value for negative coordinates.
	private int cabs(int v) { return v<0 ? (v*-1)-1 : v; }
	
	public boolean isSpawn(int x, int z) {
		return (cabs(z)<spawnRadius && cabs(x)<spawnRadius);		
	}
	public boolean isBadlands(int x, int z) {
		int absx = cabs(x);
		int absz = cabs(z);
		if (absz<badlandsRadius && absx>=safeRadius) return true;
		if (absz>=safeRadius && absx<badlandsRadius) return true;
		return false;
	}
	public boolean canAct(int x, int z, String name) {
		Quadrant q = Quadrant.quadOf(x, z);
		if (isKing(q, name)) return true;
		if (isSpawn(x, z)) return false;
		if (isBadlands(x, z)) return true;
		return isMember(q, name);
	}
	public boolean canFarm(Quadrant perm, Location loc, String player) {
		if (!canAct(loc.getBlockX(), loc.getBlockZ(), player)) return false;
		if (isBadlands(loc.getBlockX(), loc.getBlockZ())) return false;
		if (perm != Quadrant.quadOf(loc.getBlockX(), loc.getBlockZ())) return false;
		return true;
	}
	public boolean kingsOnline(Quadrant a, Quadrant b) {
		if (getUser(kingOf(a)) == null) return false;
		if (getUser(kingOf(b)) == null) return false;
		return true;
	}
	public boolean isAtWar(Quadrant a, Quadrant b) {
		return (getStatus(a, b) + getStatus(b, a) < -2); // One at war, one hostile
	}
	public boolean canGrief(Location loc, String player) {
		if (isSpawn(loc.getBlockX(), loc.getBlockZ())) return false;
		Quadrant ql = Quadrant.quadOf(loc.getBlockX(), loc.getBlockZ()), qs = membership(player);
		if (getStatus(ql, qs) == -2) { // Turf is at war with griefer 
			if (getStatus(qs, ql) == -1) { // Griefer is hostile with turf
				if (getFavor(ql, player) > 1) return true; // Griefer is hated in this turf
			}
			if (getStatus(qs, ql) == -2) return true; // Both at war; allow grief
		}
		return false;
	}
	public boolean canShop(Location loc, String player) {
		if (loc.getWorld() != world) return true; // Allow nether
		if (isBadlands(loc.getBlockX(), loc.getBlockZ())) return true; // Allow badlands
		Quadrant ql = Quadrant.quadOf(loc.getBlockX(), loc.getBlockZ()), qs = membership(player);
		if (ql == qs) return true; // Allow home shopping
		int st = getStatus(ql, qs);
		if (getFavor(ql, player) > 0) return false; // Refuse unwelcome 
		if (isSpawn(loc.getBlockX(), loc.getBlockZ())) {
			if (st < 0) return false; // Refuse hostile
			return true; // All others OK
		}
		int fv = getFavor(qs, player);
		if (st == 2 && fv > 1) return true; // Trusted allies
		if (st == 1 && fv > 2) return true; // Dignified friends
		return false; // Deny other visitors
	}
	public boolean canVisitorAct(Quadrant q, String name) {
		Quadrant of = membership(name);
		return (getStatus(q, of) == 2 && getFavor(of, name) > 2);
	}
	
	public boolean isKing(Quadrant q, String name) {
		return name.toLowerCase().equals(kingOf(q));
	}
	public boolean isMember(Quadrant q, String name) {
		if (isKing(q, name)) { return true; }
		String quad = memberConfig.getString(name.toLowerCase());
		if (quad == null || quad.isEmpty()) { return false; }
		return quad.equals(q.toString());
	}
	public boolean isNew(String name) {
		String quad = memberConfig.getString(name.toLowerCase());
		return (quad == null || quad.isEmpty());
	}
	public Quadrant membership(String name) {
		for (Quadrant q : Quadrant.values()) {
			if (isKing(q, name)) { return q; }
		}
		String quad = memberConfig.getString(name.toLowerCase());
		if (quad == null || quad.isEmpty()) { return null; }
		return Quadrant.toQuadrant(quad);
	}
	public void setMember(Quadrant q, String player) {
		memberConfig.set(player.toLowerCase(), q.toString());
		saveMemberConfig();
		setFavor(q, player, 1);
		User u = getUser(player);
		if (u != null) u.setQuad(q);
	}
	public void clearMember(String player) {
		Quadrant q = membership(player);
		memberConfig.set(player.toLowerCase(), "none");
		saveMemberConfig();
		if (q != null) setFavor(q, player, 0);
		User u = getUser(player);
		if (u != null) u.setQuad(null);
	}
	public void setApplicant(Quadrant q, String name) {
		memberConfig.set(name.toLowerCase(), q.toString() + "-app");
		saveMemberConfig();
	}
	public boolean isApplicant(Quadrant q, String name) {
		String quad = memberConfig.getString(name.toLowerCase());
		if (quad == null || quad.isEmpty()) { return false; }
		return (quad.equals(q.toString() + "-app"));
	}
	public List<String> getApplicants(Quadrant q) {
		List<String> result = new LinkedList<String>();
		for (String s : memberConfig.getKeys(false)) {
			if (memberConfig.getString(s).equals(q.toString() + "-app")) { result.add(s); }
		}
		return result;
	}
	public List<String> getMembers(Quadrant q) {
		List<String> result = new LinkedList<String>();
		for (String s : memberConfig.getKeys(false)) {
			if (isMember(q, s)) { result.add(s); }
		}
		return result;
	}
	
	public void griefAlert(Quadrant q, String griefer) {
		if (!griefAlert.contains(q)) {
			new GriefAlertTimeoutTask(this, q).runTaskLater(this, 1000);
			new GriefAlertNotifyTask(this, griefer, q).runTask(this);
			griefAlert.add(q);
		}
	}
	public void clearGriefAlert(Quadrant q) {
		griefAlert.remove(q);
	}
	
	public void favorUnlock(String name) { favorLock.remove(name.toLowerCase()); }
	public boolean isFavorLocked(String name) { return favorLock.contains(name.toLowerCase()); }
	private void setFavor(Quadrant q, String name, int level) {
		favorConfig.set(name.toLowerCase() + "." + q.toString(), level);
		saveFavorConfig();
		User u = getUser(name);
		if (u != null) u.setFavor(q, level);
		if (!isFavorLocked(name)) {
			favorLock.add(name.toLowerCase());
			new FavorUnlockTask(this, name).runTaskLater(this, 600);
		}
	}
	public int getFavor(Quadrant q, String name) {
		if (q == null) return 0;
		if (isKing(q, name)) return 4;
		int f = favorConfig.getInt(name.toLowerCase() + "." + q.toString(), -99);
		if (f == -99) {
			for (Quadrant quad : Quadrant.values()) { setFavor(quad, name, 0); }
			f = 0;
			saveFavorConfig();
		}
		return f;
	}
	public void raiseFavor(Quadrant q, String name) {
		int f = getFavor(q, name);
		if (isMember(q, name)) {
			f += 1;
			if (f > 3) f = 3;
			setFavor(q, name, f);
		} else {
			f -= 1;
			if (f < 0) f = 0;
			setFavor(q, name, f);
		}
	}
	public void lowerFavor(Quadrant q, String name) {
		int f = getFavor(q, name);
		if (isMember(q, name)) {
			f -= 1;
			if (f < 1) f = 1;
			setFavor(q, name, f);
		} else {
			f += 1;
			if (f > 3) f = 3;
			setFavor(q, name, f);
		}
	}
	
	public void statusUnlock(Quadrant q) { statusLock.remove(q); }
	public boolean isStatusLocked(Quadrant q) { return statusLock.contains(q); }
	private void setStatus(Quadrant with, Quadrant of, int status) {
		getConfig().set("stat" + with + "." + of, status);
		saveConfig();
		if (!isStatusLocked(with)) {
			statusLock.add(with);
			new StatusUnlockTask(this, with).runTaskLater(this, 12000);
		}
	}
	public int getStatus(Quadrant with, Quadrant of) {
		if (with == null) return 0;
		if (of == null) return 0;
		if (with == of) return 0;
		return getConfig().getInt("stat" + with + "." + of);
	}
	public void neutralizeStatus(Quadrant a, Quadrant b) {
		setStatus(a, b, 0);
		setStatus(b, a, 0);
	}
	public void raiseStatus(Quadrant with, Quadrant of) {
		int s = getStatus(with, of);
		s += 1;
		if (s > 2) s = 2;
		setStatus(with, of, s);
	}
	public void lowerStatus(Quadrant with, Quadrant of) {
		int s = getStatus(with, of);
		s -= 1;
		if (s < -2) s = -2;
		setStatus(with, of, s);
	}
	
	public void setQuadName(Quadrant q, String name) {
		Quadrant.setName(q, name);
		getConfig().set("name" + q.toString(), name);
		saveConfig();
	}
	public void setKing(Quadrant q, String king) {
		switch (q) {
		case NW:
			getConfig().set("kingnw", king.toLowerCase());
			break;
		case NE:
			getConfig().set("kingne", king.toLowerCase());
			break;
		case SW:
			getConfig().set("kingsw", king.toLowerCase());
			break;
		case SE:
			getConfig().set("kingse", king.toLowerCase());
			break;
		default:
			return;
		}
		kings.put(q, king.toLowerCase());
		User u = getUser(king);
		if (u != null) {
			setFavor(u.getQuad(), u.getName(), 0);
			u.setQuad(q);
			u.setFavor(q, 4);
		}
		saveConfig();
	}
		
	public Rights getRights() { return rights; }
	
	public int getSpawnRadius() { return spawnRadius; }
	public void setSpawnRadius(int radius) {
		spawnRadius = radius;
		getConfig().set("spawnrad", radius);
		saveConfig();
	}
	public int getBadlandsRadius() { return badlandsRadius; }
	public void setBadlandsRadius(int radius) {
		badlandsRadius = radius;
		getConfig().set("badrad", radius);
		saveConfig();
	}
	public int getSafeRadius() { return safeRadius; }
	public void setSafeRadius(int radius) {
		safeRadius = radius;
		getConfig().set("saferad", radius);
		saveConfig();
	}
	
	private void saveMemberConfig() {
		try { memberConfig.save(memberFile); }
		catch (IOException e) { e.printStackTrace(); }
	}
	
	private void saveFavorConfig() { 
		try { favorConfig.save(favorFile); }
		catch (IOException e) { e.printStackTrace(); }
	}
	
	private void saveLogDataConfig() { 
		try { logDataConfig.save(logDataFile); }
		catch (IOException e) { e.printStackTrace(); }
	}
	
	public void setEnterSign(Location loc) {
		getConfig().set("enterx", loc.getBlockX());
		getConfig().set("entery", loc.getBlockY());
		getConfig().set("enterz", loc.getBlockZ());
		saveConfig();
	}
	public void setExitSign(Location loc) {
		getConfig().set("exitx", loc.getBlockX());
		getConfig().set("exity", loc.getBlockY());
		getConfig().set("exitz", loc.getBlockZ());
		saveConfig();		
	}
	public void setNubSpawn(Location loc) {
		getConfig().set("nubx", loc.getX());
		getConfig().set("nuby", loc.getY());
		getConfig().set("nubz", loc.getZ());
		getConfig().set("nubpitch", loc.getPitch());
		getConfig().set("nubyaw", loc.getYaw());
		saveConfig();				
	}
	public void setEmbassySpawn(Location loc) {
		getConfig().set("embx", loc.getX());
		getConfig().set("emby", loc.getY());
		getConfig().set("embz", loc.getZ());
		getConfig().set("embpitch", loc.getPitch());
		getConfig().set("embyaw", loc.getYaw());
		saveConfig();				
	}
	
	public boolean isEnterSign(Location loc) {
		if (loc.getWorld() != world) return false;
		if (loc.getBlockX() != getConfig().getInt("enterx")) return false;
		if (loc.getBlockY() != getConfig().getInt("entery")) return false;
		if (loc.getBlockZ() != getConfig().getInt("enterz")) return false;
		return true;
	}
	public boolean isExitSign(Location loc) {
		if (loc.getWorld() != world) return false;
		if (loc.getBlockX() != getConfig().getInt("exitx")) return false;
		if (loc.getBlockY() != getConfig().getInt("exity")) return false;
		if (loc.getBlockZ() != getConfig().getInt("exitz")) return false;
		return true;		
	}
	public Location getNubSpawn() {
		return new Location(world, getConfig().getDouble("nubx"), getConfig().getDouble("nuby"), getConfig().getDouble("nubz"), (float) getConfig().getDouble("nubyaw"), (float) getConfig().getDouble("nubpitch"));
	}
	public Location getEmbassySpawn() {
		return new Location(world, getConfig().getDouble("embx"), getConfig().getDouble("emby"), getConfig().getDouble("embz"), (float) getConfig().getDouble("embyaw"), (float) getConfig().getDouble("embpitch"));
		
	}
	
	private void registerEvents() {
		PluginManager pm = getServer().getPluginManager(); 
		TabCompleter ntc = new NullTabCompleter();
		PluginCommand cmd = getCommand("qhelp");
		cmd.setExecutor(new QHelpCommand(this));
		cmd.setTabCompleter(ntc);
		cmd = getCommand("quad");
		TabExecutor te = new QuadCommand(this);
		cmd.setExecutor(te);
		cmd.setTabCompleter(te);
		getCommand("qr").setExecutor(new QRCommand(this));
		getCommand("qt").setExecutor(new QTCommand(this));
		getCommand("qwho").setExecutor(new QWhoCommand(this));
		getCommand("qwhois").setExecutor(new QWhoisCommand(this));
		getCommand("qtp").setExecutor(new QTPCommand(this));
		getCommand("qseen").setExecutor(new QSeenCommand(this));
		getCommand("qmute").setExecutor(new QMuteCommand(this));
		cmd = getCommand("qback"); 
		cmd.setExecutor(new QBackCommand(this));
		cmd.setTabCompleter(ntc);
		cmd = getCommand("qspawn"); 
		cmd.setExecutor(new QSpawnCommand(this));
		cmd.setTabCompleter(ntc);
		cmd = getCommand("qzone");
		cmd.setExecutor(new QZoneCommand(this));
		cmd.setTabCompleter(ntc);
		cmd = getCommand("qafk");
		cmd.setExecutor(new QAFKCommand(this));
		cmd.setTabCompleter(ntc);
		cmd = getCommand("qhome");
		cmd.setExecutor(new QHomeCommand(this));
		cmd.setTabCompleter(ntc);
		cmd = getCommand("qsethome");
		cmd.setExecutor(new QSetHomeCommand(this));
		cmd.setTabCompleter(ntc);
		cmd = getCommand("qid");
		cmd.setExecutor(new QIDCommand(this));
		cmd.setTabCompleter(ntc);

		pm.registerEvents(new PickupListener(this), this);
		pm.registerEvents(new BlockListener(this), this);
		pm.registerEvents(new ClickListener(this), this);
		pm.registerEvents(new DamageListener(this), this);
		pm.registerEvents(new LoginListener(this), this);
		pm.registerEvents(new ChatListener(this), this);
	}
	
	@Override
	public void onEnable() {
		world = getServer().getWorld("world");
		spawnRadius = getConfig().getInt("spawnrad");
		safeRadius = getConfig().getInt("saferad");
		badlandsRadius = getConfig().getInt("badrad");
		Quadrant.setName(Quadrant.NE, getConfig().getString("namene"));
		Quadrant.setName(Quadrant.NW, getConfig().getString("namenw"));
		Quadrant.setName(Quadrant.SE, getConfig().getString("namese"));
		Quadrant.setName(Quadrant.SW, getConfig().getString("namesw"));
		kings = new HashMap<Quadrant, String>(4);
		users = new HashMap<String, User>();
		favorLock = new HashSet<String>(4);
		statusLock = new HashSet<Quadrant>(4);
		griefAlert = new HashSet<Quadrant>(4);
		setKing(Quadrant.NW, getConfig().getString("kingnw"));
		setKing(Quadrant.NE, getConfig().getString("kingne"));
		setKing(Quadrant.SW, getConfig().getString("kingsw"));
		setKing(Quadrant.SE, getConfig().getString("kingse"));
		memberFile = new File(getDataFolder(), memberFilename);
		memberConfig = YamlConfiguration.loadConfiguration(memberFile);
		favorFile = new File(getDataFolder(), favorFilename);
		favorConfig = YamlConfiguration.loadConfiguration(favorFile);
		logDataFile = new File(getDataFolder(), logDataFilename);
		logDataConfig = YamlConfiguration.loadConfiguration(logDataFile);
		rights = new Rights(this);
		registerEvents();
		for (Player p : getServer().getOnlinePlayers()) playerLogin(p);
	}

	@Override
	public void onDisable() {
		saveMemberConfig();
		saveFavorConfig();
		saveLogDataConfig();
		kings = null;
		users = null;
		favorLock = null;
		statusLock = null;
		griefAlert = null;
		world = null;
		memberFile = null;
		memberConfig = null;
		favorFile = null;
		favorConfig = null;
		logDataFile = null;
		logDataConfig = null;
		rights = null;
	}
}
