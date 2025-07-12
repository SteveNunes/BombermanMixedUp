package frameset_tags;

import frameset.Sprite;
import util.Misc;

public class IncSprColorTintAlpha extends FrameTag {

	public double increment;

	public IncSprColorTintAlpha(double increment) {
		this.increment = increment;
	}

	public IncSprColorTintAlpha(String tags) {
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
	public IncSprColorTintAlpha getNewInstanceOfThis() {
		return new IncSprColorTintAlpha(increment);
	}

	@Override
	public void process(Sprite sprite) {
		sprite.getEffects().getColorTint().incAlpha(increment);
	}

}
