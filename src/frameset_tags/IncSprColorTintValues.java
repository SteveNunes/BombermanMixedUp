package frameset_tags;

import entities.Sprite;

public class IncSprColorTintValues extends FrameTag {
	
	private double incrementRed;
	private double incrementGreen;
	private double incrementBlue;
	private double incrementAlpha;
	
	public IncSprColorTintValues(double incrementRed, double incrementGreen, double incrementBlue, double incrementAlpha) {
		this.incrementRed = incrementRed;
		this.incrementGreen = incrementGreen;
		this.incrementBlue = incrementBlue;
		this.incrementAlpha = incrementAlpha;
	}

	public IncSprColorTintValues(double incrementRed, double incrementGreen, double incrementBlue)
		{ this(incrementRed, incrementGreen, incrementBlue, 1); }

	public double getIncrementRed()
		{ return incrementRed; }

	public double getIncrementGreen()
		{ return incrementGreen; }
		
	public double getIncrementBlue()
		{ return incrementBlue; }
	
	public double getIncrementAlpha()
		{ return incrementAlpha; }

	@Override
	public String toString()
		{ return "{" + FrameTag.getClassName(this) + ";" + incrementRed + ";" + incrementGreen + ";" + incrementBlue + ";" + incrementAlpha + "}"; }

	public IncSprColorTintValues(String tags) {
		String[] params = FrameTag.validateStringTags(this, tags, 4);
		int n = 0;
		try {
			incrementRed = Double.parseDouble(params[n++]);
			incrementGreen = Double.parseDouble(params[n++]);
			incrementBlue = Double.parseDouble(params[n++]);
			incrementAlpha = Double.parseDouble(params[n++]);
		}
		catch (Exception e)
			{ throw new RuntimeException(params[--n] + " - Invalid parameter"); }
	}

	@Override
	public IncSprColorTintValues getNewInstanceOfThis()
		{ return new IncSprColorTintValues(incrementRed, incrementGreen, incrementBlue, incrementAlpha); }
	
	@Override
	public void process(Sprite sprite) {
		sprite.getEffects().getColorTint().incRed(getIncrementRed());
		sprite.getEffects().getColorTint().incGreen(getIncrementGreen());
		sprite.getEffects().getColorTint().incBlue(getIncrementBlue());
		sprite.getEffects().getColorTint().incAlpha(getIncrementAlpha());
	}

	@Override
	public void reset() {
	}

}
