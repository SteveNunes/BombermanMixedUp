package enums;

import util.CollectionUtils;

public enum ItemType {
	
	BOMB_UP(1),
	FIRE_UP(2),
	SPEED_UP(3),
	SPIKE_BOMB(4),
	REMOTE_BOMB(5),
	P_BOMB(6),
	LAND_MINE_BOMB(7),
	RUBBER_BOMB(8),
	FOLLOW_BOMB(9),
	MAGNET_BOMB(10),
	MAGMA_BOMB(11),
	HEART_BOMB(12),
	SENSOR_BOMB(13),
	SPIKE_REMOTE_BOMB(14),
	PASS_BOMB(15),
	PASS_WALL(16),
	LINED_BOMBS(17),
	KICK_BOMB(18),
	PUNCH_BOMB(19),
	POWER_GLOVE(20),
	PUSH_POWER(21),
	EXTRA_LIVE(22),
	HEART_UP(23),
	ARMOR(24),
	TIME_STOP(25),
	ICECREAM(26),
	APPLE(27),
	ORANGE(28),
	BANANA(29),
	GOHAN(30),
	CAKE_SLICE(31),
	PICO_HAMMER(32),
	POPSILE(33),
	SPIRAL_ICECREAM(34),
	SQUARED_CAKE_SLICE(35),
	FRENCH_FRIES(36),
	SPIRAL_COLORED_ICECREAM(37),
	PUDDING(38),
	CANDY_CONE(39),
	BUTTER(40),
	CORN_DOG(41),
	OLIVES(42),
	EXTINGUISHER(43),
	RANDOM(44),
	FIRE_MAX(45),
	SPEED_DOWN(46),
	CURSE_SKULL(47),
	STRAWBERRY_ICECREAM(48),
	FIRE_IMMUNE(49),
	APPLE_2(50);
	
	private int value;
	private static ItemType[] list = {null, BOMB_UP, FIRE_UP, SPEED_UP, SPIKE_BOMB, REMOTE_BOMB,
			P_BOMB, LAND_MINE_BOMB, RUBBER_BOMB, FOLLOW_BOMB, MAGNET_BOMB, MAGMA_BOMB, HEART_BOMB,
			SENSOR_BOMB, SPIKE_REMOTE_BOMB, PASS_BOMB, PASS_WALL, LINED_BOMBS, KICK_BOMB,
			PUNCH_BOMB, POWER_GLOVE, PUSH_POWER, EXTRA_LIVE, HEART_UP, ARMOR, TIME_STOP,
			ICECREAM, APPLE, ORANGE, BANANA, GOHAN, CAKE_SLICE, PICO_HAMMER, POPSILE,
			SPIRAL_ICECREAM, SQUARED_CAKE_SLICE, FRENCH_FRIES, SPIRAL_COLORED_ICECREAM,
			PUDDING, CANDY_CONE, BUTTER, CORN_DOG, OLIVES, EXTINGUISHER, RANDOM, FIRE_MAX,
			SPEED_DOWN, CURSE_SKULL, STRAWBERRY_ICECREAM, FIRE_IMMUNE, APPLE_2};
	
	ItemType(int value)
		{ this.value = value;	}
	
	public int getValue()
		{ return value; }
	
	public ItemType getNext() {
		int i = value + 1;
		if (i == list.length)
			i = 0;
		return list[i];
	}

	public ItemType getPreview() {
		int i = value - 1;
		if (i == 0)
			i = list.length - 1;
		return list[i];
	}
	
	public static ItemType getRandom()
		{ return CollectionUtils.getRandomItemFromArray(list); }
	
	public static ItemType getItemById(int itemId) {
		if (itemId < 1 || itemId >= list.length)
			throw new RuntimeException(itemId + " - Invalid item ID");
		return list[itemId];
	}
	
	public boolean isBomb() {
		return this == FOLLOW_BOMB || this == HEART_BOMB || this == MAGMA_BOMB ||
					 this == MAGNET_BOMB || this == LAND_MINE_BOMB || this == REMOTE_BOMB ||
					 this == RUBBER_BOMB || this == SENSOR_BOMB || this == SPIKE_BOMB ||
					 this == SPIKE_REMOTE_BOMB;	}
	
}
