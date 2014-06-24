package com.gmail.chernobyl169.feudalism.locking;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public enum Currency {
	GOLD_NUGGET("n"),
	GOLD_INGOT("g"),
	GOLD_BLOCK("G"),
//	EMERALD("e"),
//	EMERALD_BLOCK("E"),
	DIAMOND("d"),
	DIAMOND_BLOCK("D");
	
	private final String st;
	
	private final static Map<String, Currency> by_name = new HashMap<String, Currency>(values().length);
	
	private Currency(String st) {
		this.st = st;
	}
	
	static {
		for (Currency c : values()) {
			by_name.put(c.st, c);
		}
	}
	
	public ItemStack createItems(int amount) {
		return new ItemStack(material(), amount);
	}
	
	/**
	 * Find the Currency associated with a currency shorthand character.
	 * @param st
	 *   - The shorthand character on a shop sign.
	 * @return
	 *   - The Currency associated with that character, or null if not found.
	 */
	public static Currency getByChar(String st) { return by_name.get(st); }
	
	/**
	 * Find the Currency associated with a {@link org.bukkit.Material}.
	 * @param m
	 *   - The Material to find.
	 * @return
	 *   - The Currency representing that Material, or null if not found.
	 */
	public static Currency getByMaterial(Material m) {
		for (Currency c : values()) {
			if (c.material() == m) return c;
		}
		return null;
	}
	
	/**
	 * Get the {@link org.bukkit.Material} this Currency represents.
	 * @return
	 *   - The Material this Currency represents.
	 */
	public Material material() { return Material.getMaterial(this.name()); }
	
	/**
	 * Get the friendly name of the Currency.
	 * @return
	 *   - A human-readable form of the name of this Currency, in all lower case.
	 */
	public String getName() {
		return name().toLowerCase().replaceAll("_", " ");		
	}
}
