package entities;

import java.util.ArrayList;
import java.util.List;

public class Monster extends Entity {

	private static List<Monster> monsterList = new ArrayList<>();

	public static List<Monster> getMonsters() {
		return monsterList;
	}

}
