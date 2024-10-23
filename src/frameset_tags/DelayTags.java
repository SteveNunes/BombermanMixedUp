package frameset_tags;

import frameset.Sprite;

public class DelayTags extends FrameTag {
	
	public int value;
	
	public DelayTags(int value)
		{ this.value = value; }

	@Override
	public String toString()
		{ return "{" + getClassName(this) + ";" + value + "}"; }

	public DelayTags(String tags) {
		String[] params = validateStringTags(this, tags, 1);
		try
			{ value = Integer.parseInt(params[0]); }
		catch (Exception e)
			{ throw new RuntimeException(params[0] + " - Invalid parameter"); }
	}

	@Override
	public DelayTags getNewInstanceOfThis()
		{ return new DelayTags(value); }

	@Override
	public void process(Sprite sprite) {}

}



