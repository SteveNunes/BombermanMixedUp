package maps;

import objmoveutils.Position;

public class Portal {

	private int spawnMobsUponExplodesQuantity;
	private Position tilePosition;
	private String activationSound;
	
	public Portal(Position tilePosition) {
		this.tilePosition = new Position(tilePosition);
		spawnMobsUponExplodesQuantity = 0;
	}
	
	public void setSpawnMobsUponExplodesQuantity(int value)
		{ spawnMobsUponExplodesQuantity = value; }
	
}
