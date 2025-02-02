package frameset_tags;

import frameset.Sprite;

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
