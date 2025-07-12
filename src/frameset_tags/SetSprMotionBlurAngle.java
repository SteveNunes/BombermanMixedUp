package frameset_tags;

import frameset.Sprite;
import util.Misc;

public class SetSprMotionBlurAngle extends FrameTag {

	public double value;

	public SetSprMotionBlurAngle(double value) {
		this.value = value;
	}

	public SetSprMotionBlurAngle(String tags) {
		sourceStringTags = tags;
		String[] params = validateStringTags(this, tags, 1);
		try {
			value = Double.parseDouble(params[0]);
		}
		catch (Exception e) {
    	Misc.addErrorOnLog(e, ".\\errors.log");
			throw new RuntimeException(params[0] + " - Invalid parameter");
		}
	}

	@Override
	public SetSprMotionBlurAngle getNewInstanceOfThis() {
		return new SetSprMotionBlurAngle(value);
	}

	@Override
	public void process(Sprite sprite) {
		sprite.getEffects().getMotionBlur().setAngle(value);
	}

}
