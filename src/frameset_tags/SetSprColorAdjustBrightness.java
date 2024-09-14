package frameset_tags;

import frameset.Sprite;
import tools.GameMisc;

public class SetSprColorAdjustBrightness extends FrameTag {
	
	private double value;
	
	public SetSprColorAdjustBrightness(double value)
		{ this.value = value; }

	public double getValue()
		{ return value; }

	@Override
	public String toString()
		{ return "{" + FrameTag.getClassName(this) + ";" + value + "}"; }

	public SetSprColorAdjustBrightness(String tags) {
		String[] params = FrameTag.validateStringTags(this, tags, 1);
		try
			{ value = Double.parseDouble(params[0]); }
		catch (Exception e)
			{ GameMisc.throwRuntimeException(params[0] + " - Invalid parameter"); }
	}

	@Override
	public SetSprColorAdjustBrightness getNewInstanceOfThis()
		{ return new SetSprColorAdjustBrightness(value); }
	
	@Override
	public void process(Sprite sprite)
		{ sprite.getEffects().getColorAdjust().setBrightness(getValue()); }

}