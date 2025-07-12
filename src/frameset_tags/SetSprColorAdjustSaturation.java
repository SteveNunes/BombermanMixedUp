package frameset_tags;

import frameset.Sprite;
import util.Misc;

public class SetSprColorAdjustSaturation extends FrameTag {

	public double value;

	public SetSprColorAdjustSaturation(double value) {
		this.value = value;
	}

	public SetSprColorAdjustSaturation(String tags) {
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
	public SetSprColorAdjustSaturation getNewInstanceOfThis() {
		return new SetSprColorAdjustSaturation(value);
	}

	@Override
	public void process(Sprite sprite) {
		sprite.getEffects().getColorAdjust().setSaturation(value);
	}

}
