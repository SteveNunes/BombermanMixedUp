package frameset_tags;

import frameset.Sprite;
import util.Misc;

public class IncSprInnerShadowOffsetY extends FrameTag {

	public double increment;

	public IncSprInnerShadowOffsetY(double increment) {
		this.increment = increment;
	}

	public IncSprInnerShadowOffsetY(String tags) {
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
	public IncSprInnerShadowOffsetY getNewInstanceOfThis() {
		return new IncSprInnerShadowOffsetY(increment);
	}

	@Override
	public void process(Sprite sprite) {
		sprite.getEffects().getInnerShadow().incOffsetY(increment);
	}

}
