package frameset_tags;

import entities.Sprite;

public class SetSprIndex extends FrameTag {
	
	private int value;
	
	public SetSprIndex(int value)
		{ this.value = value; }

	public int getValue()
		{ return value; }

	@Override
	public String toString()
		{ return "{" + FrameTag.getClassName(this) + ";" + value + "}"; }

	public SetSprIndex(String tags) {
		String[] params = FrameTag.validateStringTags(this, tags, 1);
		try
			{ value = Integer.parseInt(params[0]); }
		catch (Exception e)
			{ throw new RuntimeException(params[0] + " - Invalid parameter"); }
	}

	@Override
	public SetSprIndex getNewInstanceOfThis()
		{ return new SetSprIndex(value); }
	
	@Override
	public void process(Sprite sprite)
		{ sprite.setSpriteIndex(getValue()); }

}