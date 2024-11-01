package frameset_tags;

import frameset.Sprite;

public class IncSprColorTintGreen extends FrameTag {

	public double increment;

	public IncSprColorTintGreen(double increment) {
		this.increment = increment;
	}

	@Override
	public String toString() {
		return "{" + getClassName(this) + ";" + increment + "}";
	}

	public IncSprColorTintGreen(String tags) {
		String[] params = validateStringTags(this, tags, 1);
		try {
			increment = Double.parseDouble(params[0]);
		}
		catch (Exception e) {
			throw new RuntimeException(params[0] + " - Invalid parameter");
		}
	}

	@Override
	public IncSprColorTintGreen getNewInstanceOfThis() {
		return new IncSprColorTintGreen(increment);
	}

	@Override
	public void process(Sprite sprite) {
		sprite.getEffects().getColorTint().incGreen(increment);
	}

}
