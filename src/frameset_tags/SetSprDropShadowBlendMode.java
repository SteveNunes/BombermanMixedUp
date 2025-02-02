package frameset_tags;

import frameset.Sprite;
import javafx.scene.effect.BlendMode;

public class SetSprDropShadowBlendMode extends FrameTag {

	public BlendMode blendMode;

	public SetSprDropShadowBlendMode(BlendMode blendMode) {
		this.blendMode = blendMode;
	}

	public SetSprDropShadowBlendMode(String tags) {
		sourceStringTags = tags;
		String[] params = validateStringTags(this, tags, 1);
		try {
			blendMode = BlendMode.valueOf(params[0]);
		}
		catch (Exception e) {
			throw new RuntimeException(params[0] + " - Invalid parameter");
		}
	}

	@Override
	public SetSprDropShadowBlendMode getNewInstanceOfThis() {
		return new SetSprDropShadowBlendMode(blendMode);
	}

	@Override
	public void process(Sprite sprite) {
		sprite.getEffects().getDropShadow().setBlendMode(blendMode);
	}

}
