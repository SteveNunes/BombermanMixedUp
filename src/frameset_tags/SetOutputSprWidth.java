package frameset_tags;

import frameset.Sprite;
import tools.GameMisc;

public class SetOutputSprWidth extends FrameTag {
	
	private int value;
	
	public SetOutputSprWidth(int value)
		{ this.value = value; }

	public int getValue()
		{ return value; }

	@Override
	public String toString()
		{ return "{" + FrameTag.getClassName(this) + ";" + value + "}"; }

	public SetOutputSprWidth(String tags) {
		String[] params = FrameTag.validateStringTags(this, tags, 1);
		try
			{ value = Integer.parseInt(params[0]); }
		catch (Exception e)
			{ GameMisc.throwRuntimeException(params[0] + " - Invalid parameter"); }
	}

	@Override
	public SetOutputSprWidth getNewInstanceOfThis()
		{ return new SetOutputSprWidth(value); }
	
	@Override
	public void process(Sprite sprite)
		{ sprite.setOutputWidth(getValue()); }

}