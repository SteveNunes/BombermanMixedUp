package frameset_tags;

import frameset.Sprite;
import util.Misc;

public class IncOriginSprHeight extends FrameTag {

	public int increment;

	public IncOriginSprHeight(int increment) {
		this.increment = increment;
	}

	public IncOriginSprHeight(String tags) {
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
	public IncOriginSprHeight getNewInstanceOfThis() {
		return new IncOriginSprHeight(increment);
	}

	@Override
	public void process(Sprite sprite) {
		sprite.incOriginSpriteHeight(increment);
	}

}
