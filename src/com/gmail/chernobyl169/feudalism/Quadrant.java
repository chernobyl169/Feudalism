package com.gmail.chernobyl169.feudalism;

public enum Quadrant {
	NE,
	NW,
	SE,
	SW;
	
	private static String nwLong, neLong, swLong, seLong;
	
	public String toString() {
		switch (this) {
		case NW:
			return "nw";
		case NE:
			return "ne";
		case SW:
			return "sw";
		case SE:
			return "se";
		default:
			return "";
		}
	}
	
	public String longName() {
		switch (this) {
		case NW:
			return nwLong;
		case NE:
			return neLong;
		case SW:
			return swLong;
		case SE:
			return seLong;
		default:
			return "";
		}
	}
	
	public static void setName(Quadrant q, String name) {
		switch(q) {
		case NW:
			nwLong = name;
			break;
		case NE:
			neLong = name;
			break;
		case SW:
			swLong = name;
			break;
		case SE:
			seLong = name;
			break;
		default:
			break;
		}
	}
	public static Quadrant quadOf(int x, int z) {
		if (z<0) {
			if (x<0) { return Quadrant.NW; }
			else { return Quadrant.NE; }
		} else {
			if (x<0) { return Quadrant.SW; }
			else { return Quadrant.SE; }
		}
	}
	
	public static Quadrant toQuadrant(String s) {
		if (s == null) { return null; }
		if (s.equalsIgnoreCase("sw")) { return Quadrant.SW; }
		if (s.equalsIgnoreCase("nw")) { return Quadrant.NW; }
		if (s.equalsIgnoreCase("se")) { return Quadrant.SE; }
		if (s.equalsIgnoreCase("ne")) { return Quadrant.NE; }
		return null;
	}
}
