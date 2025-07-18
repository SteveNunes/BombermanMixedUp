package frameset_tags;

import frameset.Sprite;
import util.Misc;

public class IncSprColorAdjustValues extends FrameTag {

	public double incrementHue;
	public double incrementSaturation;
	public double incrementBrightness;

	public IncSprColorAdjustValues(double incrementHue, double incrementSaturation, double incrementBrightness) {
		this.incrementHue = incrementHue;
		this.incrementSaturation = incrementSaturation;
		this.incrementBrightness = incrementBrightness;
	}

	public IncSprColorAdjustValues(String tags) {
		sourceStringTags = tags;
		String[] params = validateStringTags(this, tags, 3);
		int n = 0;
		try {
			incrementHue = Double.parseDouble(params[n++]);
			incrementSaturation = Double.parseDouble(params[n++]);
			incrementBrightness = Double.parseDouble(params[n++]);
		}
		catch (Exception e) {
    	Misc.addErrorOnLog(e, ".\\errors.log");
			throw new RuntimeException(params[--n] + " - Invalid parameter");
		}
	}

	@Override
	public IncSprColorAdjustValues getNewInstanceOfThis() {
		return new IncSprColorAdjustValues(incrementHue, incrementSaturation, incrementBrightness);
	}

	@Override
	public void process(Sprite sprite) {
		sprite.getEffects().getColorAdjust().incValues(incrementHue, incrementSaturation, incrementBrightness);
	}

}
