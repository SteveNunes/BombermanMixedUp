package frameset_tags;

import frameset.Sprite;

public class IncSprColorTintRed extends FrameTag {

	public double increment;

	public IncSprColorTintRed(double increment) {
		this.increment = increment;
	}

	public IncSprColorTintRed(String tags) {
		sourceStringTags = tags;
		String[] params = validateStringTags(this, tags, 1);
		try {
			increment = Double.parseDouble(params[0]);
		}
		catch (Exception e) {
			throw new RuntimeException(params[0] + " - Invalid parameter");
		}
	}

	@Override
	public IncSprColorTintRed getNewInstanceOfThis() {
		return new IncSprColorTintRed(increment);
	}

	@Override
	public void process(Sprite sprite) {
		sprite.getEffects().getColorTint().incRed(increment);
	}

}
