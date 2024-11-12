package enums;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	POPSICLE(33),
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
	APPLE_2(49),
	FIRE_IMMUNE(50),
	HYPER_GLOVE(51),
	HYPER_PUNCH(52),
	HYPER_KICK(53);

	static {
		fullList = new ArrayList<>(Arrays.asList(null, BOMB_UP, FIRE_UP, SPEED_UP, SPIKE_BOMB,
				REMOTE_BOMB, P_BOMB, LAND_MINE_BOMB, RUBBER_BOMB, FOLLOW_BOMB, MAGNET_BOMB,
				MAGMA_BOMB, HEART_BOMB, SENSOR_BOMB, SPIKE_REMOTE_BOMB, PASS_BOMB, PASS_BRICK,
				LINED_BOMBS, KICK_BOMB, PUNCH_BOMB, POWER_GLOVE, PUSH_POWER, EXTRA_LIVE,
				HEART_UP, ARMOR, TIME_STOP, ICECREAM, APPLE, ORANGE, BANANA, GOHAN, CAKE_SLICE,
				PICO_HAMMER, POPSICLE, SPIRAL_ICECREAM, SQUARED_CAKE_SLICE, FRENCH_FRIES,
				SPIRAL_COLORED_ICECREAM, PUDDING, CANDY_CONE, BUTTER, CORN_DOG, OLIVES,
				EXTINGUISHER, RANDOM, FIRE_MAX, SPEED_DOWN, CURSE_SKULL, STRAWBERRY_ICECREAM,
				APPLE_2, FIRE_IMMUNE, HYPER_GLOVE, HYPER_PUNCH, HYPER_KICK));
		bombs = new ArrayList<>(Arrays.asList(FOLLOW_BOMB, HEART_BOMB, MAGMA_BOMB,
				MAGNET_BOMB, P_BOMB, SENSOR_BOMB, LAND_MINE_BOMB, REMOTE_BOMB, RUBBER_BOMB,
				SPIKE_BOMB, SPIKE_REMOTE_BOMB));
		foods = new ArrayList<>(Arrays.asList(ICECREAM, APPLE, ORANGE, BANANA, GOHAN,
				CAKE_SLICE, POPSICLE, APPLE_2, SPIRAL_ICECREAM, SQUARED_CAKE_SLICE,
				FRENCH_FRIES, OLIVES, BUTTER, SPIRAL_COLORED_ICECREAM, PUDDING, CANDY_CONE,
				CORN_DOG, STRAWBERRY_ICECREAM));
		badItems = new ArrayList<>(Arrays.asList(SPEED_DOWN, CURSE_SKULL));
	}

	private int value;
	private static List<ItemType> fullList, bombs, foods, badItems;

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
		put(TIME_STOP, 50);
		put(ICECREAM, 1000);
		put(APPLE, 1000);
		put(ORANGE, 1000);
		put(BANANA, 1000);
		put(GOHAN, 1000);
		put(CAKE_SLICE, 1000);
		put(PICO_HAMMER, 1000);
		put(POPSICLE, 1000);
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
		put(RANDOM, 50);
		put(FIRE_MAX, 50);
		put(SPEED_DOWN, 50);
		put(CURSE_SKULL, 10);
		put(STRAWBERRY_ICECREAM, 1000);
		put(APPLE_2, 1000);
		put(FIRE_IMMUNE, 50);
		put(HYPER_GLOVE, 50);
		put(HYPER_PUNCH, 50);
		put(HYPER_KICK, 50);
	}};

	@SuppressWarnings("serial")
	private static Map<ItemType, Object[]> itemSound = new HashMap<>() {{
		put(BOMB_UP, new Object[] { 150, "/voices/Item-BombUp" });
		put(FIRE_UP, new Object[] { 150, "/voices/Item-FireUp" });
		put(SPEED_UP, new Object[] { 150, "/voices/Item-SpeedUp" });
		put(PASS_BRICK, new Object[] { 150, "/voices/Item-Special" });
		put(LINED_BOMBS, new Object[] { 150, "/voices/Item-Special" });
		put(KICK_BOMB, new Object[] { 150, "/voices/Item-BombKick" });
		put(PUNCH_BOMB, new Object[] { 150, "/voices/Item-Special" });
		put(POWER_GLOVE, new Object[] { 150, "/voices/Item-PowerGlove" });
		put(PUSH_POWER, new Object[] { 150, "/voices/Item-Special" });
		put(EXTRA_LIVE, new Object[] { 0, "LiveUp" });
		put(FIRE_MAX, new Object[] { 150, "/voices/Item-FireUp" });
		put(CURSE_SKULL, new Object[] { 0, "Curse" });
	}};

	ItemType(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

	public ItemType getNext() {
		int i = value + 1;
		if (i == fullList.size())
			i = 0;
		return fullList.get(i);
	}

	public ItemType getPreview() {
		int i = value - 1;
		if (i == 0)
			i = fullList.size() - 1;
		return fullList.get(i);
	}

	public static ItemType getRandom() {
		ItemType type;
		do {
			type = fullList.get((int) MyMath.getRandom(1, fullList.size() - 1));
		}
		while (type == RANDOM);
		return type;
	}

	public static ItemType getItemById(int itemId) {
		if (itemId < 1 || itemId >= fullList.size())
			throw new RuntimeException(itemId + " - Invalid item ID");
		return fullList.get(itemId);
	}

	public String getSound() {
		return !itemSound.containsKey(this) ? null : (String) itemSound.get(this)[1];
	}

	public Integer getSoundDelay() {
		return !itemSound.containsKey(this) ? 0 : (Integer) itemSound.get(this)[0];
	}

	public int getItemScore() {
		return itemScore.get(this);
	}

	public boolean isBomb() {
		return bombs.contains(this);
	}

	public boolean isFood() {
		return foods.contains(this);
	}
	
	public boolean isBadItem() {
		return badItems.contains(this);
	}

	public static BombType getBombTypeFromItemType(ItemType type) {
		return BombType.getBombTypeFromItemType(type);
	}

}
