package entities;

import java.util.Objects;

import enums.Direction;
import objmoveutils.Position;

public class TileCoord {
	
	private int x;
	private int y;
	
	public TileCoord()
		{ this(0, 0); }
	
	public TileCoord(TileCoord tilePos)
		{ this(tilePos.x, tilePos.y); }
	
	public TileCoord(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public TileCoord getNewInstance()	
		{ return new TileCoord(x, y); }

	public int getX()
		{ return x; }

	public void setX(int x)
		{ this.x = x; }

	public int getY()
	 { return y; }

	public void setY(int y)
		{ this.y = y;	}
	
	public void setCoord(int x, int y) {
		setX(x);
		setY(y);
	}
	
	public void setCoord(TileCoord tileCoord)
		{ setCoord(tileCoord.getX(), tileCoord.getY()); }

	public void incByDirection(Direction direction, int inc) {
		x += direction == Direction.LEFT ? -inc : direction == Direction.RIGHT ? inc : 0;
		y += direction == Direction.UP ? -inc : direction == Direction.DOWN ? inc : 0;
	}
	
	public void incByDirection(Direction direction)
		{ incByDirection(direction, 1); }
	
	public Position getTilePosition()
		{ return new Position(x, y); }
	
	public Position getPosition(int tileSize)
		{ return new Position(x * tileSize, y * tileSize); }

	@Override
	public int hashCode()
		{ return Objects.hash(x, y); }

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TileCoord other = (TileCoord) obj;
		return x == other.x && y == other.y;
	}

	@Override
	public String toString()
		{ return "TileCoord [" + x + "," + y + "]"; }

}
