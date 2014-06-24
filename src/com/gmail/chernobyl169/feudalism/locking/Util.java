package com.gmail.chernobyl169.feudalism.locking;

import java.util.regex.Pattern;

public class Util {

	private Util() {}

	public static Pattern pattern = Pattern.compile("^\\D*(?=\\d)|(?<=\\d)\\D+(?=\\d)|(?<=\\d)\\D*(?=\\D$)");
	
	public static boolean shopMatch(String string) {
		int quantity, amount;
		Currency currency;
		String bits[] = Util.pattern.split(string);
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
	
	/**
	 * Get the chopped version of a player's name.
	 * @param player
	 *   - The player's name
	 * @return
	 *   - The sign-friendly chopped version
	 */
	public static String chopped(String player) {
		if (player == null) return null;
		if (player.length() < 16) return player;
		return player.substring(0, 15);
	}

}
