package frameset_tags;

public class Goto extends FrameTag {
	
	private int index;
	private int repeatCycles;
	private int currentRepeatCycle;
	
	public Goto(int index, int repeatCycles) {
		if (repeatCycles < 0)
			throw new RuntimeException("repeat value must be equal or higher than zero");
		if (index < 0)
			throw new RuntimeException("index value must be equal or higher than zero");
		this.index = index;
		this.repeatCycles = repeatCycles;
		currentRepeatCycle = 0;
	}
	
	public Goto(int index)
		{ this(index, 0); }

	public int getIndex()
		{ return index; }	

	public int getRepeat()
		{ return index; }
	
	public void incCycles()
		{ currentRepeatCycle++; }
	
	public boolean haveLeftCycles()
		{ return repeatCycles == 0 || currentRepeatCycle < repeatCycles; }
	
	public void resetCycles()
		{ currentRepeatCycle = 0; }

	@Override
	public String toString()
		{ return "{" + FrameTag.getClassName(this) + ";" + index + ";" + repeatCycles + "}"; }

	public Goto(String tags) {
		String[] params = FrameTag.validateStringTags(this, tags, 2);
		int n = 0;
		try {
			index = Integer.parseInt(params[n++]);
			repeatCycles = Integer.parseInt(params[n++]);
		}
		catch (Exception e)
			{ throw new RuntimeException(params[--n] + " - Invalid parameter"); }
	}

}
