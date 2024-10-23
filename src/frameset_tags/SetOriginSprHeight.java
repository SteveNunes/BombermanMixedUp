package frameset_tags;

import frameset.Sprite;

public class SetOriginSprHeight extends FrameTag {
	
	public int value;
	
	public SetOriginSprHeight(int value)
		{ this.value = value; }

	@Override
	public String toString()
		{ return "{" + getClassName(this) + ";" + value + "}"; }

	public SetOriginSprHeight(String tags) {
		String[] params = validateStringTags(this, tags, 1);
		try
			{ value = Integer.parseInt(params[0]); }
		catch (Exception e)
			{ throw new RuntimeException(params[0] + " - Invalid parameter"); }
	}

	@Override
	public SetOriginSprHeight getNewInstanceOfThis()
		{ return new SetOriginSprHeight(value); }
	
	@Override
	public void process(Sprite sprite)
		{ sprite.setOriginSpriteHeight(value); }

}



