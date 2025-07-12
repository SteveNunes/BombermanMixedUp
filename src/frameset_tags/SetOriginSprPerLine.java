package frameset_tags;

import frameset.Sprite;
import util.Misc;

public class SetOriginSprPerLine extends FrameTag {

	public int value;

	public SetOriginSprPerLine(int value) {
		this.value = value;
	}

	public SetOriginSprPerLine(String tags) {
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
	public SetOriginSprPerLine getNewInstanceOfThis() {
		return new SetOriginSprPerLine(value);
	}

	@Override
	public void process(Sprite sprite) {
		sprite.setSpritesPerLine(value);
	}

}
