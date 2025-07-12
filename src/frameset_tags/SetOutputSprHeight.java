package frameset_tags;

import frameset.Sprite;
import util.Misc;

public class SetOutputSprHeight extends FrameTag {

	public int value;

	public SetOutputSprHeight(int value) {
		this.value = value;
	}

	public SetOutputSprHeight(String tags) {
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
	public SetOutputSprHeight getNewInstanceOfThis() {
		return new SetOutputSprHeight(value);
	}

	@Override
	public void process(Sprite sprite) {
		sprite.setOutputHeight(value);
	}

}
