package frameset_tags;

import frameset.Sprite;
import util.Misc;

public class SetOutputSprY extends FrameTag {

	public double value;

	public SetOutputSprY(double value) {
		this.value = value;
	}

	public SetOutputSprY(String tags) {
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
	public SetOutputSprY getNewInstanceOfThis() {
		return new SetOutputSprY(value);
	}

	@Override
	public void process(Sprite sprite) {
		sprite.setY(value);
	}

}
