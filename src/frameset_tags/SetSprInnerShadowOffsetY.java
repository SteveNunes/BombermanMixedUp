package frameset_tags;

import frameset.Sprite;
import util.Misc;

public class SetSprInnerShadowOffsetY extends FrameTag {

	public int value;

	public SetSprInnerShadowOffsetY(int value) {
		this.value = value;
	}

	public SetSprInnerShadowOffsetY(String tags) {
		sourceStringTags = tags;
		String[] params = validateStringTags(this, tags, 1);
		try {
			value = Integer.parseInt(params[0]);
		}
		catch (Exception e) {
    	Misc.addErrorOnLog(e, ".\\errors.log");
			throw new RuntimeException(params[0] + " - Invalid parameter");
		}
	}

	@Override
	public SetSprInnerShadowOffsetY getNewInstanceOfThis() {
		return new SetSprInnerShadowOffsetY(value);
	}

	@Override
	public void process(Sprite sprite) {
		sprite.getEffects().getInnerShadow().setOffsetY(value);
	}

}
