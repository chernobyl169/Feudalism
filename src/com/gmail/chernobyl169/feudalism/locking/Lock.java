package com.gmail.chernobyl169.feudalism.locking;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;

public class Lock implements LockSign {

	private final boolean valid;
	
	private String owner;
	private int rank;
	
	public Lock(Block block) {
		if (block.getType() != Material.WALL_SIGN) {
			valid = false;
			return;
		}		
		Sign sign = (Sign)block.getState();
		if (!sign.getLine(0).equalsIgnoreCase("[lock]")) {			
			valid = false;
			return;
		}
		if (sign.getLine(3).equals("")) {
			valid = false;
			return;
		}
		rank = 4;
		String s = sign.getLine(1);
		if (s != null && !s.equals("")) {
			switch(s.toLowerCase()) {
			case "respected":
			case "[respected]":
				rank = 1;
				break;
			case "trusted":
			case "[trusted]":
				rank = 2;
				break;
			case "dignified":
			case "[dignified]":
				rank = 3;
				break;
			}
		}
		owner = sign.getLine(3);
		valid = true;
	}
	
	public boolean isValid() { return valid; }
	public String getOwner() { return owner; }
	public boolean ownedBy(String player) { return owner.equalsIgnoreCase(Util.chopped(player)); }
	public boolean canAccess(int rank) { return (rank >= this.rank); }
}
