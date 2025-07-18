package frameset_tags;

import frameset.Sprite;
import util.Misc;

public class IncSprMotionBlurValues extends FrameTag {

	public double incrementAngle;
	public double incrementRadius;

	public IncSprMotionBlurValues(double incrementAngle, double incrementRadius) {
		this.incrementAngle = incrementAngle;
		this.incrementRadius = incrementRadius;
	}

	public IncSprMotionBlurValues(String tags) {
		sourceStringTags = tags;
		String[] params = validateStringTags(this, tags, 2);
		int n = 0;
		try {
			incrementAngle = Double.parseDouble(params[n++]);
			incrementRadius = Double.parseDouble(params[n++]);
		}
		catch (Exception e) {
    	Misc.addErrorOnLog(e, ".\\errors.log");
			throw new RuntimeException(params[--n] + " - Invalid parameter");
		}
	}

	@Override
	public IncSprMotionBlurValues getNewInstanceOfThis() {
		return new IncSprMotionBlurValues(incrementAngle, incrementRadius);
	}

	@Override
	public void process(Sprite sprite) {
		sprite.getEffects().setMotionBlur(incrementAngle, incrementRadius);
	}

}
