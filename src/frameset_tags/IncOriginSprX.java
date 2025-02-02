package frameset_tags;

import frameset.Sprite;

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
