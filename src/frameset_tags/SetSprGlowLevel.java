package frameset_tags;

import frameset.Sprite;
import util.Misc;

public class SetSprGlowLevel extends FrameTag {

	public double value;

	public SetSprGlowLevel(double value) {
		this.value = value;
	}

	public SetSprGlowLevel(String tags) {
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
	public SetSprGlowLevel getNewInstanceOfThis() {
		return new SetSprGlowLevel(value);
	}

	@Override
	public void process(Sprite sprite) {
		sprite.getEffects().getGlow().setLevel(value);
	}

}
