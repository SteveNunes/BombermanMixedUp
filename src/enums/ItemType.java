package enums;

import java.util.HashMap;
import java.util.Map;

import util.CollectionUtils;
import util.MyMath;

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
	PASS_BRICK(16),
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
	private static ItemType[] list = {null, RANDOM, BOMB_UP, FIRE_UP, SPEED_UP, SPIKE_BOMB, REMOTE_BOMB,
			P_BOMB, LAND_MINE_BOMB, RUBBER_BOMB, FOLLOW_BOMB, MAGNET_BOMB, MAGMA_BOMB, HEART_BOMB,
			SENSOR_BOMB, SPIKE_REMOTE_BOMB, PASS_BOMB, PASS_BRICK, LINED_BOMBS, KICK_BOMB,
			PUNCH_BOMB, POWER_GLOVE, PUSH_POWER, EXTRA_LIVE, HEART_UP, ARMOR, TIME_STOP,
			ICECREAM, APPLE, ORANGE, BANANA, GOHAN, CAKE_SLICE, PICO_HAMMER, POPSILE,
			SPIRAL_ICECREAM, SQUARED_CAKE_SLICE, FRENCH_FRIES, SPIRAL_COLORED_ICECREAM,
			PUDDING, CANDY_CONE, BUTTER, CORN_DOG, OLIVES, EXTINGUISHER, FIRE_MAX,
			SPEED_DOWN, CURSE_SKULL, STRAWBERRY_ICECREAM, FIRE_IMMUNE, APPLE_2};
	
	@SuppressWarnings("serial")
	private static Map<ItemType, Integer> itemScore = new HashMap<>() {{
		put(BOMB_UP, 50);
		put(FIRE_UP, 50);
		put(SPEED_UP, 50);
		put(SPIKE_BOMB, 50);
		put(REMOTE_BOMB, 50);
		put(P_BOMB, 50);
		put(LAND_MINE_BOMB, 50);
		put(RUBBER_BOMB, 50);
		put(FOLLOW_BOMB, 50);
		put(MAGNET_BOMB, 50);
		put(MAGMA_BOMB, 50);
		put(HEART_BOMB, 50);
		put(SENSOR_BOMB, 50);
		put(SPIKE_REMOTE_BOMB, 50);
		put(PASS_BOMB, 50);
		put(PASS_BRICK, 50);
		put(LINED_BOMBS, 50);
		put(KICK_BOMB, 50);
		put(PUNCH_BOMB, 50);
		put(POWER_GLOVE, 50);
		put(PUSH_POWER, 50);
		put(EXTRA_LIVE, 50);
		put(HEART_UP, 50);
		put(ARMOR, 50);
		put(TIME_STOP, 1000);
		put(ICECREAM, 1000);
		put(APPLE, 1000);
		put(ORANGE, 1000);
		put(BANANA, 1000);
		put(GOHAN, 1000);
		put(CAKE_SLICE, 1000);
		put(PICO_HAMMER, 1000);
		put(POPSILE, 1000);
		put(SPIRAL_ICECREAM, 1000);
		put(SQUARED_CAKE_SLICE, 1000);
		put(FRENCH_FRIES, 1000);
		put(SPIRAL_COLORED_ICECREAM, 1000);
		put(PUDDING, 1000);
		put(CANDY_CONE, 1000);
		put(BUTTER, 1000);
		put(CORN_DOG, 1000);
		put(OLIVES, 1000);
		put(EXTINGUISHER, 1000);
		put(RANDOM, 1000);
		put(FIRE_MAX, 1000);
		put(SPEED_DOWN, 1000);
		put(CURSE_SKULL, 1000);
		put(STRAWBERRY_ICECREAM, 1000);
		put(FIRE_IMMUNE, 1000);
		put(APPLE_2, 1000);
	}};
	
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
		{ return list[(int)MyMath.getRandom(2, list.length - 1)]; }
	
	public static ItemType getItemById(int itemId) {
		if (itemId < 1 || itemId >= list.length)
			throw new RuntimeException(itemId + " - Invalid item ID");
		return list[itemId];
	}
	
	public int getItemScore()
		{ return itemScore.get(this); }
	
	public boolean isBomb() {
		return this == FOLLOW_BOMB || this == HEART_BOMB || this == MAGMA_BOMB ||
					 this == MAGNET_BOMB || this == LAND_MINE_BOMB || this == REMOTE_BOMB ||
					 this == RUBBER_BOMB || this == SENSOR_BOMB || this == SPIKE_BOMB ||
					 this == SPIKE_REMOTE_BOMB;	}
	
	public static BombType getBombTypeFromItemType(ItemType type)
		{	return BombType.getBombTypeFromItemType(type); }
	
}
