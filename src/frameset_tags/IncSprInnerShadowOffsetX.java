package frameset_tags;

import frameset.Sprite;
import util.Misc;

public class IncSprInnerShadowOffsetX extends FrameTag {

	public double increment;

	public IncSprInnerShadowOffsetX(double increment) {
		this.increment = increment;
	}

	public IncSprInnerShadowOffsetX(String tags) {
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
	public IncSprInnerShadowOffsetX getNewInstanceOfThis() {
		return new IncSprInnerShadowOffsetX(increment);
	}

	@Override
	public void process(Sprite sprite) {
		sprite.getEffects().getInnerShadow().incOffsetX(increment);
	}

}
