package frameset_tags;

import frameset.Sprite;
import util.Misc;

public class IncSprColorTintValues extends FrameTag {

	public double incrementRed;
	public double incrementGreen;
	public double incrementBlue;
	public double incrementAlpha;

	public IncSprColorTintValues(double incrementRed, double incrementGreen, double incrementBlue, double incrementAlpha) {
		this.incrementRed = incrementRed;
		this.incrementGreen = incrementGreen;
		this.incrementBlue = incrementBlue;
		this.incrementAlpha = incrementAlpha;
	}

	public IncSprColorTintValues(double incrementRed, double incrementGreen, double incrementBlue) {
		this(incrementRed, incrementGreen, incrementBlue, 1);
	}

	public IncSprColorTintValues(String tags) {
		sourceStringTags = tags;
		String[] params = validateStringTags(this, tags, 4);
		int n = 0;
		try {
			incrementRed = Double.parseDouble(params[n++]);
			incrementGreen = Double.parseDouble(params[n++]);
			incrementBlue = Double.parseDouble(params[n++]);
			incrementAlpha = Double.parseDouble(params[n++]);
		}
		catch (Exception e) {
    	Misc.addErrorOnLog(e, ".\\errors.log");
			throw new RuntimeException(params[--n] + " - Invalid parameter");
		}
	}

	@Override
	public IncSprColorTintValues getNewInstanceOfThis() {
		return new IncSprColorTintValues(incrementRed, incrementGreen, incrementBlue, incrementAlpha);
	}

	@Override
	public void process(Sprite sprite) {
		sprite.getEffects().getColorTint().incRed(incrementRed);
		sprite.getEffects().getColorTint().incGreen(incrementGreen);
		sprite.getEffects().getColorTint().incBlue(incrementBlue);
		sprite.getEffects().getColorTint().incAlpha(incrementAlpha);
	}

}
