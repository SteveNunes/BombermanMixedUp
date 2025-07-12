package frameset_tags;

import frameset.Sprite;
import util.Misc;

public class IncSprColorTintGreen extends FrameTag {

	public double increment;

	public IncSprColorTintGreen(double increment) {
		this.increment = increment;
	}

	public IncSprColorTintGreen(String tags) {
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
	public IncSprColorTintGreen getNewInstanceOfThis() {
		return new IncSprColorTintGreen(increment);
	}

	@Override
	public void process(Sprite sprite) {
		sprite.getEffects().getColorTint().incGreen(increment);
	}

}
