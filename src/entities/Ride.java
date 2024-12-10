package entities;

import java.util.ArrayList;
import java.util.List;

public class Ride extends Entity {

	private static List<Ride> rideList = new ArrayList<>();

	public static List<Ride> getRides() {
		return rideList;
	}

}
