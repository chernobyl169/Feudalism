package com.gmail.chernobyl169.feudalism.locking;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public class Shop implements LockSign {

	public enum ShopResult {
		OK, EMPTY, FULL, SHORT, OVER
	}
	
	private final boolean valid;
	
	private Block chest;
	private Inventory inv;
	private boolean isBounty;
	private int quantity, amount, supply, cash, empties;
	private ShopItem item;
	private Currency currency;
	private String owner;

	/**
	 * Instantiate a Shop based on a sign {@link org.bukkit.Block}. This block must be
	 * a {@link org.bukkit.Material.WALL_SIGN} for the shop to be valid, and the block
	 * directly below it must be a {@link org.bukkit.Material.CHEST}.
	 * @param block
	 *   - The wall sign block for this Shop.
	 */
	public Shop(Block block) {
		if (block.getType() != Material.WALL_SIGN) {
			valid = false;
			return;
		}
		chest = block.getRelative(BlockFace.DOWN);
		if (chest.getType() != Material.CHEST) {
			valid = false;
			return;
		}
		Sign sign = (Sign)block.getState();
		switch (sign.getLine(0).toLowerCase()) {
		case "[shop]":
			isBounty = false;
			break;
		case "[bounty]":
			isBounty = true;
			break;
		default:
			valid = false;
			return;
		}
		item = ShopItem.getByName(sign.getLine(1));
		if (item == null) {
			valid = false;
			return;
		}
		if (!match(sign.getLine(2))) {
			valid = false;
			return;
		}
		if (sign.getLine(3).equals("")) {
			valid = false;
			return;
		}
		owner = sign.getLine(3);
		valid = true;
		empties = 0;
		supply = 0;
		cash = 0;
		inv = ((InventoryHolder)chest.getState()).getInventory();
		ItemStack contents[] = inv.getContents();
		for (int i = 0; i < contents.length; i++) {
			final ItemStack it = contents[i];
			if (it == null) {
				empties += 1;
			} else {
				if (item.itemMatch(it)) supply += it.getAmount();
				if (currency.material() == it.getType()) cash += it.getAmount();
			}
		}
	}

	private boolean match(String line3) {
		String bits[] = Util.pattern.split(line3);
		if (bits.length <= 3) return false;
		try {
			quantity = Integer.parseInt(bits[1]);
			amount = Integer.parseInt(bits[2]);
			currency = Currency.getByChar(bits[3]);
		}
		catch (NumberFormatException e) { return false; }
		if (quantity <= 0) return false;
		if (amount <= 0) return false;
		if (currency == null) return false;
		return true;
	}
		
	public boolean isValid() { return valid; }
	public String getOwner() { return owner; }
	public boolean ownedBy(String player) { return owner.equalsIgnoreCase(Util.chopped(player)); }
	
	/**
	 * Discover whether this Shop is a bounty (or a sale).
	 * @return
	 *   - Whether this Shop is a bounty.
	 */
	public boolean isBounty() { return isBounty; }
	
	/**
	 * Get the quantity of items exchanged in one transaction.
	 * @return
	 *   - The shop transaction quantity.
	 */
	public int getQuantity() { return quantity; }
	
	/**
	 * Get the amount of currency items exchanged in one transaction.
	 * @return
	 *   - The shop transaction amount.
	 */
	public int getAmount() { return amount; }
	
	/**
	 * Get the {@link com.gmail.chernobyl169.feudalism.locking.Currency} used in this Shop.
	 * @return
	 *   - The Currency specified in the Shop
	 */
	public Currency getCurrency() { return currency; }
	
	/**
	 * Get the {@link com.gmail.chernobyl169.feudalism.locking.ShopItem} used in this Shop.
	 * @return
	 *   - The ShopItem representing the item for sale or purchase.
	 */
	public ShopItem getItem() { return item; }
	
	public int quantityRemaining() { return supply; }
	
	public int amountRemaining() { return cash; }
	
	public boolean hasRoom() {
		int fit = isBounty ? quantity : amount;
		int max = isBounty ? item.stackSize() : currency.material().getMaxStackSize();
		int free = empties * max;
		int t = (isBounty ? supply : cash) % max;
		if (t != 0) free += max - t;
		return free >= fit;
	}
	
	public boolean isEmpty() { return isBounty ? cash < amount : supply < quantity; }
	
	@SuppressWarnings("deprecation")
	public ShopResult transact(Player player) {
		if (!hasRoom()) return ShopResult.FULL;
		if (isEmpty()) return ShopResult.EMPTY;
		Inventory pinv = player.getInventory();
		if (!pinv.containsAtLeast(player.getItemInHand(), isBounty ? quantity : amount)) return ShopResult.SHORT;
		// Remove from shop
		int left = isBounty ? amount : quantity;
		ItemStack items[] = inv.getContents();
		for (int i = 0; i < items.length; i++) {
			if (left > 0 && items[i] != null) {
				if (isBounty ? currency.material() == items[i].getType() : item.itemMatch(items[i])) {
					if (left >= items[i].getAmount()) {
						left -= items[i].getAmount();
						inv.clear(i);
					} else {
						items[i].setAmount(items[i].getAmount() - left);
						left = 0;
					}
				}
			}
		}
		// Remove from player
		left = isBounty ? quantity : amount;
		items = pinv.getContents();
		for (int i = 0; i < items.length; i++) {
			if (left > 0 && items[i] != null) {
				if (isBounty ? item.itemMatch(items[i]) : currency.material() == items[i].getType()) {
					if (left >= items[i].getAmount()) {
						left -= items[i].getAmount();
						pinv.clear(i);
					} else {
						items[i].setAmount(items[i].getAmount() - left);
						left = 0;
					}
				}
			}
		}
		// Add to shop
		left = isBounty ? quantity : amount;
		int stack = isBounty ? item.stackSize() : currency.material().getMaxStackSize();
		items = inv.getContents();
		int q;
		for (int i = 0; i < items.length; i++) {
			if (left > 0) {
				if (items[i] == null) {
					q = stack < left ? stack : left;
					inv.setItem(i, isBounty ? item.createItems(q) : currency.createItems(q));
					left -= q;
				} else if (isBounty ? item.itemMatch(items[i]) : currency.material() == items[i].getType()) {
					q = stack - items[i].getAmount();
					if (q > left) {
						items[i].setAmount(items[i].getAmount() + left);
						left = 0;
					} else {
						items[i].setAmount(stack);
						left -= q;
					}
				}
			}
		}
		// Add to player
		left = isBounty ? amount : quantity;
		stack = isBounty ? currency.material().getMaxStackSize() : item.stackSize();
		items = pinv.getContents();
		for (int i = 0; i < items.length; i++) {
			if (left > 0) {
				if (items[i] == null) {
					q = stack < left ? stack : left;
					pinv.setItem(i, isBounty ? currency.createItems(q) : item.createItems(q));
					left -= q;
				} else if (isBounty ? currency.material() == items[i].getType() : item.itemMatch(items[i])) {
					q = stack - items[i].getAmount();
					if (q > left) {
						items[i].setAmount(items[i].getAmount() + left);
						left = 0;
					} else {
						items[i].setAmount(stack);
						left -= q;
					}
				}
			}
		}
		player.updateInventory();
		// Dump leftovers
		if (left > 0) {
			Location loc = player.getLocation();
			while (left > 0) {
				q = left < stack ? left : stack;
				ItemStack it = isBounty? currency.createItems(q) : item.createItems(q);
				loc.getWorld().dropItem(loc, it);
				left -= q;
			}
			return ShopResult.OVER;
		}
		return ShopResult.OK;
	}
}
