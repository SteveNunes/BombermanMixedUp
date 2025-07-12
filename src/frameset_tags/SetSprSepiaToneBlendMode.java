package frameset_tags;

import frameset.Sprite;
import javafx.scene.effect.BlendMode;
import util.Misc;

public class SetSprSepiaToneBlendMode extends FrameTag {

	public BlendMode blendMode;

	public SetSprSepiaToneBlendMode(BlendMode blendMode) {
		this.blendMode = blendMode;
	}

	public SetSprSepiaToneBlendMode(String tags) {
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
	public SetSprSepiaToneBlendMode getNewInstanceOfThis() {
		return new SetSprSepiaToneBlendMode(blendMode);
	}

	@Override
	public void process(Sprite sprite) {
		sprite.getEffects().getSepiaTone().setBlendMode(blendMode);
	}

}
