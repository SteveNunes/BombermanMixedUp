package frameset_tags;

import frameset.Sprite;

public class SetSprGlowLevel extends FrameTag {

	public double value;

	public SetSprGlowLevel(double value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return "{" + getClassName(this) + ";" + value + "}";
	}

	public SetSprGlowLevel(String tags) {
		String[] params = validateStringTags(this, tags, 1);
		try {
			value = Double.parseDouble(params[0]);
		}
		catch (Exception e) {
			throw new RuntimeException(params[0] + " - Invalid parameter");
		}
	}

	@Override
	public SetSprGlowLevel getNewInstanceOfThis() {
		return new SetSprGlowLevel(value);
	}

	@Override
	public void process(Sprite sprite) {
		sprite.getEffects().getGlow().setLevel(value);
	}

}
