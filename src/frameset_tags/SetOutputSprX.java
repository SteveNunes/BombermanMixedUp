package frameset_tags;

import frameset.Sprite;
import tools.Tools;

public class SetOutputSprX extends FrameTag {
	
	private double value;
	
	public SetOutputSprX(double value)
		{ this.value = value; }

	public double getValue()
		{ return value; }

	@Override
	public String toString()
		{ return "{" + FrameTag.getClassName(this) + ";" + value + "}"; }

	public SetOutputSprX(String tags) {
		String[] params = FrameTag.validateStringTags(this, tags, 1);
		try
			{ value = Double.parseDouble(params[0]); }
		catch (Exception e)
			{ throw new RuntimeException(params[0] + " - Invalid parameter"); }
	}

	@Override
	public SetOutputSprX getNewInstanceOfThis()
		{ return new SetOutputSprX(value); }
	
	@Override
	public void process(Sprite sprite)
		{ sprite.setX(getValue()); }

}