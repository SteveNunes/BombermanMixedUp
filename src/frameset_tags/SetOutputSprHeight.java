package frameset_tags;

import frameset.Sprite;
import tools.GameMisc;

public class SetOutputSprHeight extends FrameTag {
	
	private int value;
	
	public SetOutputSprHeight(int value)
		{ this.value = value; }

	public int getValue()
		{ return value; }

	@Override
	public String toString()
		{ return "{" + FrameTag.getClassName(this) + ";" + value + "}"; }

	public SetOutputSprHeight(String tags) {
		String[] params = FrameTag.validateStringTags(this, tags, 1);
		try
			{ value = Integer.parseInt(params[0]); }
		catch (Exception e)
			{ GameMisc.throwRuntimeException(params[0] + " - Invalid parameter"); }
	}

	@Override
	public SetOutputSprHeight getNewInstanceOfThis()
		{ return new SetOutputSprHeight(value); }
	
	@Override
	public void process(Sprite sprite)
		{ sprite.setOutputHeight(getValue()); }

}