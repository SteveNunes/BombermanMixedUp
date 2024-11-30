package frameset_tags;

import enums.SpriteLayerType;
import frameset.Sprite;

public class SetSprLayerType extends FrameTag {

	public SpriteLayerType value;

	public SetSprLayerType(SpriteLayerType value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return "{" + getClassName(this) + ";" + (value == null ? "-" : value) + "}";
	}

	public SetSprLayerType(String tags) {
		String[] params = validateStringTags(this, tags, 1);
		try {
			value = params[0].equals("-") ? SpriteLayerType.GROUND : SpriteLayerType.valueOf(params[0]);
		}
		catch (Exception e) {
			throw new RuntimeException(params[0] + " - Invalid parameter");
		}
	}

	@Override
	public SetSprLayerType getNewInstanceOfThis() {
		return new SetSprLayerType(value);
	}

	@Override
	public void process(Sprite sprite) {
		sprite.setLayerType(value);
	}

}
