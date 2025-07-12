package frameset_tags;

import frameset.Sprite;
import util.Misc;

public class SetOutputSprWidth extends FrameTag {

	public int value;

	public SetOutputSprWidth(int value) {
		this.value = value;
	}

	public SetOutputSprWidth(String tags) {
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
	public SetOutputSprWidth getNewInstanceOfThis() {
		return new SetOutputSprWidth(value);
	}

	@Override
	public void process(Sprite sprite) {
		sprite.setOutputWidth(value);
	}

}
