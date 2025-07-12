package frameset_tags;

import frameset.Sprite;
import util.Misc;

public class SetSprIndex extends FrameTag {

	public Integer value;

	public SetSprIndex(Integer value) {
		this.value = value;
	}

	public SetSprIndex(String tags) {
		sourceStringTags = tags;
		String[] params = validateStringTags(this, tags, 1);
		try {
			value = params[0].equals("-") ? null : Integer.parseInt(params[0]);
		}
		catch (Exception e) {
    	Misc.addErrorOnLog(e, ".\\errors.log");
			throw new RuntimeException(params[0] + " - Invalid parameter");
		}
	}

	@Override
	public SetSprIndex getNewInstanceOfThis() {
		return new SetSprIndex(value);
	}

	@Override
	public void process(Sprite sprite) {
		if (value == null)
			sprite.setVisible(false);
		else {
			sprite.setVisible(true);
			sprite.setSpriteIndex(value);
		}
	}

}
