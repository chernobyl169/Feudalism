package com.gmail.chernobyl169.feudalism.locking;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;

public class Trade implements LockSign {

	private final boolean valid;
	
	private String owner, other;
	
	public Trade(Block block) {
		if (block.getType() != Material.WALL_SIGN) {
			valid = false;
			return;
		}		
		Sign sign = (Sign)block.getState();
		if (!sign.getLine(0).equalsIgnoreCase("[trade]")) {			
			valid = false;
			return;
		}
		if (sign.getLine(3).equals("")) {
			valid = false;
			return;
		}
		owner = sign.getLine(3);
		other = sign.getLine(2);
		if (other.equals("")) other = null;
		valid = true;		
	}

	public boolean isValid() { return valid; }
	public String getOwner() { return owner; }
	public String getOther() { return other; }
	public boolean ownedBy(String player) { return owner.equalsIgnoreCase(Util.chopped(player)); }
	public boolean isOther(String player) { return Util.chopped(player).equalsIgnoreCase(other); }

}
