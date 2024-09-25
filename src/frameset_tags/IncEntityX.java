package frameset_tags;

import entities.Entity;
import frameset.FrameSet;
import frameset.Sprite;
import tools.Tools;

public class IncEntityX extends FrameTag {
	
	private int increment;
	
	public IncEntityX(int increment)
		{ this.increment = increment; }

	public int getIncrement()
		{ return increment; }

	@Override
	public String toString()
		{ return "{" + FrameTag.getClassName(this) + ";" + increment + "}"; }

	public IncEntityX(String tags) {
		String[] params = FrameTag.validateStringTags(this, tags, 1);
		try
			{ increment = Integer.parseInt(params[0]); }
		catch (Exception e)
			{ throw new RuntimeException(params[0] + " - Invalid parameter"); }
	}

	@Override
	public IncEntityX getNewInstanceOfThis()
		{ return new IncEntityX(increment); }

	@Override
	public void process(Sprite sprite) {
		FrameSet frameSet = sprite.getMainFrameSet();
		Entity entity = frameSet.getEntity();
		entity.incX(getIncrement());
	}

}
