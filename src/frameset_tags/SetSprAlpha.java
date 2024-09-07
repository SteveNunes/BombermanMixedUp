package frameset_tags;

import entities.Sprite;
import tools.GameMisc;

public class SetSprAlpha extends FrameTag {
	
	private double value;
	
	public SetSprAlpha(double value)
		{ this.value = value; }

	public double getValue()
		{ return value; }	

	@Override
	public String toString()
		{ return "{" + FrameTag.getClassName(this) + ";" + value + "}"; }

	public SetSprAlpha(String tags) {
		String[] params = FrameTag.validateStringTags(this, tags, 1);
		try
			{ value = Double.parseDouble(params[0]); }
		catch (Exception e)
			{ GameMisc.throwRuntimeException(params[0] + " - Invalid parameter"); }
	}

	@Override
	public SetSprAlpha getNewInstanceOfThis()
		{ return new SetSprAlpha(value); }
	
	@Override
	public void process(Sprite sprite)
		{ sprite.setAlpha(getValue()); }

}