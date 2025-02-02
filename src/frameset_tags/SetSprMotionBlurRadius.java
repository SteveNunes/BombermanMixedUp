package frameset_tags;

import frameset.Sprite;

public class SetSprMotionBlurRadius extends FrameTag {

	public double value;

	public SetSprMotionBlurRadius(double value) {
		this.value = value;
	}

	public SetSprMotionBlurRadius(String tags) {
		sourceStringTags = tags;
		String[] params = validateStringTags(this, tags, 1);
		try {
			value = Double.parseDouble(params[0]);
		}
		catch (Exception e) {
			throw new RuntimeException(params[0] + " - Invalid parameter");
		}
	}

	@Override
	public SetSprMotionBlurRadius getNewInstanceOfThis() {
		return new SetSprMotionBlurRadius(value);
	}

	@Override
	public void process(Sprite sprite) {
		sprite.getEffects().getMotionBlur().setRadius(value);
	}

}
