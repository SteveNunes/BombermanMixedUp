package frameset_tags;

import frameset.Sprite;

public class SetOriginSprWidth extends FrameTag {

	public int value;

	public SetOriginSprWidth(int value) {
		this.value = value;
	}

	public SetOriginSprWidth(String tags) {
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
	public SetOriginSprWidth getNewInstanceOfThis() {
		return new SetOriginSprWidth(value);
	}

	@Override
	public void process(Sprite sprite) {
		sprite.setOriginSpriteWidth(value);
	}

}
