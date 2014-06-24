package com.gmail.chernobyl169.feudalism.locking;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

public class BlockChecker {

	private BlockChecker() {}

	private static BlockFace[] dirs = {BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST};
	
	public static Lock getLockSign(Block block) {
		if (block.getType() != Material.WALL_SIGN) return null;
		Lock t = new Lock(block);
		return t.isValid() ? t : null;
	}
	
	public static Shop getShopSign(Block block) {
		if (block.getType() != Material.WALL_SIGN) return null;
		Shop t = new Shop(block);
		return t.isValid() ? t : null;		
	}
	
	public static Trade getTradeSign(Block block) {
		if (block.getType() != Material.WALL_SIGN) return null;
		Trade t = new Trade(block);
		return t.isValid() ? t : null;		
	}
	
	@SuppressWarnings("deprecation")
	private static LockSign checkDir(Block block, BlockFace face, boolean getAll) {
		int opp = 0;
		Block t = block.getRelative(face);
		if (t.getType() != Material.WALL_SIGN) return null;
		switch (face) {
		case NORTH:
			opp = 2;
			break;
		case SOUTH:
			opp = 3;
			break;
		case WEST:
			opp = 4;
			break;
		case EAST:
			opp = 5;
			break;
		default:
			return null;
		}
		if (t.getData() != opp) return null;
		LockSign s = getLockSign(t);
		if (getAll) {
			if (s == null) s = getShopSign(t);
			if (s == null) s = getTradeSign(t);
		}
		return s;
	}
	
	private static LockSign getSupported(Block block, boolean getAll) {
		LockSign ls = null;
		for (BlockFace face : dirs) {
			if (ls == null) ls = checkDir(block, face, getAll);
		}
		return ls;
	}
	
	/**
	 * Discover whether this block is locked.
	 * @param block
	 * The block to check
	 * @return
	 * Whether the block is locked by a shop or lock sign
	 */
	public static boolean isProtected(Block block) {
		return getLockOf(block) != null;
	}
	
	/**
	 * Get the lock for this block.
	 * @param block
	 * The block to check
	 * @return
	 * The lock, or null if not locked
	 */
	public static LockSign getLockOf(Block block) {
		Block t = null;
		LockSign lock = getSupported(block, true); // Holds lock
		switch(block.getType()) {
		case WALL_SIGN:
			if (lock == null) lock = getLockSign(block); // Is lock
			if (lock == null) lock = getShopSign(block); // Is shop
			break;
		case CHEST:
			if (lock == null) lock = getShopSign(block.getRelative(BlockFace.UP)); // Chest from above
			for (BlockFace face : dirs) {
				t = block.getRelative(face);
				if (t.getType() == block.getType()) { // Double chest
					if (lock == null) lock = getShopSign(t.getRelative(BlockFace.UP));
					if (lock == null) lock = getSupported(t, true);
				}
			}
			break;
		case WOOD_DOOR:
		case WOODEN_DOOR:
//		case IRON_DOOR:
			t = block.getRelative(BlockFace.UP);
			if (lock == null) lock = getSupported(t, false);
			if (t.getType() == block.getType()) {
				t = t.getRelative(BlockFace.UP);
				if (lock == null) lock = getLockSign(t);
				if (lock == null) lock = getSupported(t, false);
			} else {
				if (lock == null) lock = getLockSign(t);
				t = block.getRelative(BlockFace.DOWN);
				if (lock == null) lock = getSupported(t, false);
			}
			break;
		case STONE_BUTTON:
		case WOOD_BUTTON:
		case LEVER:
			if (lock == null) lock = getLockSign(block.getRelative(BlockFace.UP));
			if (lock == null) lock = getLockSign(block.getRelative(BlockFace.DOWN));
			break;
		default:
			break;
		}
		return lock;
	}
}
