package frameset_tags;

import frameset.Sprite;
import tools.GameMisc;

public class SetSprMotionBlurAngle extends FrameTag {
	
	private double value;
	
	public SetSprMotionBlurAngle(double value)
		{ this.value = value; }

	public double getValue()
		{ return value; }

	@Override
	public String toString()
		{ return "{" + FrameTag.getClassName(this) + ";" + value + "}"; }

	public SetSprMotionBlurAngle(String tags) {
		String[] params = FrameTag.validateStringTags(this, tags, 1);
		try
			{ value = Double.parseDouble(params[0]); }
		catch (Exception e)
			{ GameMisc.throwRuntimeException(params[0] + " - Invalid parameter"); }
	}

	@Override
	public SetSprMotionBlurAngle getNewInstanceOfThis()
		{ return new SetSprMotionBlurAngle(value); }
	
	@Override
	public void process(Sprite sprite)
		{ sprite.getEffects().getMotionBlur().setAngle(getValue()); }

}