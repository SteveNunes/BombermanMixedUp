package frameset_tags;

import frameset.Sprite;
import util.Misc;

public class SetSprColorTintBlue extends FrameTag {

	public double value;

	public SetSprColorTintBlue(double value) {
		this.value = value;
	}

	public SetSprColorTintBlue(String tags) {
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
	public SetSprColorTintBlue getNewInstanceOfThis() {
		return new SetSprColorTintBlue(value);
	}

	@Override
	public void process(Sprite sprite) {
		sprite.getEffects().getColorTint().setBlue(value);
	}

}
