package frameset_tags;

import frameset.Sprite;
import util.Misc;

public class IncTicksPerFrame extends FrameTag {

	public int increment;

	public IncTicksPerFrame(int increment) {
		this.increment = increment;
	}

	public IncTicksPerFrame(String tags) {
		sourceStringTags = tags;
		String[] params = validateStringTags(this, tags, 1);
		try {
			increment = Integer.parseInt(params[0]);
		}
		catch (Exception e) {
    	Misc.addErrorOnLog(e, ".\\errors.log");
			throw new RuntimeException(params[0] + " - Invalid parameter");
		}
	}

	@Override
	public IncTicksPerFrame getNewInstanceOfThis() {
		return new IncTicksPerFrame(increment);
	}

	@Override
	public void process(Sprite sprite) {
		sprite.getSourceFrameSet().incFramesPerTick(increment);
	}

}
