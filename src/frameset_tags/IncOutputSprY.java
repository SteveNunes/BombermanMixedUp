package frameset_tags;

import frameset.Sprite;
import util.Misc;

public class IncOutputSprY extends FrameTag {

	public double increment;

	public IncOutputSprY(double increment) {
		this.increment = increment;
	}

	public IncOutputSprY(String tags) {
		sourceStringTags = tags;
		String[] params = validateStringTags(this, tags, 1);
		try {
			increment = Double.parseDouble(params[0]);
		}
		catch (Exception e) {
    	Misc.addErrorOnLog(e, ".\\errors.log");
			throw new RuntimeException(params[0] + " - Invalid parameter");
		}
	}

	@Override
	public IncOutputSprY getNewInstanceOfThis() {
		return new IncOutputSprY(increment);
	}

	@Override
	public void process(Sprite sprite) {
		sprite.incY(increment);
	}

}
