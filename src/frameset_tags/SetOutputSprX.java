package frameset_tags;

import frameset.Sprite;
import util.Misc;

public class SetOutputSprX extends FrameTag {

	public double value;

	public SetOutputSprX(double value) {
		this.value = value;
	}

	public SetOutputSprX(String tags) {
		sourceStringTags = tags;
		String[] params = validateStringTags(this, tags, 1);
		try {
			value = Double.parseDouble(params[0]);
		}
		catch (Exception e) {
    	Misc.addErrorOnLog(e, ".\\errors.log");
			throw new RuntimeException(params[0] + " - Invalid parameter");
		}
	}

	@Override
	public SetOutputSprX getNewInstanceOfThis() {
		return new SetOutputSprX(value);
	}

	@Override
	public void process(Sprite sprite) {
		sprite.setX(value);
	}

}
