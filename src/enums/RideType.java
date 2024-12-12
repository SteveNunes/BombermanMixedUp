package enums;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@SuppressWarnings("serial")
public enum RideType {
	
	BOMBER_SHIP(0),
	BLUE_LOUIE(1),
	IRON_BALL(10);
	
	private int value;
	
	private static Set<RideType> mechs = new HashSet<>(Set.of(IRON_BALL));
	private static Map<Integer, RideType> idToType = new HashMap<>() {{
		put(0, BOMBER_SHIP);
		put(1, BLUE_LOUIE);
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
