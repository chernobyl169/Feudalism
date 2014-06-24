package com.gmail.chernobyl169.feudalism.locking;

public interface LockSign {

	/**
	 * Find the owner of this sign.
	 * @return
	 * The owner's name, or null for invalid sign.
	 */
	public String getOwner();
	
	/**
	 * Discover whether this is a valid lock.
	 * @return
	 * True if this is a valid lock.
	 */
	public boolean isValid();

	/**
	 * Discover whether this lock is owned by someone.
	 * @param player
	 * The player checking
	 * @return
	 * Whether they own this lock
	 */
	public boolean ownedBy(String player);
}

