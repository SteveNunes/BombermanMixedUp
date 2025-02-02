package frameset_tags;

import frameset.Sprite;

public class SetSprFrontValue extends FrameTag {

	public Integer value;

	public SetSprFrontValue(Integer value) {
		this.value = value;
	}

	public SetSprFrontValue(String tags) {
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
	public SetSprFrontValue getNewInstanceOfThis() {
		return new SetSprFrontValue(value);
	}

	@Override
	public void process(Sprite sprite) {
		sprite.setFrontValue(value);
	}

}
