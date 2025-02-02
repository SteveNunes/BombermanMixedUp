package frameset_tags;

import frameset.Sprite;

public class IncOutputSprX extends FrameTag {

	public double increment;

	public IncOutputSprX(double increment) {
		this.increment = increment;
	}

	public IncOutputSprX(String tags) {
		sourceStringTags = tags;
		String[] params = validateStringTags(this, tags, 1);
		try {
			increment = Double.parseDouble(params[0]);
		}
		catch (Exception e) {
			throw new RuntimeException(params[0] + " - Invalid parameter");
		}
	}

	@Override
	public IncOutputSprX getNewInstanceOfThis() {
		return new IncOutputSprX(increment);
	}

	@Override
	public void process(Sprite sprite) {
		sprite.incX(increment);
	}

}
