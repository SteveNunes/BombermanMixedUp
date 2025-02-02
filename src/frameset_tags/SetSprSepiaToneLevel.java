package frameset_tags;

import frameset.Sprite;

public class SetSprSepiaToneLevel extends FrameTag {

	public double value;

	public SetSprSepiaToneLevel(double value) {
		this.value = value;
	}

	public SetSprSepiaToneLevel(String tags) {
		sourceStringTags = tags;
		String[] params = validateStringTags(this, tags, 1);
		try {
			value = Double.parseDouble(params[0]);
		}
		catch (Exception e) {
			throw new RuntimeException(params[0] + " - Invalid parameter");
		}
	}

	@Override
	public SetSprSepiaToneLevel getNewInstanceOfThis() {
		return new SetSprSepiaToneLevel(value);
	}

	@Override
	public void process(Sprite sprite) {
		sprite.getEffects().getSepiaTone().setLevel(value);
	}

}
