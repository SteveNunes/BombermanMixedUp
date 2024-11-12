package frameset_tags;

import frameset.Sprite;

public class SetDisabled extends FrameTag {

	public boolean state;

	public SetDisabled(boolean state) {
		this.state = state;
	}

	@Override
	public String toString() {
		return "{" + getClassName(this) + ";" + state + "}";
	}

	public SetDisabled(String tags) {
		String[] params = validateStringTags(this, tags);
		if (params.length > 1)
			throw new RuntimeException(tags + " - Too much parameters");
		try {
			state = params.length == 0 ? true : Boolean.parseBoolean(params[0]);
		}
		catch (Exception e) {
			throw new RuntimeException(params[0] + " - Invalid parameter");
		}
	}

	@Override
	public SetDisabled getNewInstanceOfThis() {
		return new SetDisabled(state);
	}

	@Override
	public void process(Sprite sprite) {
		if (state)
			sprite.getSourceEntity().setDisabled();
		else
			sprite.getSourceEntity().setEnabled();
	}

}
