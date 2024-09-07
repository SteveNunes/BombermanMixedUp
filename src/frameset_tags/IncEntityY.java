package frameset_tags;

import entities.Entity;
import entities.FrameSet;
import entities.Sprite;
import tools.GameMisc;

public class IncEntityY extends FrameTag {
	
	private int increment;
	
	public IncEntityY(int increment)
		{ this.increment = increment; }

	public int getIncrement()
		{ return increment; }

	@Override
	public String toString()
		{ return "{" + FrameTag.getClassName(this) + ";" + increment + "}"; }

	public IncEntityY(String tags) {
		String[] params = FrameTag.validateStringTags(this, tags, 1);
		try
			{ increment = Integer.parseInt(params[0]); }
		catch (Exception e)
			{ GameMisc.throwRuntimeException(params[0] + " - Invalid parameter"); }
	}

	@Override
	public IncEntityY getNewInstanceOfThis()
		{ return new IncEntityY(increment); }

	@Override
	public void process(Sprite sprite) {
		FrameSet frameSet = sprite.getMainFrameSet();
		Entity entity = frameSet.getEntity();
		entity.incY(getIncrement());
	}

}
