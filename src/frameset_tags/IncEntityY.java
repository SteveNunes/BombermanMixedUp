package frameset_tags;

import frameset.Sprite;

public class IncEntityY extends FrameTag {
	
	public int increment;
	
	public IncEntityY(int increment)
		{ this.increment = increment; }

	@Override
	public String toString()
		{ return "{" + getClassName(this) + ";" + increment + "}"; }

	public IncEntityY(String tags) {
		String[] params = validateStringTags(this, tags, 1);
		try
			{ increment = Integer.parseInt(params[0]); }
		catch (Exception e)
			{ throw new RuntimeException(params[0] + " - Invalid parameter"); }
	}

	@Override
	public IncEntityY getNewInstanceOfThis()
		{ return new IncEntityY(increment); }

	@Override
	public void process(Sprite sprite) {
		sprite.getSourceEntity().incY(increment);
	}

}
