package frameset_tags;

import entities.Sprite;

public class SetSprBloomThreshold extends FrameTag {
	
	private double value;
	
	public SetSprBloomThreshold(double value)
		{ this.value = value; }

	public double getValue()
		{ return value; }	
	
	@Override
	public String toString()
		{ return "{" + FrameTag.getClassName(this) + ";" + value + "}"; }

	public SetSprBloomThreshold(String tags) {
		String[] params = FrameTag.validateStringTags(this, tags, 1);
		try
			{ value = Double.parseDouble(params[0]); }
		catch (Exception e)
			{ throw new RuntimeException(params[0] + " - Invalid parameter"); }
	}

	@Override
	public SetSprBloomThreshold getNewInstanceOfThis()
		{ return new SetSprBloomThreshold(value); }
	
	@Override
	public void process(Sprite sprite)
		{ sprite.getEffects().getBloom().setThreshold(getValue()); }

}