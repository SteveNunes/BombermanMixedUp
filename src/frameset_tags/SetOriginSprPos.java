package frameset_tags;

import frameset.Sprite;

public class SetOriginSprPos extends FrameTag {

	public int x;
	public int y;

	public SetOriginSprPos(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public SetOriginSprPos(String tags) {
		sourceStringTags = tags;
		String[] params = validateStringTags(this, tags, 2);
		int n = 0;
		try {
			x = Integer.parseInt(params[n++]);
			y = Integer.parseInt(params[n++]);
		}
		catch (Exception e) {
			throw new RuntimeException(params[--n] + " - Invalid parameter");
		}
	}

	@Override
	public SetOriginSprPos getNewInstanceOfThis() {
		return new SetOriginSprPos(x, y);
	}

	@Override
	public void process(Sprite sprite) {
		sprite.setOriginSpriteX(x);
		sprite.setOriginSpriteY(y);
	}

}
