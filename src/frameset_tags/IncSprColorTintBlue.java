package frameset_tags;

import frameset.Sprite;

public class IncSprColorTintBlue extends FrameTag {

	public double increment;

	public IncSprColorTintBlue(double increment) {
		this.increment = increment;
	}

	@Override
	public String toString() {
		return "{" + getClassName(this) + ";" + increment + "}";
	}

	public IncSprColorTintBlue(String tags) {
		String[] params = validateStringTags(this, tags, 1);
		try {
			increment = Double.parseDouble(params[0]);
		}
		catch (Exception e) {
			throw new RuntimeException(params[0] + " - Invalid parameter");
		}
	}

	@Override
	public IncSprColorTintBlue getNewInstanceOfThis() {
		return new IncSprColorTintBlue(increment);
	}

	@Override
	public void process(Sprite sprite) {
		sprite.getEffects().getColorTint().incBlue(increment);
	}

}
