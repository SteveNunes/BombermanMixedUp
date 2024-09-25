package frameset_tags;

import frameset.Sprite;
import tools.Tools;

public class SetSprColorTintBlue extends FrameTag {
	
	private double value;
	
	public SetSprColorTintBlue(double value)
		{ this.value = value; }

	public double getValue()
		{ return value; }

	@Override
	public String toString()
		{ return "{" + FrameTag.getClassName(this) + ";" + value + "}"; }

	public SetSprColorTintBlue(String tags) {
		String[] params = FrameTag.validateStringTags(this, tags, 1);
		try
			{ value = Double.parseDouble(params[0]); }
		catch (Exception e)
			{ throw new RuntimeException(params[0] + " - Invalid parameter"); }
	}

	@Override
	public SetSprColorTintBlue getNewInstanceOfThis()
		{ return new SetSprColorTintBlue(value); }
	
	@Override
	public void process(Sprite sprite)
		{ sprite.getEffects().getColorTint().setBlue(getValue()); }

}