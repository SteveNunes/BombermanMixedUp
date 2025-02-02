package frameset_tags;

import frameset.Sprite;

public class SetObjX extends FrameTag {

	public int value;

	public SetObjX(int value) {
		this.value = value;
	}

	public SetObjX(String tags) {
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
	public SetObjX getNewInstanceOfThis() {
		return new SetObjX(value);
	}

	@Override
	public void process(Sprite sprite) {
		sprite.getSourceFrameSet().setX(value);
	}

}
