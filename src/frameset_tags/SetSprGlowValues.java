package frameset_tags;

import frameset.Sprite;
import javafx.scene.effect.BlendMode;

public class SetSprGlowValues extends FrameTag {

	public double level;
	public BlendMode blendMode;

	public SetSprGlowValues(double level, BlendMode blendMode) {
		this.level = level;
		this.blendMode = blendMode;
	}

	public SetSprGlowValues(double threshold) {
		this(threshold, BlendMode.SRC_ATOP);
	}

	@Override
	public String toString() {
		return "{" + getClassName(this) + ";" + level + ";" + blendMode.name() + "}";
	}

	public SetSprGlowValues(String tags) {
		String[] params = validateStringTags(this, tags, 2);
		int n = 0;
		try {
			level = Double.parseDouble(params[n++]);
			blendMode = BlendMode.valueOf(params[n++]);
		}
		catch (Exception e) {
			throw new RuntimeException(params[--n] + " - Invalid parameter");
		}
	}

	@Override
	public SetSprGlowValues getNewInstanceOfThis() {
		return new SetSprGlowValues(level, blendMode);
	}

	@Override
	public void process(Sprite sprite) {
		sprite.getEffects().setGlow(level, blendMode);
	}

}
