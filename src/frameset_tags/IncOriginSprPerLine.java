package frameset_tags;

import frameset.Sprite;
import util.Misc;

public class IncOriginSprPerLine extends FrameTag {

	public int increment;

	public IncOriginSprPerLine(int increment) {
		this.increment = increment;
	}

	public IncOriginSprPerLine(String tags) {
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
	public IncOriginSprPerLine getNewInstanceOfThis() {
		return new IncOriginSprPerLine(increment);
	}

	@Override
	public void process(Sprite sprite) {
		sprite.incSpritesPerLine(increment);
	}

}
