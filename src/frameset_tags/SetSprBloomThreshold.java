package frameset_tags;

import frameset.Sprite;
import util.Misc;

public class SetSprBloomThreshold extends FrameTag {

	public double value;

	public SetSprBloomThreshold(double value) {
		this.value = value;
	}

	public SetSprBloomThreshold(String tags) {
		sourceStringTags = tags;
		String[] params = validateStringTags(this, tags, 1);
		try {
			value = Double.parseDouble(params[0]);
		}
		catch (Exception e) {
    	Misc.addErrorOnLog(e, ".\\errors.log");
			throw new RuntimeException(params[0] + " - Invalid parameter");
		}
	}

	@Override
	public SetSprBloomThreshold getNewInstanceOfThis() {
		return new SetSprBloomThreshold(value);
	}

	@Override
	public void process(Sprite sprite) {
		sprite.getEffects().getBloom().setThreshold(value);
	}

}
