package frameset_tags;

import frameset.Sprite;

public class IncSprMotionBlurAngle extends FrameTag {

	public double increment;

	public IncSprMotionBlurAngle(double increment) {
		this.increment = increment;
	}

	public IncSprMotionBlurAngle(String tags) {
		sourceStringTags = tags;
		String[] params = validateStringTags(this, tags, 1);
		try {
			increment = Double.parseDouble(params[0]);
		}
		catch (Exception e) {
			throw new RuntimeException(params[0] + " - Invalid parameter");
		}
	}

	@Override
	public IncSprMotionBlurAngle getNewInstanceOfThis() {
		return new IncSprMotionBlurAngle(increment);
	}

	@Override
	public void process(Sprite sprite) {
		sprite.getEffects().getMotionBlur().incAngle(increment);
	}

}
