package frameset_tags;

import frameset.Sprite;
import tools.Tools;

public class IncSprRotate extends FrameTag {
	
	private int increment;
	
	public IncSprRotate(int increment)
		{ this.increment = increment; }

	public int getIncrement()
		{ return increment; }

	@Override
	public String toString()
		{ return "{" + FrameTag.getClassName(this) + ";" + increment + "}"; }
	
	public IncSprRotate(String tags) {
		String[] params = FrameTag.validateStringTags(this, tags, 1);
		try
			{ increment = Integer.parseInt(params[0]); }
		catch (Exception e)
			{ throw new RuntimeException(params[0] + " - Invalid parameter"); }
	}

	@Override
	public IncSprRotate getNewInstanceOfThis()
		{ return new IncSprRotate(increment); }
	
	@Override
	public void process(Sprite sprite)
		{ sprite.incRotation(getIncrement()); }

}
