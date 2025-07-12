package frameset_tags;

import frameset.Sprite;
import util.Misc;

public class SetOriginSprX extends FrameTag {

	public int value;

	public SetOriginSprX(int value) {
		this.value = value;
	}

	public SetOriginSprX(String tags) {
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
	public SetOriginSprX getNewInstanceOfThis() {
		return new SetOriginSprX(value);
	}

	@Override
	public void process(Sprite sprite) {
		sprite.setOriginSpriteX(value);
	}

}
