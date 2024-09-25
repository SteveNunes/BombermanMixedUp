package frameset_tags;

import application.Main;
import frameset.FrameSet;
import frameset.Sprite;
import tools.Tools;

public class Goto extends FrameTag {
	
	private int index;
	private int repeatCycles;
	public int currentRepeatCycle;
	
	public Goto(int index, int repeatCycles) {
		/* Index menor que 0 decrementa o INDEX atual do FrameSet no valor informado.
		 * Index igual ou maior que 0 define o INDEX atual do FrameSet para o valor informado.
		 */
		if (repeatCycles < 0)
			throw new RuntimeException("repeat value must be equal or higher than zero");
		this.index = index;
		this.repeatCycles = repeatCycles;
		currentRepeatCycle = 0;
	}
	
	public Goto(int index)
		{ this(index, 0); }

	public int getIndex()
		{ return index; }	

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
		{ return "{" + FrameTag.getClassName(this) + ";" + index + (repeatCycles == 0 ? ("}") : (";" + repeatCycles + "}")); }

	public Goto(String tags) {
		String[] params = FrameTag.validateStringTags(this, tags);
		if (params.length > 2)
			throw new RuntimeException(tags + " - Too much parameters");
		if (params.length < 1)
			throw new RuntimeException(tags + " - Too few parameters");
		int n = 0;
		try {
			index = Integer.parseInt(params[n]);
			repeatCycles = params.length == 1 ? 0 : Integer.parseInt(params[++n]);
			currentRepeatCycle = 0;
		}
		catch (Exception e)
			{ throw new RuntimeException(params[n] + " - Invalid parameter"); }
	}

	@Override
	public Goto getNewInstanceOfThis()
		{ return new Goto(index, repeatCycles); }

	@Override
	public void process(Sprite sprite) {
		FrameSet frameSet = sprite.getMainFrameSet();
		if (!Main.frameSetEditorIsPaused() && !frameSet.isStopped()) {
			if (haveLeftCycles()) {
				incCycles();
				int index = getIndex() < 0 ? frameSet.getCurrentFrameIndex() + getIndex() : getIndex();
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
