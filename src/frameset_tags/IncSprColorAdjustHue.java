package frameset_tags;

import frameset.Sprite;

public class IncSprColorAdjustHue extends FrameTag {

	public double increment;

	public IncSprColorAdjustHue(double increment) {
		this.increment = increment;
	}

	@Override
	public String toString() {
		return "{" + getClassName(this) + ";" + increment + "}";
	}

	public IncSprColorAdjustHue(String tags) {
		String[] params = validateStringTags(this, tags, 1);
		try {
			increment = Double.parseDouble(params[0]);
		}
		catch (Exception e) {
			throw new RuntimeException(params[0] + " - Invalid parameter");
		}
	}

	@Override
	public IncSprColorAdjustHue getNewInstanceOfThis() {
		return new IncSprColorAdjustHue(increment);
	}

	@Override
	public void process(Sprite sprite) {
		sprite.getEffects().getColorAdjust().incHue(increment);
	}

}
