package frameset_tags;

import frameset.Sprite;
import util.Misc;

public class SetSprRotate extends FrameTag {

	public int value;

	public SetSprRotate(int value) {
		this.value = value;
	}

	public SetSprRotate(String tags) {
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
	public SetSprRotate getNewInstanceOfThis() {
		return new SetSprRotate(value);
	}

	@Override
	public void process(Sprite sprite) {
		sprite.setRotation(value);
	}

}
