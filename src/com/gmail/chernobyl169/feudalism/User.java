package com.gmail.chernobyl169.feudalism;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class User {

	private final String name;
	private final Player pl;
	private Quadrant membership;
	private Map<Quadrant, Integer> favor;
	private User replyTarget;
	private Location backTarget;
	private long muteTime, lastAFK;
	
	public User(FeudalismPlugin plugin, Player player) {
		pl = player;
		name = player.getName();
		membership = plugin.membership(name);
		favor = new HashMap<Quadrant, Integer>(4, (float) 1.0);
		for (Quadrant q : Quadrant.values()) favor.put(q, plugin.getFavor(q, name));
	}
	
	public boolean isMuted() {
		return muteTime > System.currentTimeMillis();
	}
	public void mute() {
		if (isMuted()) {
			muteTime = 0L;
		} else {
			muteTime = System.currentTimeMillis() + 300000; // 300s = 5m
		}
	}
	
	public boolean afkBoot() { return System.currentTimeMillis() - lastAFK < 30000; }
	public void afk() {
		lastAFK = System.currentTimeMillis();
	}
	
	public String getName() { return name; }
	public Quadrant getQuad() { return membership; }
	
	public void setQuad(Quadrant q) { membership = q; }
	
	public int getFavor(Quadrant q) {
		if (q == null) return 0;
		return favor.get(q);
	}
	
	public void setFavor(Quadrant q, int favor) { if (q != null) this.favor.put(q, favor); }
	
	public int getRank() { return getFavor(membership); }
	
	public boolean isKing(Quadrant q) {
		return membership == q && getFavor(q) == 4;
	}
	
	public void sendMessage(String message) { pl.sendMessage(message); }
	public void setReplyTarget(User sender) { replyTarget = sender; }
	public User getReplyTarget() { return replyTarget; }
	
	public void setBackTarget(Location loc) { backTarget = loc; }
	public Location getBackTarget() { return backTarget; }
}
