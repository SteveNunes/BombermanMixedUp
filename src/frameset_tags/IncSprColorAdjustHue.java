package frameset_tags;

import frameset.Sprite;
import util.Misc;

public class IncSprColorAdjustHue extends FrameTag {

	public double increment;

	public IncSprColorAdjustHue(double increment) {
		this.increment = increment;
	}

	public IncSprColorAdjustHue(String tags) {
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
	public IncSprColorAdjustHue getNewInstanceOfThis() {
		return new IncSprColorAdjustHue(increment);
	}

	@Override
	public void process(Sprite sprite) {
		sprite.getEffects().getColorAdjust().incHue(increment);
	}

}
