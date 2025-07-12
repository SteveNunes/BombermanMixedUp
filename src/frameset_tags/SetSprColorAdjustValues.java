package frameset_tags;

import frameset.Sprite;
import javafx.scene.effect.BlendMode;
import util.Misc;

public class SetSprColorAdjustValues extends FrameTag {

	public double hue;
	public double saturation;
	public double brightness;
	public BlendMode blendMode;

	public SetSprColorAdjustValues(double hue, double saturation, double brightness, BlendMode blendMode) {
		this.hue = hue;
		this.saturation = saturation;
		this.brightness = brightness;
		this.blendMode = blendMode;
	}

	public SetSprColorAdjustValues(double hue, double saturation, double brightness) {
		this(hue, saturation, brightness, BlendMode.SRC_ATOP);
	}

	public SetSprColorAdjustValues(String tags) {
		sourceStringTags = tags;
		String[] params = validateStringTags(this, tags, 4);
		int n = 0;
		try {
			hue = Double.parseDouble(params[n++]);
			saturation = Double.parseDouble(params[n++]);
			brightness = Double.parseDouble(params[n++]);
			blendMode = BlendMode.valueOf(params[n++]);
		}
		catch (Exception e) {
    	Misc.addErrorOnLog(e, ".\\errors.log");
			throw new RuntimeException(params[--n] + " - Invalid parameter");
		}
	}

	@Override
	public SetSprColorAdjustValues getNewInstanceOfThis() {
		return new SetSprColorAdjustValues(hue, saturation, brightness, blendMode);
	}

	@Override
	public void process(Sprite sprite) {
		sprite.getEffects().setColorAdjust(hue, saturation, brightness, blendMode);
	}

}
