package frameset_tags;

import frameset.Sprite;
import util.Misc;

public class IncSprGaussBlurRadius extends FrameTag {

	public int increment;

	public IncSprGaussBlurRadius(int increment) {
		this.increment = increment;
	}

	public IncSprGaussBlurRadius(String tags) {
		sourceStringTags = tags;
		String[] params = validateStringTags(this, tags, 1);
		try {
			increment = Integer.parseInt(params[0]);
		}
		catch (Exception e) {
    	Misc.addErrorOnLog(e, ".\\errors.log");
			throw new RuntimeException(params[0] + " - Invalid parameter");
		}
	}

	@Override
	public IncSprGaussBlurRadius getNewInstanceOfThis() {
		return new IncSprGaussBlurRadius(increment);
	}

	@Override
	public void process(Sprite sprite) {
		sprite.getEffects().getGaussianBlur().incRadius(increment);
	}

}
