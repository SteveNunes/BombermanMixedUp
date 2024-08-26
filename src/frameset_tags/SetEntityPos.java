package frameset_tags;

import entities.Entity;
import entities.FrameSet;
import entities.Sprite;

public class SetEntityPos extends FrameTag {
	
	private int x;
	private int y;
	
	public SetEntityPos(int x, int y) {
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

	public SetEntityPos(String tags) {
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
	public SetEntityPos getNewInstanceOfThis()
		{ return new SetEntityPos(x, y); }

	@Override
	public void process(Sprite sprite) {
		FrameSet frameSet = sprite.getMainFrameSet();
		Entity entity = frameSet.getEntity();
		entity.incX(getX());
		entity.incY(getY());
	}

}
