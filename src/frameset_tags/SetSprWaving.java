package frameset_tags;

import frameset.Sprite;
import util.Misc;

public class SetSprWaving extends FrameTag {

	public int speed;
	public int[] wavingPattern;
	public boolean disable;

	public SetSprWaving(int speed, int[] wavingPattern) {
		this.speed = speed;
		this.wavingPattern = wavingPattern;
		disable = false;
	}

	public SetSprWaving(String tags) {
		sourceStringTags = tags;
		String[] params = validateStringTags(this, tags);
		if (params.length > 2)
			throw new RuntimeException(tags + " - Too much parameters");
		if (params.length > 0) {
			if (params[0].equals("disable")) {
				disable = true;
				return;
			}
			if (params[0].equals("enable")) {
				disable = false;
				return;
			}
		}
		int n = 0;
		try {
			speed = params.length < 1 || params[n = 0].equals("-") ? 1 : Integer.parseInt(params[n = 4]);
			wavingPattern = null;
			if (params.length == 2) {
				String[] split = params[n = 1].split(":");
				wavingPattern = new int[split.length];
				for (int x = 0; x < split.length; x++)
					wavingPattern[x] = Integer.parseInt(split[x]);
			}
		}
		catch (Exception e) {
    	Misc.addErrorOnLog(e, ".\\errors.log");
			throw new RuntimeException(params[n] + " - Invalid parameter");
		}
	}

	@Override
	public SetSprWaving getNewInstanceOfThis() {
		return new SetSprWaving(speed, wavingPattern);
	}

	@Override
	public void process(Sprite sprite) {
		if (sprite.getWavingImage() == null)
			sprite.setWavingImage(speed, wavingPattern);
		else if (disable)
			sprite.setWavingImage(null);
	}

}
