package frameset_tags;

import frameset.Sprite;
import tools.GameMisc;

public class IncSprMotionBlurValues extends FrameTag {
	
	private double incrementAngle;
	private double incrementRadius;
	
	public IncSprMotionBlurValues(double incrementAngle, double incrementRadius) {
		this.incrementAngle = incrementAngle;
		this.incrementRadius = incrementRadius;
	}

	public double getIncrementAngle()
		{ return incrementAngle; }

	public double getIncrementRadius()
		{ return incrementRadius; }

	@Override
	public String toString()
		{ return "{" + FrameTag.getClassName(this) + ";" + incrementAngle + ";" + incrementRadius + "}"; }

	public IncSprMotionBlurValues(String tags) {
		String[] params = FrameTag.validateStringTags(this, tags, 2);
		int n = 0;
		try {
			incrementAngle = Double.parseDouble(params[n++]);
			incrementRadius = Double.parseDouble(params[n++]);
		}
		catch (Exception e)
			{ GameMisc.throwRuntimeException(params[--n] + " - Invalid parameter"); }
	}

	@Override
	public IncSprMotionBlurValues getNewInstanceOfThis()
		{ return new IncSprMotionBlurValues(incrementAngle, incrementRadius); }
	
	@Override
	public void process(Sprite sprite)
		{ sprite.getEffects().setMotionBlur(getIncrementAngle(), getIncrementRadius()); }

}
