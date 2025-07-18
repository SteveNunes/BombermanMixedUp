package frameset_tags;

import frameset.Sprite;
import javafx.scene.effect.BlendMode;
import util.Misc;

public class SetSprGaussBlurBlendMode extends FrameTag {

	public BlendMode blendMode;

	public SetSprGaussBlurBlendMode(BlendMode blendMode) {
		this.blendMode = blendMode;
	}

	public SetSprGaussBlurBlendMode(String tags) {
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
	public SetSprGaussBlurBlendMode getNewInstanceOfThis() {
		return new SetSprGaussBlurBlendMode(blendMode);
	}

	@Override
	public void process(Sprite sprite) {
		sprite.getEffects().setGaussianBlur(sprite.getEffects().getGaussianBlur().getRadius(), blendMode);
	}

}
