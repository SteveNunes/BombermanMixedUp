package frameset_tags;

import application.Main;
import frameset.FrameSet;
import frameset.Sprite;
import tools.GameMisc;

public class RepeatLastFrame extends FrameTag {
	
	private int repeatCycles;
	private int currentRepeatCycle;
	
	public RepeatLastFrame(int repeatCycles) {
		if (repeatCycles < 0)
			GameMisc.throwRuntimeException("repeat value must be equal or higher than zero");
		this.repeatCycles = repeatCycles;
		currentRepeatCycle = 0;
	}
	
	public int getRepeatCycles()
		{ return repeatCycles; }
	
	public void incCycles()
		{ currentRepeatCycle++; }
	
	public void resetCycles()
		{ currentRepeatCycle = 0; }

	public boolean haveLeftCycles()
		{ return repeatCycles == 0 || currentRepeatCycle < repeatCycles; }
	
	@Override
	public String toString()
		{ return "{" + FrameTag.getClassName(this) + (repeatCycles == 0 ? ("}") : (";" + repeatCycles + "}")); }

	public RepeatLastFrame(String tags) {
		String[] params = FrameTag.validateStringTags(this, tags);
		if (params.length > 1)
			GameMisc.throwRuntimeException(tags + " - Too much parameters");
		if (params.length < 1)
			GameMisc.throwRuntimeException(tags + " - Too few parameters");
		try {
			repeatCycles = params.length < 1 ? 0 : Integer.parseInt(params[0]);
			currentRepeatCycle = 0;
		}
		catch (Exception e)
			{ GameMisc.throwRuntimeException(params[0] + " - Invalid parameter"); }
	}

	@Override
	public RepeatLastFrame getNewInstanceOfThis()
		{ return new RepeatLastFrame(repeatCycles); }
	
	@Override
	public void process(Sprite sprite) {
		FrameSet frameSet = sprite.getMainFrameSet();
		if (!Main.frameSetEditorIsPaused() && !frameSet.isStopped()) {
			if (haveLeftCycles()) {
				incCycles();
				int index = frameSet.getCurrentFrameIndex() - 1;
				if (index < 0)
					index = 0;
				else if (index >= frameSet.getTotalFrames())
					index = frameSet.getTotalFrames() == 0 ? 0 : frameSet.getTotalFrames() - 1;
				frameSet.setCurrentFrameIndex(index);
			}
			else {
				resetCycles();
				frameSet.incFrameIndex();
			}
		}
	}

}
