package frameset_tags;

import frameset.Sprite;

public class SetOutputSprY extends FrameTag {

	public double value;

	public SetOutputSprY(double value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return "{" + getClassName(this) + ";" + value + "}";
	}

	public SetOutputSprY(String tags) {
		String[] params = validateStringTags(this, tags, 1);
		try {
			value = Double.parseDouble(params[0]);
		}
		catch (Exception e) {
			throw new RuntimeException(params[0] + " - Invalid parameter");
		}
	}

	@Override
	public SetOutputSprY getNewInstanceOfThis() {
		return new SetOutputSprY(value);
	}

	@Override
	public void process(Sprite sprite) {
		sprite.setY(value);
	}

}
