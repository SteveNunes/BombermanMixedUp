package maps;

import entities.Entity;
import objmoveutils.Position;

public class Portal extends Entity {

	private int spawnMobsUponExplodesQuantity;
	private Position tilePosition;
	private String activationSound;

	public void setSpawnMobsUponExplodesQuantity(int value) {
		spawnMobsUponExplodesQuantity = value;
	}

}
