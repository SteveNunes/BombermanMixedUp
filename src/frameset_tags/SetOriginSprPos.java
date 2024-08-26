package frameset_tags;

import entities.Sprite;

public class SetOriginSprPos extends FrameTag {
	
	private int x;
	private int y;
	
	public SetOriginSprPos(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public int getX()
		{ return x; }

	public int getY()
		{ return y; }	

	@Override
	public String toString()
		{ return "{" + FrameTag.getClassName(this) + ";" + x + ";" + y + "}"; }

	public SetOriginSprPos(String tags) {
		String[] params = FrameTag.validateStringTags(this, tags, 2);
		int n = 0;
		try {
			x = Integer.parseInt(params[n++]);
			y = Integer.parseInt(params[n++]);
		}
		catch (Exception e)
			{ throw new RuntimeException(params[--n] + " - Invalid parameter"); }
	}

	@Override
	public SetOriginSprPos getNewInstanceOfThis()
		{ return new SetOriginSprPos(x, y); }

	@Override
	public void process(Sprite sprite) {
		sprite.setOriginSpriteX(getX());
		sprite.setOriginSpriteY(getY());
	}

}