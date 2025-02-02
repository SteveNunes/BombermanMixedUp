package frameset_tags;

import frameset.Sprite;

public class IncOutputSprWidth extends FrameTag {

	public int increment;

	public IncOutputSprWidth(int increment) {
		this.increment = increment;
	}

	public IncOutputSprWidth(String tags) {
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
	public IncOutputSprWidth getNewInstanceOfThis() {
		return new IncOutputSprWidth(increment);
	}

	@Override
	public void process(Sprite sprite) {
		sprite.incOutputWidth(increment);
	}

}
