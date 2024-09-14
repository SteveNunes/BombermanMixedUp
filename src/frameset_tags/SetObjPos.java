package frameset_tags;

import frameset.FrameSet;
import frameset.Sprite;
import tools.GameMisc;

public class SetObjPos extends FrameTag {
	
	private int x;
	private int y;
	
	public SetObjPos(int x, int y) {
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

	public SetObjPos(String tags) {
		String[] params = FrameTag.validateStringTags(this, tags, 2);
		int n = 0;
		try {
			x = Integer.parseInt(params[n++]);
			y = Integer.parseInt(params[n++]);
		}
		catch (Exception e)
			{ GameMisc.throwRuntimeException(params[--n] + " - Invalid parameter"); }
	}

	@Override
	public SetObjPos getNewInstanceOfThis()
		{ return new SetObjPos(x, y); }

	@Override
	public void process(Sprite sprite) {
		FrameSet frameSet = sprite.getMainFrameSet();
		frameSet.setPosition(getX(), getY());
	}

}
