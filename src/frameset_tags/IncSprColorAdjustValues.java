package frameset_tags;

import entities.Sprite;

public class IncSprColorAdjustValues extends FrameTag {
	
	private double incrementHue;
	private double incrementSaturation;
	private double incrementBrightness;
	
	public IncSprColorAdjustValues(double incrementHue, double incrementSaturation, double incrementBrightness) {
		this.incrementHue = incrementHue;
		this.incrementSaturation = incrementSaturation;
		this.incrementBrightness = incrementBrightness;
	}

	public double getIncrementHue()
		{ return incrementHue; }

	public double getIncrementSaturation()
		{ return incrementSaturation; }
		
	public double getIncrementBrightness()
		{ return incrementBrightness; }
	

	@Override
	public String toString()
		{ return "{" + FrameTag.getClassName(this) + ";" + incrementHue + ";" + incrementSaturation + ";" + incrementBrightness + "}"; }
	
	public IncSprColorAdjustValues(String tags) {
		String[] params = FrameTag.validateStringTags(this, tags, 3);
		int n = 0;
		try {
			incrementHue = Double.parseDouble(params[n++]);
			incrementSaturation = Double.parseDouble(params[n++]);
			incrementBrightness = Double.parseDouble(params[n++]);
		}
		catch (Exception e)
			{ throw new RuntimeException(params[--n] + " - Invalid parameter"); }
	}

	@Override
	public IncSprColorAdjustValues getNewInstanceOfThis()
		{ return new IncSprColorAdjustValues(incrementHue, incrementSaturation, incrementBrightness); }
	
	@Override
	public void process(Sprite sprite) {
		sprite.getEffects().getColorAdjust().incValues(getIncrementHue(),
																									 getIncrementSaturation(),
																									 getIncrementBrightness());
	}

	@Override
	public void reset() {
	}

}
