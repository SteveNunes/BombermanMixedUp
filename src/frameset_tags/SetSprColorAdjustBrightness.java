package frameset_tags;

import frameset.Sprite;

public class SetSprColorAdjustBrightness extends FrameTag {

	public double value;

	public SetSprColorAdjustBrightness(double value) {
		this.value = value;
	}

	public SetSprColorAdjustBrightness(String tags) {
		sourceStringTags = tags;
		String[] params = validateStringTags(this, tags, 1);
		try {
			value = Double.parseDouble(params[0]);
		}
		catch (Exception e) {
			throw new RuntimeException(params[0] + " - Invalid parameter");
		}
	}

	@Override
	public SetSprColorAdjustBrightness getNewInstanceOfThis() {
		return new SetSprColorAdjustBrightness(value);
	}

	@Override
	public void process(Sprite sprite) {
		sprite.getEffects().getColorAdjust().setBrightness(value);
	}

}
