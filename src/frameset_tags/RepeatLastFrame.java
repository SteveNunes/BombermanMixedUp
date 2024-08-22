package frameset_tags;

public class RepeatLastFrame extends FrameTag {
	
	private int repeatCycles;
	private int currentRepeatCycle;
	
	public RepeatLastFrame(int repeatCycles) {
		if (repeatCycles < 0)
			throw new RuntimeException("repeat value must be equal or higher than zero");
		this.repeatCycles = repeatCycles;
		currentRepeatCycle = 0;
	}
	
	public int getRepeatCycles()
		{ return repeatCycles; }
	
	public void incCycles()
		{ currentRepeatCycle++; }
	
	public boolean haveLeftCycles()
		{ return repeatCycles == 0 || currentRepeatCycle < repeatCycles; }
	
	public void resetCycles()
		{ currentRepeatCycle = 0; }

	@Override
	public String toString()
		{ return "{" + FrameTag.getClassName(this) + ";" + repeatCycles + "}"; }

	public RepeatLastFrame(String tags) {
		String[] params = FrameTag.validateStringTags(this, tags, 1);
		try {
			repeatCycles = Integer.parseInt(params[0]);
			currentRepeatCycle = 0;
		}
		catch (Exception e)
			{ throw new RuntimeException(params[0] + " - Invalid parameter"); }
	}

	@Override
	public RepeatLastFrame getNewInstanceOfThis()
		{ return new RepeatLastFrame(repeatCycles); }
	
}
