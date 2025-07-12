package frameset_tags;

import enums.SpriteLayerType;
import frameset.Sprite;
import util.Misc;

public class SetSprLayerType extends FrameTag {

	public SpriteLayerType value;

	public SetSprLayerType(SpriteLayerType value) {
		this.value = value;
	}

	public SetSprLayerType(String tags) {
		sourceStringTags = tags;
		String[] params = validateStringTags(this, tags, 1);
		try {
			value = params[0].equals("-") ? SpriteLayerType.GROUND : SpriteLayerType.valueOf(params[0]);
		}
		catch (Exception e) {
    	Misc.addErrorOnLog(e, ".\\errors.log");
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
