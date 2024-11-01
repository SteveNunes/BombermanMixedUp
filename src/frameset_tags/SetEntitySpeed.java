package frameset_tags;

import frameset.Sprite;

public class SetEntitySpeed extends FrameTag {

	public double value;

	public SetEntitySpeed(double value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return "{" + getClassName(this) + ";" + value + "}";
	}

	public SetEntitySpeed(String tags) {
		String[] params = validateStringTags(this, tags, 1);
		try {
			value = Integer.parseInt(params[0]);
		}
		catch (Exception e) {
			throw new RuntimeException(params[0] + " - Invalid parameter");
		}
	}

	@Override
	public SetEntitySpeed getNewInstanceOfThis() {
		return new SetEntitySpeed(value);
	}

	@Override
	public void process(Sprite sprite) {
		sprite.getSourceEntity().setSpeed(value);
	}

}
