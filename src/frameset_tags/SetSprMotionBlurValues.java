package frameset_tags;

import frameset.Sprite;
import javafx.scene.effect.BlendMode;
import util.Misc;

public class SetSprMotionBlurValues extends FrameTag {

	public double angle;
	public double radius;
	public BlendMode blendMode;

	public SetSprMotionBlurValues(double angle, double radius, BlendMode blendMode) {
		this.angle = angle;
		this.radius = radius;
		this.blendMode = blendMode;
	}

	public SetSprMotionBlurValues(double angle, double radius) {
		this(angle, radius, BlendMode.SRC_ATOP);
	}

	public SetSprMotionBlurValues(String tags) {
		sourceStringTags = tags;
		String[] params = validateStringTags(this, tags, 3);
		int n = 0;
		try {
			angle = Double.parseDouble(params[n++]);
			radius = Double.parseDouble(params[n++]);
			blendMode = BlendMode.valueOf(params[n++]);
		}
		catch (Exception e) {
    	Misc.addErrorOnLog(e, ".\\errors.log");
			throw new RuntimeException(params[--n] + " - Invalid parameter");
		}
	}

	@Override
	public SetSprMotionBlurValues getNewInstanceOfThis() {
		return new SetSprMotionBlurValues(angle, radius, blendMode);
	}

	@Override
	public void process(Sprite sprite) {
		sprite.getEffects().setMotionBlur(angle, radius, blendMode);
	}

}
