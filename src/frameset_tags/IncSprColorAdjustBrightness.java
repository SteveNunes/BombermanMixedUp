package frameset_tags;

import frameset.Sprite;
import util.Misc;

public class IncSprColorAdjustBrightness extends FrameTag {

	public double increment;

	public IncSprColorAdjustBrightness(double increment) {
		this.increment = increment;
	}

	public IncSprColorAdjustBrightness(String tags) {
		sourceStringTags = tags;
		String[] params = validateStringTags(this, tags, 1);
		try {
			increment = Double.parseDouble(params[0]);
		}
		catch (Exception e) {
    	Misc.addErrorOnLog(e, ".\\errors.log");
			throw new RuntimeException(params[0] + " - Invalid parameter");
		}
	}

	@Override
	public IncSprColorAdjustBrightness getNewInstanceOfThis() {
		return new IncSprColorAdjustBrightness(increment);
	}

	@Override
	public void process(Sprite sprite) {
		sprite.getEffects().getColorAdjust().incBrightness(increment);
	}

}
