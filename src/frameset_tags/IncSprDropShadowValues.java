package frameset_tags;

import frameset.Sprite;

public class IncSprDropShadowValues extends FrameTag {

	public double incrementOffsetX;
	public double incrementOffsetY;

	public IncSprDropShadowValues(double incrementOffsetX, double incrementOffsetY) {
		this.incrementOffsetX = incrementOffsetX;
		this.incrementOffsetY = incrementOffsetY;
	}

	@Override
	public String toString() {
		return "{" + getClassName(this) + ";" + incrementOffsetX + ";" + incrementOffsetY + "}";
	}

	public IncSprDropShadowValues(String tags) {
		String[] params = validateStringTags(this, tags, 2);
		int n = 0;
		try {
			incrementOffsetX = Double.parseDouble(params[n++]);
			incrementOffsetY = Double.parseDouble(params[n++]);
		}
		catch (Exception e) {
			throw new RuntimeException(params[--n] + " - Invalid parameter");
		}
	}

	@Override
	public IncSprDropShadowValues getNewInstanceOfThis() {
		return new IncSprDropShadowValues(incrementOffsetX, incrementOffsetY);
	}

	@Override
	public void process(Sprite sprite) {
		sprite.getEffects().getDropShadow().incOffsetX(incrementOffsetX);
		sprite.getEffects().getDropShadow().incOffsetY(incrementOffsetY);
	}

}
