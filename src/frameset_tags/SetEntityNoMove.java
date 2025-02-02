package frameset_tags;

import frameset.Sprite;

public class SetEntityNoMove extends FrameTag {

	public boolean value;

	public SetEntityNoMove(boolean value) {
		this.value = value;
	}

	public SetEntityNoMove(String tags) {
		sourceStringTags = tags;
		String[] params = validateStringTags(this, tags, 1);
		try {
			value = Boolean.parseBoolean(params[0]);
		}
		catch (Exception e) {
			throw new RuntimeException(params[0] + " - Invalid parameter");
		}
	}

	@Override
	public SetEntityNoMove getNewInstanceOfThis() {
		return new SetEntityNoMove(value);
	}

	@Override
	public void process(Sprite sprite) {
		sprite.getSourceEntity().setNoMove(value);
	}

}
