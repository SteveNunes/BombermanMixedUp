package frameset_tags;

import frameset.Sprite;
import util.Misc;

public class SetSprAlpha extends FrameTag {

	public double value;

	public SetSprAlpha(double value) {
		this.value = value;
	}

	public SetSprAlpha(String tags) {
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
	public SetSprAlpha getNewInstanceOfThis() {
		return new SetSprAlpha(value);
	}

	@Override
	public void process(Sprite sprite) {
		sprite.setAlpha(value);
	}

}
