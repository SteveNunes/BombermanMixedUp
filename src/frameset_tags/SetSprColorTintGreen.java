package frameset_tags;

import frameset.Sprite;

public class SetSprColorTintGreen extends FrameTag {
	
	public double value;
	
	public SetSprColorTintGreen(double value)
		{ this.value = value; }

	@Override
	public String toString()
		{ return "{" + FrameTag.getClassName(this) + ";" + value + "}"; }

	public SetSprColorTintGreen(String tags) {
		String[] params = FrameTag.validateStringTags(this, tags, 1);
		try
			{ value = Double.parseDouble(params[0]); }
		catch (Exception e)
			{ throw new RuntimeException(params[0] + " - Invalid parameter"); }
	}

	@Override
	public SetSprColorTintGreen getNewInstanceOfThis()
		{ return new SetSprColorTintGreen(value); }
	
	@Override
	public void process(Sprite sprite)
		{ sprite.getEffects().getColorTint().setGreen(value); }

}



