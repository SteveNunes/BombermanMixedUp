package frameset_tags;

import frameset.Sprite;
import javafx.scene.effect.BlendMode;
import tools.Tools;

public class SetSprMotionBlurValues extends FrameTag {
	
	private double angle;
	private double radius;
	private BlendMode blendMode;
	
	public SetSprMotionBlurValues(double angle, double radius, BlendMode blendMode) {
		this.angle = angle;
		this.radius = radius;
		this.blendMode = blendMode;
	}

	public SetSprMotionBlurValues(double angle, double radius)
		{ this(angle, radius, BlendMode.SRC_ATOP); }

	public double getAngle()
		{ return angle; }

	public double getRadius()
		{ return radius; }	
	
	public BlendMode getBlendMode()
		{ return blendMode; }

	@Override
	public String toString()
		{ return "{" + FrameTag.getClassName(this) + ";" + angle + ";" + radius + ";" + blendMode.name() + "}"; }
	
	public SetSprMotionBlurValues(String tags) {
		String[] params = FrameTag.validateStringTags(this, tags, 3);
		int n = 0;
		try {
			angle = Double.parseDouble(params[n++]);
			radius = Double.parseDouble(params[n++]);
			blendMode = BlendMode.valueOf(params[n++]);
		}
		catch (Exception e)
			{ throw new RuntimeException(params[--n] + " - Invalid parameter"); }
	}

	@Override
	public SetSprMotionBlurValues getNewInstanceOfThis()
		{ return new SetSprMotionBlurValues(angle, radius, blendMode); }
	
	@Override
	public void process(Sprite sprite)
		{ sprite.getEffects().setMotionBlur(getAngle(), getRadius(), getBlendMode()); }

}