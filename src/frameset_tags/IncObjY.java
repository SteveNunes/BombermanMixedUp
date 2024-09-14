package frameset_tags;

import frameset.FrameSet;
import frameset.Sprite;
import tools.GameMisc;

public class IncObjY extends FrameTag {
	
	private int increment;
	
	public IncObjY(int increment)
		{ this.increment = increment; }

	public int getIncrement()
		{ return increment; }

	@Override
	public String toString()
		{ return "{" + FrameTag.getClassName(this) + ";" + increment + "}"; }

	public IncObjY(String tags) {
		String[] params = FrameTag.validateStringTags(this, tags, 1);
		try
			{ increment = Integer.parseInt(params[0]); }
		catch (Exception e)
			{ GameMisc.throwRuntimeException(params[0] + " - Invalid parameter"); }
	}

	@Override
	public IncObjY getNewInstanceOfThis()
		{ return new IncObjY(increment); }

	@Override
	public void process(Sprite sprite) {
		FrameSet frameSet = sprite.getMainFrameSet();
		frameSet.incY(getIncrement());
	}

}
