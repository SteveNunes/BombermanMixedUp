package frameset_tags;

import frameset.Sprite;
import javafx.scene.effect.BlendMode;
import util.Misc;

public class SetSprInnerShadowValues extends FrameTag {

	public double offsetX;
	public double offsetY;
	public BlendMode blendMode;

	public SetSprInnerShadowValues(double offsetX, double offsetY, BlendMode blendMode) {
		this.offsetX = offsetX;
		this.offsetY = offsetY;
		this.blendMode = blendMode;
	}

	public SetSprInnerShadowValues(double offsetX, double offsetY) {
		this(offsetX, offsetY, BlendMode.SRC_ATOP);
	}

	public SetSprInnerShadowValues(String tags) {
		sourceStringTags = tags;
		String[] params = validateStringTags(this, tags, 3);
		int n = 0;
		try {
			offsetX = Double.parseDouble(params[n++]);
			offsetY = Double.parseDouble(params[n++]);
			blendMode = BlendMode.valueOf(params[n++]);
		}
		catch (Exception e) {
    	Misc.addErrorOnLog(e, ".\\errors.log");
			throw new RuntimeException(params[--n] + " - Invalid parameter");
		}
	}

	@Override
	public SetSprInnerShadowValues getNewInstanceOfThis() {
		return new SetSprInnerShadowValues(offsetX, offsetY, blendMode);
	}

	@Override
	public void process(Sprite sprite) {
		sprite.getEffects().setInnerShadow(offsetX, offsetY, blendMode);
	}

}
