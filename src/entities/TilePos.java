package entities;

import java.util.Objects;

public class TilePos {
	
	private int x;
	private int y;
	
	public TilePos(TilePos tilePos)
		{ this(tilePos.x, tilePos.y); }
	
	public TilePos(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public int getX()
		{ return x; }

	public void setX(int x)
	 { this.x = x; }

	public int getY()
	 { return y; }

	public void setY(int y)
	 { this.y = y; }
	
	public void setPos(int x, int y) {
		setX(x);
		setY(y);
	}

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
		TilePos other = (TilePos) obj;
		return x == other.x && y == other.y;
	}

}
