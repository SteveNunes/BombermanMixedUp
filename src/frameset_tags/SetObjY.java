package frameset_tags;

import frameset.Sprite;

public class SetObjY extends FrameTag {

	public int value;

	public SetObjY(int value) {
		this.value = value;
	}

	public SetObjY(String tags) {
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
	public SetObjY getNewInstanceOfThis() {
		return new SetObjY(value);
	}

	@Override
	public void process(Sprite sprite) {
		sprite.getSourceFrameSet().setY(value);
	}

}
