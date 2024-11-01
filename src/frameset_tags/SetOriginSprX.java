package frameset_tags;

import frameset.Sprite;

public class SetOriginSprX extends FrameTag {

	public int value;

	public SetOriginSprX(int value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return "{" + getClassName(this) + ";" + value + "}";
	}

	public SetOriginSprX(String tags) {
		String[] params = validateStringTags(this, tags, 1);
		try {
			value = Integer.parseInt(params[0]);
		}
		catch (Exception e) {
			throw new RuntimeException(params[0] + " - Invalid parameter");
		}
	}

	@Override
	public SetOriginSprX getNewInstanceOfThis() {
		return new SetOriginSprX(value);
	}

	@Override
	public void process(Sprite sprite) {
		sprite.setOriginSpriteX(value);
	}

}
