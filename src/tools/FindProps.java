package tools;

import enums.Direction;
import objmoveutils.TileCoord;

public class FindProps {
	
	private TileCoord coord;
	private Direction dir;
	
	public FindProps(TileCoord coord, Direction dir) {
		this.coord = coord;
		this.dir = dir;
	}

	public TileCoord getCoord() {
		return coord;
	}

	public Direction getDir() {
		return dir;
	}

}
