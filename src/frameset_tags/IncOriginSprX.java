package frameset_tags;

import frameset.Sprite;
import util.Misc;

public class IncOriginSprX extends FrameTag {

	public int increment;

	public IncOriginSprX(int increment) {
		this.increment = increment;
	}

	public IncOriginSprX(String tags) {
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
	public IncOriginSprX getNewInstanceOfThis() {
		return new IncOriginSprX(increment);
	}

	@Override
	public void process(Sprite sprite) {
		sprite.incOriginSpriteX(increment);
	}

}
