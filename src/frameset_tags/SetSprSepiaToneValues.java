package frameset_tags;

import frameset.Sprite;
import javafx.scene.effect.BlendMode;

public class SetSprSepiaToneValues extends FrameTag {

	public double level;
	public BlendMode blendMode;

	public SetSprSepiaToneValues(double level, BlendMode blendMode) {
		this.level = level;
		this.blendMode = blendMode;
	}

	public SetSprSepiaToneValues(double threshold) {
		this(threshold, BlendMode.SRC_ATOP);
	}

	public SetSprSepiaToneValues(String tags) {
		sourceStringTags = tags;
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
	public SetSprSepiaToneValues getNewInstanceOfThis() {
		return new SetSprSepiaToneValues(level, blendMode);
	}

	@Override
	public void process(Sprite sprite) {
		sprite.getEffects().setSepiaTone(level, blendMode);
	}

}
