package frameset_tags;

import frameset.Sprite;
import javafx.scene.effect.BlendMode;
import util.Misc;

public class SetSprColorAdjustBlendMode extends FrameTag {

	public BlendMode blendMode;

	public SetSprColorAdjustBlendMode(BlendMode blendMode) {
		this.blendMode = blendMode;
	}

	public SetSprColorAdjustBlendMode(String tags) {
		sourceStringTags = tags;
		String[] params = validateStringTags(this, tags, 1);
		try {
			blendMode = BlendMode.valueOf(params[0]);
		}
		catch (Exception e) {
    	Misc.addErrorOnLog(e, ".\\errors.log");
			throw new RuntimeException(params[0] + " - Invalid parameter");
		}
	}

	@Override
	public SetSprColorAdjustBlendMode getNewInstanceOfThis() {
		return new SetSprColorAdjustBlendMode(blendMode);
	}

	@Override
	public void process(Sprite sprite) {
		sprite.getEffects().getColorAdjust().setBlendMode(blendMode);
	}

}
