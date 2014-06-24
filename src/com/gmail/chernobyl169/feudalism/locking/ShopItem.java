package com.gmail.chernobyl169.feudalism.locking;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.inventory.ItemStack;

/**
 * An Enum representing every item that can be sold at a shop.
 * @author chernobyl169
 *
 */
public enum ShopItem {
	STONE(1),
	GRASS(2),
	DIRT(3),
	PODZOL(3, 2), // 1.7
	COBBLE(4),
	OAK_PLANK(5),
	SPRUCE_PLANK(5, 1),
	BIRCH_PLANK(5, 2),
	JUNGLE_PLANK(5, 3),
	ACACIA_PLANK(5, 4), // 1.7
	DARK_PLANK(5, 5), // 1.7
	OAK_SAPLING(6),
	SPRUCE_SAPLING(6, 1),
	BIRCH_SAPLING(6, 2),
	JUNGLE_SAPLING(6, 3),
	ACACIA_SAPLING(6, 4), // 1.7
	DARK_SAPLING(6, 5), // 1.7
	SAND(12),
	GRAVEL(13),
	GOLD_ORE(14),
	IRON_ORE(15),
	COAL_ORE(16),
	OAK_LOG(17),
	SPRUCE_LOG(17, 1),
	BIRCH_LOG(17, 2),
	JUNGLE_LOG(17, 3),
	OAK_LEAVES(18),
	SPRUCE_LEAVES(18, 1),
	BIRCH_LEAVES(18, 2),
	JUNGLE_LEAVES(18, 3),
	GLASS(20),
	LAPIS_ORE(21),
	LAPIS_BLOCK(22),
	DISPENSER(23),
	SANDSTONE(24),
	CUT_SANDSTONE(24, 1),
	FINE_SANDSTONE(24, 2),
	NOTE_BLOCK(25),
	POWERED_RAIL(27),
	DETECTOR_RAIL(28),
	STICKY_PISTON(29),
	SPIDERWEB(30),
	TALL_GRASS(31, 1),
	FERN(31, 2),
	SHRUB(32),
	PISTON(33),
	WHITE_WOOL(35),
	ORANGE_WOOL(35, 1),
	MAGENTA_WOOL(35, 2),
	SKY_WOOL(35, 3),
	YELLOW_WOOL(35, 4),
	LIME_WOOL(35, 5),
	PINK_WOOL(35, 6),
	GRAY_WOOL(35, 7),
	SILVER_WOOL(35, 8),
	CYAN_WOOL(35, 9),
	PURPLE_WOOL(35, 10),
	BLUE_WOOL(35, 11),
	BROWN_WOOL(35, 12),
	GREEN_WOOL(35, 13),
	RED_WOOL(35, 14),
	BLACK_WOOL(35, 15),
//	FLOWER(37), // 1.6
//	ROSE(38), // 1.6
	DANDELION(37), // 1.7
	POPPY(38), // 1.7
	BLUE_ORCHID(38, 1), // 1.7
	ALLIUM(38, 2), // 1.7
	AZURE_BLUET(38, 3), // 1.7
	RED_TULIP(38, 4), // 1.7
	ORANGE_TULIP(38, 5), // 1.7
	WHITE_TULIP(38, 6), // 1.7
	PINK_TULIP(38, 7), // 1.7
	OXEYE_DAISY(38, 8), // 1.7
	BROWN_MUSHROOM(39),
	RED_MUSHROOM(40),
	GOLD_BLOCK(41),
	IRON_BLOCK(42),
	STONE_SLAB(44),
	SANDSTONE_SLAB(44, 1),
	COBBLE_SLAB(44, 3),
	BRICK_SLAB(44, 4),
	STBRICK_SLAB(44, 5),
	NETHBRICK_SLAB(44, 6),
	QUARTZ_SLAB(44, 7),
	BRICK(45),
	TNT(46),
	BOOKSHELF(47),
	MOSSY_COBBLE(48),
	OBSIDIAN(49),
	TORCH(50),
	OAK_STAIR(53),
	CHEST(54),
	DIAMOND_ORE(56),
	DIAMOND_BLOCK(57),
	CRAFTING_TABLE(58),
	FURNACE(61),
	LADDER(65),
	RAIL(66),
	COBBLE_STAIR(67),
	LEVER(69),
	STONE_PLATE(70),
	WOODEN_PLATE(72),
	REDSTONE_ORE(73),
	REDSTONE_TORCH(76),
	STONE_BUTTON(77),
	ICE(79),
	SNOW_BLOCK(80),
	CACTUS(81),
	CLAY_BLOCK(82),
	JUKEBOX(84),
	FENCE(85),
	PUMPKIN(86),
	NETHERRACK(87),
	SOUL_SAND(88),
	GLOWSTONE_BLOCK(89),
	JACK_O_LANTERN(91),
	WHITE_STGLASS(95), // 1.7
	ORANGE_STGLASS(95, 1), // 1.7
	MAGENTA_STGLASS(95, 2), // 1.7
	SKY_STGLASS(95, 3), // 1.7
	YELLOW_STGLASS(95, 4), // 1.7
	LIME_STGLASS(95, 5), // 1.7
	PINK_STGLASS(95, 6), // 1.7
	GRAY_STGLASS(95, 7), // 1.7
	SILVER_STGLASS(95, 8), // 1.7
	CYAN_STGLASS(95, 9), // 1.7
	PURPLE_STGLASS(95, 10), // 1.7
	BLUE_STGLASS(95, 11), // 1.7
	BROWN_STGLASS(95, 12), // 1.7
	GREEN_STGLASS(95, 13), // 1.7
	RED_STGLASS(95, 14), // 1.7
	BLACK_STGLASS(95, 15), // 1.7
	TRAPDOOR(96),
	STBRICK(98),
	MOSSY_STBRICK(98, 1),
	CRACKED_STBRICK(98, 2),
	CUT_STBRICK(98, 3),
	IRON_BARS(101),
	GLASS_PANE(102),
	MELON_BLOCK(103),
	VINES(106),
	FENCE_GATE(107),
	BRICK_STAIR(108),
	STBRICK_STAIR(109),
	MYCELIUM(110),
	LILY_PAD(111),
	NETHBRICK(112),
	NETHER_FENCE(113),
	NETHBRICK_STAIR(114),
	ENCHANT_TABLE(116),
	END_STONE(121),
	DRAGON_EGG(122),
	REDSTONE_LAMP(123),
	OAK_SLAB(126),
	SPRUCE_SLAB(126, 1),
	BIRCH_SLAB(126, 2),
	JUNGLE_SLAB(126, 3),
	ACACIA_SLAB(126, 4), // 1.7
	DARK_SLAB(126, 5), // 1.7
	SANDSTONE_STAIR(128),
	EMERALD_ORE(129),
	ENDER_CHEST(130),
	TRIPWIRE_HOOK(131),
	EMERALD_BLOCK(133),
	SPRUCE_STAIR(134),
	BIRCH_STAIR(135),
	JUNGLE_STAIR(136),
	BEACON(138),
	COBBLE_WALL(139),
	MOSSY_WALL(139, 1),
	WOODEN_BUTTON(143),
	ANVIL(145),
	TRAPPED_CHEST(146),
	GOLD_PLATE(147),
	IRON_PLATE(148),
	SOLAR_PANEL(151),
	REDSTONE_BLOCK(152),
	QUARTZ_ORE(153),
	HOPPER(154),
	QUARTZ_BLOCK(155),
	CUT_QUARTZ(155, 1),
	FINE_QUARTZ(155, 2),
	QUARTZ_STAIR(156),
	DROPPER(158),
	WHITE_CLAY(159),
	ORANGE_CLAY(159, 1),
	MAGENTA_CLAY(159, 2),
	SKY_CLAY(159, 3),
	YELLOW_CLAY(159, 4),
	LIME_CLAY(159, 5),
	PINK_CLAY(159, 6),
	GRAY_CLAY(159, 7),
	SILVER_CLAY(159, 8),
	CYAN_CLAY(159, 9),
	PURPLE_CLAY(159, 10),
	BLUE_CLAY(159, 11),
	BROWN_CLAY(159, 12),
	GREEN_CLAY(159, 13),
	RED_CLAY(159, 14),
	BLACK_CLAY(159, 15),
	WHITE_ST_PANE(160), // 1.7
	ORANGE_ST_PANE(160, 1), // 1.7
	MAGENTA_ST_PANE(160, 2), // 1.7
	SKY_ST_PANE(160, 3), // 1.7
	YELLOW_ST_PANE(160, 4), // 1.7
	LIME_ST_PANE(160, 5), // 1.7
	PINK_ST_PANE(160, 6), // 1.7
	GRAY_ST_PANE(160, 7), // 1.7
	SILVER_ST_PANE(160, 8), // 1.7
	CYAN_ST_PANE(160, 9), // 1.7
	PURPLE_ST_PANE(160, 10), // 1.7
	BLUE_ST_PANE(160, 11), // 1.7
	BROWN_ST_PANE(160, 12), // 1.7
	GREEN_ST_PANE(160, 13), // 1.7
	RED_ST_PANE(160, 14), // 1.7
	BLACK_ST_PANE(160, 15), // 1.7
	ACACIA_LEAVES(161), // 1.7
	DARK_LEAVES(161, 1), // 1.7
	ACACIA_LOG(162), // 1.7
	DARK_LOG(162, 1), // 1.7
	ACACIA_STAIR(163), // 1.7
	DARK_STAIR(164), // 1.7
	HAY_BALE(170),
	WHITE_CARPET(171),
	ORANGE_CARPET(171, 1),
	MAGENTA_CARPET(171, 2),
	SKY_CARPET(171, 3),
	YELLOW_CARPET(171, 4),
	LIME_CARPET(171, 5),
	PINK_CARPET(171, 6),
	GRAY_CARPET(171, 7),
	SILVER_CARPET(171, 8),
	CYAN_CARPET(171, 9),
	PURPLE_CARPET(171, 10),
	BLUE_CARPET(171, 11),
	BROWN_CARPET(171, 12),
	GREEN_CARPET(171, 13),
	RED_CARPET(171, 14),
	BLACK_CARPET(171, 15),
	HARD_CLAY(172),
	COAL_BLOCK(173),
	PACKED_ICE(174), // 1.7
	SUNFLOWER(175), // 1.7
	LILAC(175, 1), // 1.7
	LARGE_GRASS(175, 2), // 1.7
	LARGE_FERN(175, 3), // 1.7
	ROSEBUSH(175, 4), // 1.7
	PEONY(175, 5), // 1.7

	IRON_SHOVEL(256),
	IRON_PICKAXE(257),
	IRON_AXE(258),
	FLINT_AND_STEEL(259),
	APPLE(260),
	BOW(261),
	ARROW(262),
	COAL(263),
	CHARCOAL(263, 1),
	DIAMOND(264),
	IRON_INGOT(265),
	GOLD_INGOT(266),
	IRON_SWORD(267),
	WOODEN_SWORD(268),
	WOODEN_SHOVEL(269),
	WOODEN_PICKAXE(270),
	WOODEN_AXE(271),
	STONE_SWORD(272),
	STONE_SHOVEL(273),
	STONE_PICKAXE(274),
	STONE_AXE(275),
	DIAMOND_SWORD(276),
	DIAMOND_SHOVEL(277),
	DIAMOND_PICKAXE(278),
	DIAMOND_AXE(279),
	STICK(280),
	BOWL(281),
	MUSHROOM_STEW(282),
	GOLD_SWORD(283),
	GOLD_SHOVEL(284),
	GOLD_PICKAXE(285),
	GOLD_AXE(286),
	STRING(287),
	FEATHER(288),
	GUNPOWDER(289),
	WOODEN_HOE(290),
	STONE_HOE(291),
	IRON_HOE(292),
	DIAMOND_HOE(293),
	GOLD_HOE(294),
	SEEDS(295),
	WHEAT(296),
	BREAD(297),
	LEATHER_HELMET(298),
	LEATHER_ARMOR(299),
	LEATHER_LEGGING(300),
	LEATHER_BOOTS(301),
	CHAIN_HELMET(302),
	CHAIN_ARMOR(303),
	CHAIN_LEGGING(304),
	CHAIN_BOOTS(305),
	IRON_HELMET(306),
	IRON_ARMOR(307),
	IRON_LEGGING(308),
	IRON_BOOTS(309),
	DIAMOND_HELMET(310),
	DIAMOND_ARMOR(311),
	DIAMOND_LEGGING(312),
	DIAMOND_BOOTS(313),
	GOLD_HELMET(314),
	GOLD_ARMOR(315),
	GOLD_LEGGING(316),
	GOLD_BOOTS(317),
	FLINT(318),
	PORKCHOP(319),
	COOKED_PORKCHOP(320),
	PAINTING(321),
	GOLD_APPLE(322),
	NOTCH_APPLE(322, 1),
	SIGN(323),
	WOODEN_DOOR(324),
	BUCKET(325),
	WATER_BUCKET(326),
	LAVA_BUCKET(327),
	MINECART(328),
	SADDLE(329),
	IRON_DOOR(330),
	REDSTONE_DUST(331),
	SNOWBALL(332),
	BOAT(333),
	LEATHER(334),
	MILK_BUCKET(335),
	CLAY_BRICK(336),
	CLAY(337),
	SUGAR_CANE(338),
	PAPER(339),
	BOOK(340),
	SLIMEBALL(341),
	CHEST_MINECART(342),
	POWER_MINECART(343),
	EGG(344),
	COMPASS(345),
	FISHING_ROD(346),
	CLOCK(347),
	GLOWSTONE_DUST(348),
	RAW_FISH(349),
	RAW_SALMON(349, 1), // 1.7
	RAW_CLOWNFISH(349, 2), // 1.7
	RAW_PUFFERFISH(349, 3), // 1.7
	FISH(350),
	SALMON(350, 1), // 1.7
	CLOWNFISH(350, 2), // 1.7
	PUFFERFISH(350, 3), // 1.7
	INK_SAC(351),
	RED_DYE(351, 1),
	GREEN_DYE(351, 2),
	COCOA_BEAN(351, 3),
	LAPIS(351, 4),
	PURPLE_DYE(351, 5),
	CYAN_DYE(351, 6),
	SILVER_DYE(351, 7),
	GRAY_DYE(351, 8),
	PINK_DYE(351, 9),
	LIME_DYE(351, 10),
	YELLOW_DYE(351, 11),
	SKY_DYE(351, 12),
	MAGENTA_DYE(351, 13),
	ORANGE_DYE(351, 14),
	BONEMEAL(351, 15),
	BONE(352),
	SUGAR(353),
	CAKE(354),
	BED(355),
	REPEATER(356),
	COOKIE(357),
	SHEARS(359),
	MELON(360),
	PUMPKIN_SEEDS(361),
	MELON_SEEDS(362),
	STEAK(363),
	COOKED_STEAK(364),
	CHICKEN(365),
	COOKED_CHICKEN(366),
	ROTTEN_FLESH(367),
	ENDER_PEARL(368),
	BLAZE_ROD(369),
	GHAST_TEAR(370),
	GOLD_NUGGET(371),
	NETHER_WART(372),
	WATER_BOTTLE(373),
	AWKWARD_POTION(373, 16),
	THICK_POTION(373, 32),
	MUNDANE_POTION(373, 64),
	REGEN_POTION(373, 8193),
	SPEED_POTION(373, 8194),
	FIRE_POTION(373, 8195),
	POISON_POTION(373, 8196),
	HEAL_POTION(373, 8197),
	SIGHT_POTION(373, 8198),
	WEAK_POTION(373, 8200),
	STRONG_POTION(373, 8201),
	SLOW_POTION(373, 8202),
	DAMAGE_POTION(373, 8204),
	INVIS_POTION(373, 8206),
	REGEN_2_POTION(373, 8225),
	SPEED_2_POTION(373, 8226),
	POISON_2_POTION(373, 8228),
	HEAL_2_POTION(373, 8229),
	STRONG_2_POTION(373, 8233),
	DAMAGE_2_POTION(373, 8236),
	REGEN_X_POTION(373, 8257),
	SPEED_X_POTION(373, 8258),
	FIRE_X_POTION(373, 8259),
	POISON_X_POTION(373, 8260),
	SIGHT_X_POTION(373, 8262),
	WEAK_X_POTION(373, 8264),
	STRONG_X_POTION(373, 8265),
	SLOW_X_POTION(373, 8266),
	INVIS_X_POTION(373, 8270),
	REGEN_SPLASH(373, 16385),
	SPEED_SPLASH(373, 16386),
	FIRE_SPLASH(373, 16387),
	POISON_SPLASH(373, 16388),
	HEAL_SPLASH(373, 16389),
	SIGHT_SPLASH(373, 16390),
	WEAK_SPLASH(373, 16392),
	STRONG_SPLASH(373, 16393),
	SLOW_SPLASH(373, 16394),
	DAMAGE_SPLASH(373, 16396),
	INVIS_SPLASH(373, 16398),
	REGEN_2_SPLASH(373, 16417),
	SPEED_2_SPLASH(373, 16418),
	POISON_2_SPLASH(373, 16420),
	HEAL_2_SPLASH(373, 16421),
	STRONG_2_SPLASH(373, 16425),
	DAMAGE_2_SPLASH(373, 16428),
	REGEN_X_SPLASH(373, 16449),
	SPEED_X_SPLASH(373, 16450),
	FIRE_X_SPLASH(373, 16451),
	POISON_X_SPLASH(373, 16452),
	SIGHT_X_SPLASH(373, 16454),
	WEAK_X_SPLASH(373, 16456),
	STRONG_X_SPLASH(373, 16457),
	SLOW_X_SPLASH(373, 16458),
	INVIS_X_SPLASH(373, 16462),
	GLASS_BOTTLE(374),
	SPIDER_EYE(375),
	FERMENTED_EYE(376),
	BLAZE_POWDER(377),
	MAGMA_CREAM(378),
	BREWSTAND(379),
	CAULDRON(380),
	EYE_OF_ENDER(381),
	GOLDEN_MELON(382),
	XP_BOTTLE(384),
	BOOK_AND_QUILL(386),
	EMERALD(388),
	ITEM_FRAME(389),
	FLOWER_POT(390),
	CARROT(391),
	POTATO(392),
	BAKED_POTATO(393),
	POISON_POTATO(394),
	MAP(395),
	GOLDEN_CARROT(396),
	WITHER_SKULL(397, 1),
	CARROT_ON_STICK(398),
	NETHER_STAR(399),
	PUMPKIN_PIE(400),
	COMPARATOR(404),
	NETHER_BRICK(405),
	RAW_QUARTZ(406),
	TNT_MINECART(407),
	HOPPER_MINECART(408),
	IRON_H_ARMOR(417),
	GOLD_H_ARMOR(418),
	DIAMOND_H_ARMOR(419),
	LEASH(420),
	NAMETAG(421),
	DISC_13(2256),
	DISC_CAT(2257),
	DISC_BLOCKS(2258),
	DISC_CHIRP(2259),
	DISC_FAR(2260),
	DISC_MALL(2261),
	DISC_MELLOHI(2262),
	DISC_STAL(2263),
	DISC_STRAD(2264),
	DISC_WARD(2265),
	DISC_11(2266),
	DISC_WAIT(2267);
	
	private final int id, data;
	private final static ItemStack items[] = new ItemStack[values().length];
	private final static Map<String, ShopItem> by_name = new HashMap<String, ShopItem>(values().length);
	
	private ShopItem(int id) {
		this(id, 0);
	}
	private ShopItem(int id, int data) {
		this.id = id;
		this.data = data;
	}
	
	static {
		for (ShopItem s : values()) {
			items[s.ordinal()] = s.itemStack();
			by_name.put(s.shopName(), s);
		}
	}
	
	/**
	 * Convert an {@link org.bukkit.inventory.ItemStack} into a valid name for shopping.
	 * @param item
	 * The ItemStack being evaluated
	 * @return
	 * A valid name for a shopping sign, or null if the item can't be sold
	 */
	public static String getShopName(ItemStack item) {
		ShopItem i = getByItem(item);
		if (i == null) { return null; }
		return i.shopName();
	}
	
	/**
	 * Identify the ShopItem associated with an {@link org.bukkit.inventory.ItemStack}.
	 * @param item
	 * The ItemStack being evaluated
	 * @return
	 * The ShopItem representing that item, or null if the item can't be sold
	 */
	public static ShopItem getByItem(ItemStack item) {
		for (ShopItem i : values()) {
			if (items[i.ordinal()].isSimilar(item)) {
				return i;
			}
		}
		return null;
	}
	
	public ItemStack createItems(int amount) {
		ItemStack created = itemStack();
		created.setAmount(amount);
		return created;
	}
	
	/**
	 * Determine whether an ItemStack matches this shop.
	 * @param item
	 * The ItemStack to compare
	 * @return
	 * Whether the shop trades that item
	 */
	public boolean itemMatch(ItemStack item) {
		return items[ordinal()].isSimilar(item);
	}
	
	public int stackSize() {
		return items[ordinal()].getMaxStackSize();
	}
	
	/**
	 * Identify the ShopItem associated with a name. Case insensitive.
	 * @param str
	 * The name being evaluated
	 * @return
	 * The ShopItem associated with that name, or null if the name isn't an item that can be sold
	 */
	public static ShopItem getByName(String str) {
		return by_name.get(str.toLowerCase());
	}
	
	/**
	 * Get the name, in lower case, associated with this ShopItem.
	 * @return
	 * A String containing the valid shop name
	 */
	public String shopName() {
		return name().toLowerCase().replaceAll("_", " ");
	}
	
	/**
	 * Get an {@link org.bukkit.inventory.ItemStack} matching this ShopItem.
	 * @return
	 * An ItemStack representing a single item of this type
	 */
	@SuppressWarnings("deprecation")
	private ItemStack itemStack() {
		if (id == 373) {
			return new ItemStack(id, 1, (short)data);
		} else {
			return new ItemStack(id, 1, (short)0, (byte)data);
		}
	}
}
