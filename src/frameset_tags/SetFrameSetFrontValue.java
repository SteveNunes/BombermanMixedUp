package frameset_tags;

import frameset.Sprite;

public class SetFrameSetFrontValue extends FrameTag {

	public Integer value;

	public SetFrameSetFrontValue(Integer value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return "{" + getClassName(this) + ";" + value + "}";
	}

	public SetFrameSetFrontValue(String tags) {
		String[] params = validateStringTags(this, tags, 1);
		try {
			value = Integer.parseInt(params[0]);
		}
		catch (Exception e) {
			throw new RuntimeException(params[0] + " - Invalid parameter");
		}
	}

	@Override
	public SetFrameSetFrontValue getNewInstanceOfThis() {
		return new SetFrameSetFrontValue(value);
	}

	@Override
	public void process(Sprite sprite) {
		sprite.getSourceFrameSet().setFrontValue(value);
	}

}
