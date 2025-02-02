package frameset_tags;

import frameset.Sprite;

public class SetOriginSprY extends FrameTag {

	public int value;

	public SetOriginSprY(int value) {
		this.value = value;
	}

	public SetOriginSprY(String tags) {
		sourceStringTags = tags;
		String[] params = validateStringTags(this, tags, 1);
		try {
			value = Integer.parseInt(params[0]);
		}
		catch (Exception e) {
			throw new RuntimeException(params[0] + " - Invalid parameter");
		}
	}

	@Override
	public SetOriginSprY getNewInstanceOfThis() {
		return new SetOriginSprY(value);
	}

	@Override
	public void process(Sprite sprite) {
		sprite.setOriginSpriteY(value);
	}

}
