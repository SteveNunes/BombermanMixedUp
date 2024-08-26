package frameset_tags;

import entities.Sprite;

public class SetOriginSprWidth extends FrameTag {
	
	private int value;
	
	public SetOriginSprWidth(int value)
		{ this.value = value; }

	public int getValue()
		{ return value; }

	@Override
	public String toString()
		{ return "{" + FrameTag.getClassName(this) + ";" + value + "}"; }

	public SetOriginSprWidth(String tags) {
		String[] params = FrameTag.validateStringTags(this, tags, 1);
		try
			{ value = Integer.parseInt(params[0]); }
		catch (Exception e)
			{ throw new RuntimeException(params[0] + " - Invalid parameter"); }
	}

	@Override
	public SetOriginSprWidth getNewInstanceOfThis()
		{ return new SetOriginSprWidth(value); }
	
	@Override
	public void process(Sprite sprite)
		{ sprite.setOriginSpriteWidth(getValue()); }

	@Override
	public void reset() {
	}

}