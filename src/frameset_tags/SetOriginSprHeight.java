package frameset_tags;

import frameset.Sprite;
import util.Misc;

public class SetOriginSprHeight extends FrameTag {

	public int value;

	public SetOriginSprHeight(int value) {
		this.value = value;
	}

	public SetOriginSprHeight(String tags) {
		sourceStringTags = tags;
		String[] params = validateStringTags(this, tags, 1);
		try {
			value = Integer.parseInt(params[0]);
		}
		catch (Exception e) {
    	Misc.addErrorOnLog(e, ".\\errors.log");
			throw new RuntimeException(params[0] + " - Invalid parameter");
		}
	}

	@Override
	public SetOriginSprHeight getNewInstanceOfThis() {
		return new SetOriginSprHeight(value);
	}

	@Override
	public void process(Sprite sprite) {
		sprite.setOriginSpriteHeight(value);
	}

}
