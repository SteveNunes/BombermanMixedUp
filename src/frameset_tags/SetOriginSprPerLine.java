package frameset_tags;

import frameset.Sprite;

public class SetOriginSprPerLine extends FrameTag {
	
	public int value;
	
	public SetOriginSprPerLine(int value)
		{ this.value = value; }

	@Override
	public String toString()
		{ return "{" + getClassName(this) + ";" + value + "}"; }

	public SetOriginSprPerLine(String tags) {
		String[] params = validateStringTags(this, tags, 1);
		try
			{ value = Integer.parseInt(params[0]); }
		catch (Exception e)
			{ throw new RuntimeException(params[0] + " - Invalid parameter"); }
	}

	@Override
	public SetOriginSprPerLine getNewInstanceOfThis()
		{ return new SetOriginSprPerLine(value); }
	
	@Override
	public void process(Sprite sprite)
		{ sprite.setSpritesPerLine(value); }

}



