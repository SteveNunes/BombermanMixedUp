package frameset_tags;

import frameset.Sprite;
import util.Misc;

public class SetSprDropShadowOffsetX extends FrameTag {

	public double value;

	public SetSprDropShadowOffsetX(double value) {
		this.value = value;
	}

	public SetSprDropShadowOffsetX(String tags) {
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
	public SetSprDropShadowOffsetX getNewInstanceOfThis() {
		return new SetSprDropShadowOffsetX(value);
	}

	@Override
	public void process(Sprite sprite) {
		sprite.getEffects().getDropShadow().setOffsetX(value);
	}

}
