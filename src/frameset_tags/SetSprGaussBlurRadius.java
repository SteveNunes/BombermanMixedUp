package frameset_tags;

import frameset.Sprite;
import util.Misc;

public class SetSprGaussBlurRadius extends FrameTag {

	public int value;

	public SetSprGaussBlurRadius(int value) {
		this.value = value;
	}

	public SetSprGaussBlurRadius(String tags) {
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
	public SetSprGaussBlurRadius getNewInstanceOfThis() {
		return new SetSprGaussBlurRadius(value);
	}

	@Override
	public void process(Sprite sprite) {
		sprite.getEffects().getGaussianBlur().setRadius(value);
	}

}
