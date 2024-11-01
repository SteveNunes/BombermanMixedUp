package frameset_tags;

import frameset.Sprite;

public class IncOutputSprHeight extends FrameTag {

	public int increment;

	public IncOutputSprHeight(int increment) {
		this.increment = increment;
	}

	@Override
	public String toString() {
		return "{" + getClassName(this) + ";" + increment + "}";
	}

	public IncOutputSprHeight(String tags) {
		String[] params = validateStringTags(this, tags, 1);
		try {
			increment = Integer.parseInt(params[0]);
		}
		catch (Exception e) {
			throw new RuntimeException(params[0] + " - Invalid parameter");
		}
	}

	@Override
	public IncOutputSprHeight getNewInstanceOfThis() {
		return new IncOutputSprHeight(increment);
	}

	@Override
	public void process(Sprite sprite) {
		sprite.incOutputHeight(increment);
	}

}
