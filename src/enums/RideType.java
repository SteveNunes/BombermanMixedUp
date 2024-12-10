package enums;

import java.util.HashSet;
import java.util.Set;

public enum RideType {
	
	BLUE_LOUIE(0),
	IRON_BALL(10);
	
	private int value;
	
	private static Set<RideType> mechs = new HashSet<>(Set.of(IRON_BALL));
	
	private RideType(int value) {
		this.value = value;
	}
	
	public boolean isMech() {
		return mechs.contains(this);
	}
	
	public int getValue() {
		return value;
	}

}
