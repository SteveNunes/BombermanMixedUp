package frameset_tags;

import frameset.Sprite;
import util.Misc;

public class SetTicksPerFrame extends FrameTag {

	public int value;

	public SetTicksPerFrame(int value) {
		this.value = value;
	}

	public SetTicksPerFrame(String tags) {
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
	public SetTicksPerFrame getNewInstanceOfThis() {
		return new SetTicksPerFrame(value);
	}

	@Override
	public void process(Sprite sprite) {
		sprite.getSourceFrameSet().setFramesPerTick(value);
	}

}
