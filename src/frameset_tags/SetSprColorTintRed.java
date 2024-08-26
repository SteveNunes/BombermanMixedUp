package frameset_tags;

import entities.Sprite;

public class SetSprColorTintRed extends FrameTag {
	
	private double value;
	
	public SetSprColorTintRed(double value)
		{ this.value = value; }

	public double getValue()
		{ return value; }

	@Override
	public String toString()
		{ return "{" + FrameTag.getClassName(this) + ";" + value + "}"; }

	public SetSprColorTintRed(String tags) {
		String[] params = FrameTag.validateStringTags(this, tags, 1);
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
		{ sprite.getEffects().getColorTint().setRed(getValue()); }

	@Override
	public void reset() {
	}

}