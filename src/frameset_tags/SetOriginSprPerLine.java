package frameset_tags;

import entities.Sprite;

public class SetOriginSprPerLine extends FrameTag {
	
	private int value;
	
	public SetOriginSprPerLine(int value)
		{ this.value = value; }

	public int getValue()
		{ return value; }

	@Override
	public String toString()
		{ return "{" + FrameTag.getClassName(this) + ";" + value + "}"; }

	public SetOriginSprPerLine(String tags) {
		String[] params = FrameTag.validateStringTags(this, tags, 1);
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
		{ sprite.setSpritesPerLine(getValue()); }

	@Override
	public void reset() {
	}

}
