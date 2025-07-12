package frameset_tags;

import frameset.Sprite;
import util.Misc;

public class SetBlinkingFrames extends FrameTag {

	public int value;

	public SetBlinkingFrames(int value) {
		this.value = value;
	}

	public SetBlinkingFrames(String tags) {
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
	public SetBlinkingFrames getNewInstanceOfThis() {
		return new SetBlinkingFrames(value);
	}

	@Override
	public void process(Sprite sprite) {
		sprite.getSourceEntity().setBlinkingFrames(value);
	}

}
