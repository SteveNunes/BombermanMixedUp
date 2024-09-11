package maps;

import java.util.ArrayList;
import java.util.List;

import enums.PortalCriteria;
import objmoveutils.Position;

public class Portal {

	private String originMapSet;
	private int spawnMobsUponExplodesQuantity;
	private List<PortalCriteria> portalDisplayCriterias;
	private List<PortalCriteria> portalJoinCriterias;
	private Position tilePosition;
	private String activationSound;
	
	public Portal(String oroginMapSet, Position tilePosition) {
		this.originMapSet = oroginMapSet;
		this.tilePosition = new Position(tilePosition);
		spawnMobsUponExplodesQuantity = 0;
		portalDisplayCriterias = new ArrayList<>();
		portalJoinCriterias = new ArrayList<>();
	}
	
	public void setSpawnMobsUponExplodesQuantity(int value)
		{ spawnMobsUponExplodesQuantity = value; }
	
	public void addPortalDisplayCriterias(PortalCriteria criteria) {
		if (!portalDisplayCriterias.contains(criteria))
			portalDisplayCriterias.add(criteria);
	}
	
	public void removePortalDisplayCriterias(PortalCriteria criteria) {
		if (portalDisplayCriterias.contains(criteria))
			portalDisplayCriterias.remove(criteria);
	}
	
	public void addPortalJoinCriterias(PortalCriteria criteria) {
		if (!portalJoinCriterias.contains(criteria))
			portalJoinCriterias.add(criteria);
	}
	
	public void removePortalJoinCriterias(PortalCriteria criteria) {
		if (portalJoinCriterias.contains(criteria))
			portalJoinCriterias.remove(criteria);
	}
	
}
