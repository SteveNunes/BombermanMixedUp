package frameset_tags;

import frameset.Sprite;
import javafx.scene.effect.BlendMode;

public class SetSprColorAdjustBlendMode extends FrameTag {

	public BlendMode blendMode;

	public SetSprColorAdjustBlendMode(BlendMode blendMode) {
		this.blendMode = blendMode;
	}

	@Override
	public String toString() {
		return "{" + getClassName(this) + ";" + blendMode.name() + "}";
	}

	public SetSprColorAdjustBlendMode(String tags) {
		String[] params = validateStringTags(this, tags, 1);
		try {
			blendMode = BlendMode.valueOf(params[0]);
		}
		catch (Exception e) {
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
