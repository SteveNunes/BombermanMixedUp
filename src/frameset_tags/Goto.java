package frameset_tags;

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
	
	public boolean haveLeftCycles()
		{ return repeatCycles == 0 || currentRepeatCycle < repeatCycles; }
	
	public void resetCycles()
		{ currentRepeatCycle = 0; }

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

}
