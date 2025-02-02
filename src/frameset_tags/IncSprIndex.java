package frameset_tags;

import frameset.Sprite;

public class IncSprIndex extends FrameTag {

	public int increment;

	public IncSprIndex(int increment) {
		this.increment = increment;
	}

	public IncSprIndex(String tags) {
		sourceStringTags = tags;
		String[] params = validateStringTags(this, tags, 1);
		try {
			increment = Integer.parseInt(params[0]);
		}
		catch (Exception e) {
			throw new RuntimeException(params[0] + " - Invalid parameter");
		}
	}

	@Override
	public IncSprIndex getNewInstanceOfThis() {
		return new IncSprIndex(increment);
	}

	@Override
	public void process(Sprite sprite) {
		sprite.incSpriteIndex(increment);
	}

}
