package frameset_tags;

import entities.Sprite;
import tools.GameMisc;

public class SetSprRotate extends FrameTag {
	
	private int value;
	
	public SetSprRotate(int value)
		{ this.value = value; }

	public int getValue()
		{ return value; }

	@Override
	public String toString()
		{ return "{" + FrameTag.getClassName(this) + ";" + value + "}"; }

	public SetSprRotate(String tags) {
		String[] params = FrameTag.validateStringTags(this, tags, 1);
		try
			{ value = Integer.parseInt(params[0]); }
		catch (Exception e)
			{ GameMisc.throwRuntimeException(params[0] + " - Invalid parameter"); }
	}

	@Override
	public SetSprRotate getNewInstanceOfThis()
		{ return new SetSprRotate(value); }
	
	@Override
	public void process(Sprite sprite)
		{ sprite.setRotation(getValue()); }

}