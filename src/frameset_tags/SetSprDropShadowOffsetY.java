package frameset_tags;

import frameset.Sprite;
import util.Misc;

public class SetSprDropShadowOffsetY extends FrameTag {

	public double value;

	public SetSprDropShadowOffsetY(double value) {
		this.value = value;
	}

	public SetSprDropShadowOffsetY(String tags) {
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
	public SetSprDropShadowOffsetY getNewInstanceOfThis() {
		return new SetSprDropShadowOffsetY(value);
	}

	@Override
	public void process(Sprite sprite) {
		sprite.getEffects().getDropShadow().setOffsetY(value);
	}

}
