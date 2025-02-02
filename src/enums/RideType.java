package enums;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@SuppressWarnings("serial")
public enum RideType {
	
	BOMBER_SHIP(0),
	BLUE_LOUIE(1),
	YELLOW_LOUIE(2),
	PINK_LOUIE(3),
	DARK_PURPLE_LOUIE(4),
	GREEN_LOUIE(5),
	RED_LOUIE(6),
	DARK_LOUIE(7),
	IRON_BALL(10);
	
	private int value;
	
	private static Set<RideType> mechs = new HashSet<>(Set.of(IRON_BALL));
	private static Map<Integer, RideType> idToType = new HashMap<>() {{
		put(0, BOMBER_SHIP);
		put(1, BLUE_LOUIE);
		put(2, YELLOW_LOUIE);
		put(3, PINK_LOUIE);
		put(4, DARK_PURPLE_LOUIE);
		put(5, GREEN_LOUIE);
		put(6, RED_LOUIE);
		put(7, DARK_LOUIE);
		put(10, IRON_BALL);
	}};	
	
	private RideType(int value) {
		this.value = value;
	}
	
	public static RideType getRideTypeById(int id) {
		if (!idToType.containsKey(id))
			throw new RuntimeException(id + " - Invalid RideType ID");
		return idToType.get(id);
	}
	
	public boolean isMech() {
		return mechs.contains(this);
	}
	
	public int getValue() {
		return value;
	}

}
