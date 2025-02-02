package frameset_tags;

import frameset.Sprite;

public class SetInvencibleFrames extends FrameTag {

	public int value;

	public SetInvencibleFrames(int value) {
		this.value = value;
	}

	public SetInvencibleFrames(String tags) {
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
	public SetInvencibleFrames getNewInstanceOfThis() {
		return new SetInvencibleFrames(value);
	}

	@Override
	public void process(Sprite sprite) {
		sprite.getSourceEntity().setInvencibleFrames(value);
	}

}
