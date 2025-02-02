package frameset_tags;

import frameset.Sprite;

public class IncSprFrontValue extends FrameTag {

	public int increment;

	public IncSprFrontValue(int increment) {
		this.increment = increment;
	}

	public IncSprFrontValue(String tags) {
		sourceStringTags = tags;
		String[] params = validateStringTags(this, tags, 1);
		try {
			increment = Integer.parseInt(params[0]);
		}
		catch (Exception e) {
			throw new RuntimeException(params[0] + " - Invalid parameter");
		}
	}

	@Override
	public IncSprFrontValue getNewInstanceOfThis() {
		return new IncSprFrontValue(increment);
	}

	@Override
	public void process(Sprite sprite) {
		sprite.incFrontValue(increment);
	}

}
