package frameset_tags;

import frameset.Sprite;
import javafx.scene.effect.BlendMode;

public class SetSprBloomValues extends FrameTag {

	public double threshold;
	public BlendMode blendMode;

	public SetSprBloomValues(double threshold, BlendMode blendMode) {
		this.threshold = threshold;
		this.blendMode = blendMode;
	}

	public SetSprBloomValues(double threshold) {
		this(threshold, BlendMode.SRC_ATOP);
	}

	public SetSprBloomValues(String tags) {
		sourceStringTags = tags;
		String[] params = validateStringTags(this, tags, 2);
		int n = 0;
		try {
			threshold = Double.parseDouble(params[n++]);
			blendMode = BlendMode.valueOf(params[n++]);
		}
		catch (Exception e) {
			throw new RuntimeException(params[--n] + " - Invalid parameter");
		}
	}

	@Override
	public SetSprBloomValues getNewInstanceOfThis() {
		return new SetSprBloomValues(threshold, blendMode);
	}

	@Override
	public void process(Sprite sprite) {
		sprite.getEffects().setBloom(threshold, blendMode);
	}

}
