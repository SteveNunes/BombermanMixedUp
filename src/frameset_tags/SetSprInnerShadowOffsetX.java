package frameset_tags;

import frameset.Sprite;
import util.Misc;

public class SetSprInnerShadowOffsetX extends FrameTag {

	public int value;

	public SetSprInnerShadowOffsetX(int value) {
		this.value = value;
	}

	public SetSprInnerShadowOffsetX(String tags) {
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
	public SetSprInnerShadowOffsetX getNewInstanceOfThis() {
		return new SetSprInnerShadowOffsetX(value);
	}

	@Override
	public void process(Sprite sprite) {
		sprite.getEffects().getInnerShadow().setOffsetX(value);
	}

}
