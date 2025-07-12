package frameset_tags;

import frameset.Sprite;
import util.Misc;

public class SetSprColorTintAlpha extends FrameTag {

	public double value;

	public SetSprColorTintAlpha(double value) {
		this.value = value;
	}

	public SetSprColorTintAlpha(String tags) {
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
	public SetSprColorTintAlpha getNewInstanceOfThis() {
		return new SetSprColorTintAlpha(value);
	}

	@Override
	public void process(Sprite sprite) {
		sprite.getEffects().getColorTint().setAlpha(value);
	}

}
