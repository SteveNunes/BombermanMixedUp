package frameset_tags;

import frameset.FrameSet;
import frameset.Sprite;

public class Goto extends FrameTag {

	public int index;
	public int repeatCycles;
	public int currentRepeatCycle;

	public Goto(int index, int repeatCycles) {
		/*
		 * Index menor que 0 decrementa o INDEX atual do FrameSet no valor informado.
		 * Index igual ou maior que 0 define o INDEX atual do FrameSet para o valor
		 * informado.
		 */
		if (repeatCycles < 0)
			throw new RuntimeException("repeat value must be equal or higher than zero");
		this.index = index;
		this.repeatCycles = repeatCycles;
		currentRepeatCycle = 0;
	}

	public Goto(int index) {
		this(index, 0);
	}

	public void resetCycles() {
		currentRepeatCycle = 0;
	}

	public boolean haveLeftCycles() {
		return repeatCycles == 0 || currentRepeatCycle < repeatCycles;
	}

	@Override
	public String toString() {
		return "{" + getClassName(this) + ";" + index + (repeatCycles == 0 ? ("}") : (";" + repeatCycles + "}"));
	}

	public Goto(String tags) {
		String[] params = validateStringTags(this, tags);
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
		catch (Exception e) {
			throw new RuntimeException(params[n] + " - Invalid parameter");
		}
	}

	@Override
	public Goto getNewInstanceOfThis() {
		return new Goto(index, repeatCycles);
	}

	@Override
	public void process(Sprite sprite) {
		FrameSet frameSet = sprite.getSourceFrameSet();
		if (this.index == frameSet.getCurrentFrameIndex())
			return;
		if (!frameSet.isStopped()) {
			if (haveLeftCycles()) {
				currentRepeatCycle++;
				int index = this.index < 0 ? frameSet.getCurrentFrameIndex() + this.index : this.index;
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
