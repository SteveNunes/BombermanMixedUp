package frameset_tags;

import frameset.Sprite;
import javafx.scene.effect.BlendMode;
import util.Misc;

public class SetSprDropShadowValues extends FrameTag {

	public double offsetX;
	public double offsetY;
	public BlendMode blendMode;

	public SetSprDropShadowValues(double offsetX, double offsetY, BlendMode blendMode) {
		this.offsetX = offsetX;
		this.offsetY = offsetY;
		this.blendMode = blendMode;
	}

	public SetSprDropShadowValues(double offsetX, double offsetY) {
		this(offsetX, offsetY, BlendMode.SRC_ATOP);
	}

	public SetSprDropShadowValues(String tags) {
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
	public SetSprDropShadowValues getNewInstanceOfThis() {
		return new SetSprDropShadowValues(offsetX, offsetY, blendMode);
	}

	@Override
	public void process(Sprite sprite) {
		sprite.getEffects().setDropShadow(offsetX, offsetY, blendMode);
	}

}
