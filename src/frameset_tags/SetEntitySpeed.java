package frameset_tags;

import frameset.Sprite;
import util.Misc;

public class SetEntitySpeed extends FrameTag {

	public double value;

	public SetEntitySpeed(double value) {
		this.value = value;
	}

	public SetEntitySpeed(String tags) {
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
	public SetEntitySpeed getNewInstanceOfThis() {
		return new SetEntitySpeed(value);
	}

	@Override
	public void process(Sprite sprite) {
		sprite.getSourceEntity().setSpeed(value);
	}

}
