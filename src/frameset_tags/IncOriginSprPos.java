package frameset_tags;

public class IncOriginSprPos extends FrameTag {
	
	private int incrementX;
	private int incrementY;
	
	public IncOriginSprPos(int incrementX, int incrementY) {
		this.incrementX = incrementX;
		this.incrementY = incrementY;
	}

	public int getIncrementX()
		{ return incrementX; }

	public int getIncrementY()
		{ return incrementY; }		

	@Override
	public String toString()
		{ return "{" + FrameTag.getClassName(this) + ";" + incrementX + ";" + incrementY + "}"; }

	public IncOriginSprPos(String tags) {
		String[] params = FrameTag.validateStringTags(this, tags, 2);
		int n = 0;
		try {
			incrementX = Integer.parseInt(params[n++]);
			incrementY = Integer.parseInt(params[n++]);
		}
		catch (Exception e)
			{ throw new RuntimeException(params[--n] + " - Invalid parameter"); }
	}

}
