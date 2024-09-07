package frameset_tags;

import entities.Sprite;
import tools.GameMisc;

public class SetOriginSprHeight extends FrameTag {
	
	private int value;
	
	public SetOriginSprHeight(int value)
		{ this.value = value; }

	public int getValue()
		{ return value; }

	@Override
	public String toString()
		{ return "{" + FrameTag.getClassName(this) + ";" + value + "}"; }

	public SetOriginSprHeight(String tags) {
		String[] params = FrameTag.validateStringTags(this, tags, 1);
		try
			{ value = Integer.parseInt(params[0]); }
		catch (Exception e)
			{ GameMisc.throwRuntimeException(params[0] + " - Invalid parameter"); }
	}

	@Override
	public SetOriginSprHeight getNewInstanceOfThis()
		{ return new SetOriginSprHeight(value); }
	
	@Override
	public void process(Sprite sprite)
		{ sprite.setOriginSpriteHeight(getValue()); }

}