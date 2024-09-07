package frameset_tags;

import entities.Sprite;
import tools.GameMisc;

public class IncOutputSprHeight extends FrameTag {
	
	private int increment;
	
	public IncOutputSprHeight(int increment)
		{ this.increment = increment; }

	public int getIncrement()
		{ return increment; }

	@Override
	public String toString()
		{ return "{" + FrameTag.getClassName(this) + ";" + increment + "}"; }
	
	public IncOutputSprHeight(String tags) {
		String[] params = FrameTag.validateStringTags(this, tags, 1);
		try
			{ increment = Integer.parseInt(params[0]); }
		catch (Exception e)
			{ GameMisc.throwRuntimeException(params[0] + " - Invalid parameter"); }
	}

	@Override
	public IncOutputSprHeight getNewInstanceOfThis()
		{ return new IncOutputSprHeight(increment); }
	
	@Override
	public void process(Sprite sprite)
		{ sprite.incOutputHeight(getIncrement()); }

}
