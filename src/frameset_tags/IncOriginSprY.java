package frameset_tags;

import frameset.Sprite;
import util.Misc;

public class IncOriginSprY extends FrameTag {

	public int increment;

	public IncOriginSprY(int increment) {
		this.increment = increment;
	}

	public IncOriginSprY(String tags) {
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
	public IncOriginSprY getNewInstanceOfThis() {
		return new IncOriginSprY(increment);
	}

	@Override
	public void process(Sprite sprite) {
		sprite.incOriginSpriteY(increment);
	}

}
