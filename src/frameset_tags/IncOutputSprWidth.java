package frameset_tags;

import entities.Sprite;
import tools.GameMisc;

public class IncOutputSprWidth extends FrameTag {
	
	private int increment;
	
	public IncOutputSprWidth(int increment)
		{ this.increment = increment; }

	public int getIncrement()
		{ return increment; }

	@Override
	public String toString()
		{ return "{" + FrameTag.getClassName(this) + ";" + increment + "}"; }
	
	public IncOutputSprWidth(String tags) {
		String[] params = FrameTag.validateStringTags(this, tags, 1);
		try
			{ increment = Integer.parseInt(params[0]); }
		catch (Exception e)
			{ GameMisc.throwRuntimeException(params[0] + " - Invalid parameter"); }
	}

	@Override
	public IncOutputSprWidth getNewInstanceOfThis()
		{ return new IncOutputSprWidth(increment); }
	
	@Override
	public void process(Sprite sprite)
		{ sprite.incOutputWidth(getIncrement()); }

}
