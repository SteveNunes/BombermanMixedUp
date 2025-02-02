package frameset_tags;

import frameset.Sprite;

public class SetFrameSet extends FrameTag {

	public String value;

	public SetFrameSet(String tags) {
		sourceStringTags = tags;
		String[] params = validateStringTags(this, tags, 1);
		value = params[0];
	}

	@Override
	public SetFrameSet getNewInstanceOfThis() {
		return new SetFrameSet(sourceStringTags);
	}

	@Override
	public void process(Sprite sprite) {
		sprite.getSourceEntity().setFrameSet(value);
	}

}
