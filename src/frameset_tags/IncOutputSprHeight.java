package frameset_tags;

import frameset.Sprite;
import util.Misc;

public class IncOutputSprHeight extends FrameTag {

	public int increment;

	public IncOutputSprHeight(int increment) {
		this.increment = increment;
	}

	public IncOutputSprHeight(String tags) {
		sourceStringTags = tags;
		String[] params = validateStringTags(this, tags, 1);
		try {
			increment = Integer.parseInt(params[0]);
		}
		catch (Exception e) {
    	Misc.addErrorOnLog(e, ".\\errors.log");
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
