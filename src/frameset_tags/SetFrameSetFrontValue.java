package frameset_tags;

import frameset.Sprite;
import util.Misc;

public class SetFrameSetFrontValue extends FrameTag {

	public Integer value;

	public SetFrameSetFrontValue(Integer value) {
		this.value = value;
	}

	public SetFrameSetFrontValue(String tags) {
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
	public SetFrameSetFrontValue getNewInstanceOfThis() {
		return new SetFrameSetFrontValue(value);
	}

	@Override
	public void process(Sprite sprite) {
		sprite.getSourceFrameSet().setFrontValue(value);
	}

}
