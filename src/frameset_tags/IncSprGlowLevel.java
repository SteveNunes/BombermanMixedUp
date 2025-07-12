package frameset_tags;

import frameset.Sprite;
import util.Misc;

public class IncSprGlowLevel extends FrameTag {

	public int increment;

	public IncSprGlowLevel(int increment) {
		this.increment = increment;
	}

	public IncSprGlowLevel(String tags) {
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
	public IncSprGlowLevel getNewInstanceOfThis() {
		return new IncSprGlowLevel(increment);
	}

	@Override
	public void process(Sprite sprite) {
		sprite.getEffects().setGlow(sprite.getEffects().getGlow().getLevel() + increment, sprite.getEffects().getGlow().getBlendMode());
	}

}
