package frameset_tags;

import entities.Sprite;

public class IncOriginSprPerLine extends FrameTag {
	
	private int increment;
	
	public IncOriginSprPerLine(int increment)
		{ this.increment = increment; }

	public int getIncrement()
		{ return increment; }

	@Override
	public String toString()
		{ return "{" + FrameTag.getClassName(this) + ";" + increment + "}"; }

	public IncOriginSprPerLine(String tags) {
		String[] params = FrameTag.validateStringTags(this, tags, 1);
		try
			{ increment = Integer.parseInt(params[0]); }
		catch (Exception e)
			{ throw new RuntimeException(params[0] + " - Invalid parameter"); }
	}

	@Override
	public IncOriginSprPerLine getNewInstanceOfThis()
		{ return new IncOriginSprPerLine(increment); }
	
	@Override
	public void process(Sprite sprite)
		{ sprite.incSpritesPerLine(getIncrement()); }

}
