package frameset_tags;

import frameset.Sprite;

public class DelayInFramesTags extends FrameTag {

	public int value;

	public DelayInFramesTags(int value) {
		this.value = value;
	}

	public DelayInFramesTags(String tags) {
		sourceStringTags = tags;
		String[] params = validateStringTags(this, tags, 1);
		try {
			value = Integer.parseInt(params[0]);
		}
		catch (Exception e) {
			throw new RuntimeException(params[0] + " - Invalid parameter");
		}
	}

	@Override
	public DelayInFramesTags getNewInstanceOfThis() {
		return new DelayInFramesTags(value);
	}

	@Override
	public void process(Sprite sprite) {}

}
