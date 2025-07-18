package frameset_tags;

import frameset.Sprite;
import javafx.scene.effect.BlendMode;
import util.Misc;

public class SetSprMotionBlurBlendMode extends FrameTag {

	public BlendMode blendMode;

	public SetSprMotionBlurBlendMode(BlendMode blendMode) {
		this.blendMode = blendMode;
	}

	public SetSprMotionBlurBlendMode(String tags) {
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
	public SetSprMotionBlurBlendMode getNewInstanceOfThis() {
		return new SetSprMotionBlurBlendMode(blendMode);
	}

	@Override
	public void process(Sprite sprite) {
		sprite.getEffects().getMotionBlur().setBlendMode(blendMode);
	}

}
