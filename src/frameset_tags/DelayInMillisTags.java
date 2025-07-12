package frameset_tags;

import frameset.Sprite;
import util.Misc;

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
    	Misc.addErrorOnLog(e, ".\\errors.log");
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
