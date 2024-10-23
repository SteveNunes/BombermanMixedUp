package frameset_tags;

import frameset.Sprite;

public class SetSprColorTintRed extends FrameTag {
	
	public double value;
	
	public SetSprColorTintRed(double value)
		{ this.value = value; }

	@Override
	public String toString()
		{ return "{" + getClassName(this) + ";" + value + "}"; }

	public SetSprColorTintRed(String tags) {
		String[] params = validateStringTags(this, tags, 1);
		try
			{ value = Double.parseDouble(params[0]); }
		catch (Exception e)
			{ throw new RuntimeException(params[0] + " - Invalid parameter"); }
	}

	@Override
	public SetSprColorTintRed getNewInstanceOfThis()
		{ return new SetSprColorTintRed(value); }
	
	@Override
	public void process(Sprite sprite)
		{ sprite.getEffects().getColorTint().setRed(value); }

}



