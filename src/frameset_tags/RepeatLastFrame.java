package frameset_tags;

import frameset.FrameSet;
import frameset.Sprite;

public class RepeatLastFrame extends FrameTag {

	public int repeatCycles;
	public int currentRepeatCycle;

	public RepeatLastFrame(int repeatCycles) {
		if (repeatCycles < 0)
			throw new RuntimeException("repeat value must be equal or higher than zero");
		this.repeatCycles = repeatCycles;
		currentRepeatCycle = 0;
	}

	public void incCycles() {
		currentRepeatCycle++;
	}

	public void resetCycles() {
		currentRepeatCycle = 0;
	}

	public boolean haveLeftCycles() {
		return repeatCycles == 0 || currentRepeatCycle < repeatCycles;
	}

	public RepeatLastFrame(String tags) {
		sourceStringTags = tags;
		String[] params = validateStringTags(this, tags);
		if (params.length > 1)
			throw new RuntimeException(tags + " - Too much parameters");
		if (params.length < 1)
			throw new RuntimeException(tags + " - Too few parameters");
		try {
			repeatCycles = params.length < 1 ? 0 : Integer.parseInt(params[0]);
			currentRepeatCycle = 0;
		}
		catch (Exception e) {
			throw new RuntimeException(params[0] + " - Invalid parameter");
		}
	}

	@Override
	public RepeatLastFrame getNewInstanceOfThis() {
		return new RepeatLastFrame(repeatCycles);
	}

	@Override
	public void process(Sprite sprite) {
		FrameSet frameSet = sprite.getSourceFrameSet();
		if (!frameSet.isStopped()) {
			if (haveLeftCycles()) {
				incCycles();
				int index = frameSet.getCurrentFrameIndex() - 1;
				if (index < 0 || index >= frameSet.getTotalFrames())
					index = 0;
				sprite.getSourceFrameSet().setCurrentFrameIndex(index);
			}
			else {
				resetCycles();
				sprite.getSourceFrameSet().incFrameIndex();
			}
		}
	}

}
