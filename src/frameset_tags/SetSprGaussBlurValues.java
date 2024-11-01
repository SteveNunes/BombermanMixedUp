package frameset_tags;

import frameset.Sprite;
import javafx.scene.effect.BlendMode;

public class SetSprGaussBlurValues extends FrameTag {

	public int radius;
	public BlendMode blendMode;

	public SetSprGaussBlurValues(int radius, BlendMode blendMode) {
		this.radius = radius;
		this.blendMode = blendMode;
	}

	public SetSprGaussBlurValues(int radius) {
		this(radius, BlendMode.SRC_ATOP);
	}

	@Override
	public String toString() {
		return "{" + getClassName(this) + ";" + radius + ";" + blendMode.name() + "}";
	}

	public SetSprGaussBlurValues(String tags) {
		String[] params = validateStringTags(this, tags, 2);
		int n = 0;
		try {
			radius = Integer.parseInt(params[n++]);
			blendMode = BlendMode.valueOf(params[n++]);
		}
		catch (Exception e) {
			throw new RuntimeException(params[--n] + " - Invalid parameter");
		}
	}

	@Override
	public SetSprGaussBlurValues getNewInstanceOfThis() {
		return new SetSprGaussBlurValues(radius, blendMode);
	}

	@Override
	public void process(Sprite sprite) {
		sprite.getEffects().setGaussianBlur(radius, blendMode);
	}

}
