package frameset_tags;

import frameset.Sprite;

public class DelayInMillisTags extends FrameTag {

	public int value;

	public DelayInMillisTags(int value) {
		this.value = value;
	}

	public DelayInMillisTags(String tags) {
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
	public DelayInMillisTags getNewInstanceOfThis() {
		return new DelayInMillisTags(value);
	}

	@Override
	public void process(Sprite sprite) {}

}
