package frameset_tags;

import frameset.Sprite;

public class IncOriginSprSize extends FrameTag {

	public int incrementWidth;
	public int incrementHeight;

	public IncOriginSprSize(int incrementWidth, int incrementHeight) {
		this.incrementWidth = incrementWidth;
		this.incrementHeight = incrementHeight;
	}

	public IncOriginSprSize(String tags) {
		sourceStringTags = tags;
		String[] params = validateStringTags(this, tags, 2);
		int n = 0;
		try {
			incrementWidth = Integer.parseInt(params[n++]);
			incrementHeight = Integer.parseInt(params[n++]);
		}
		catch (Exception e) {
			throw new RuntimeException(params[--n] + " - Invalid parameter");
		}
	}

	@Override
	public IncOriginSprSize getNewInstanceOfThis() {
		return new IncOriginSprSize(incrementWidth, incrementHeight);
	}

	@Override
	public void process(Sprite sprite) {
		sprite.incOriginSpriteWidth(incrementWidth);
		sprite.incOriginSpriteHeight(incrementHeight);
	}

}
