package frameset_tags;

import entities.TileCoord;

public class TileCoord2 extends TileCoord {

	private int offsetX;
	private int offsetY;
	
	public TileCoord2()
		{ this(0, 0, 0, 0); }
	
	public TileCoord2(TileCoord2 tileCoord2)
		{ this(tileCoord2.getX(), tileCoord2.getY(), tileCoord2.offsetX, tileCoord2.offsetY); }
	
	public TileCoord2(int x, int y, int offsetX, int offsetY) {
		super(x, y);
		this.offsetX = offsetX;
		this.offsetY = offsetY;
	}
	
	public TileCoord2 getNewInstance()	
		{ return new TileCoord2(getX(), getY(), offsetX, offsetY); }
	
	public int getOffsetX()
		{ return offsetX; }

	public void setOffsetX(int offsetX)
		{ this.offsetX = offsetX; }

	public int getOffsetY()
	 { return offsetY; }

	public void setOffsetY(int offsetY)
		{ this.offsetY = offsetY;	}
	
	public void setOffset(int offsetX, int offsetY) {
		setOffsetX(offsetX);
		setOffsetY(offsetY);
	}
	
	public void setOffset(TileCoord2 tileCoord2)
		{ setOffset(tileCoord2.getOffsetX(), tileCoord2.getOffsetY()); }
	
	public TileCoord getTileCoord()
		{ return (TileCoord)this; }

	@Override
	public String toString()
		{ return "TileCoord2 [" + getX() + "," + getY() + "] [" + offsetX + "," + offsetY + "]"; }

}
