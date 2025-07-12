package frameset_tags;

import frameset.Sprite;
import util.Misc;

public class IncSprDropShadowOffsetY extends FrameTag {

	public double increment;

	public IncSprDropShadowOffsetY(double increment) {
		this.increment = increment;
	}

	public IncSprDropShadowOffsetY(String tags) {
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
	public IncSprDropShadowOffsetY getNewInstanceOfThis() {
		return new IncSprDropShadowOffsetY(increment);
	}

	@Override
	public void process(Sprite sprite) {
		sprite.getEffects().getDropShadow().incOffsetY(increment);
	}

}
