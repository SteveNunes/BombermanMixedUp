package tools;

import enums.Direction;
import enums.FindType;
import objmoveutils.TileCoord;

public class FindProps {
	
	private TileCoord coord;
	private Direction dir;
	private FindType foundType;
	private boolean isAcessible;
	
	public FindProps(FindType foundType, TileCoord coord, Direction dir, boolean isAcessible) {
		this.coord = coord;
		this.dir = dir;
		this.foundType = foundType;
		this.isAcessible = isAcessible;
	}

	public TileCoord getCoord() {
		return coord;
	}

	public Direction getDir() {
		return dir;
	}

	public FindType getFoundType() {
		return foundType;
	}
	
	public boolean isAcessible() {
		return isAcessible;
	}

}
