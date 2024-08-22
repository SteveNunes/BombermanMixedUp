package frameset_tags;

public class SetOriginSprSize extends FrameTag {
	
	private int width;
	private int height;
	
	public SetOriginSprSize(int width, int height) {
		this.width = width;
		this.height = height;
	}

	public int getWidth()
		{ return width; }

	public int getHeight()
		{ return height; }	

	@Override
	public String toString()
		{ return "{" + FrameTag.getClassName(this) + ";" + width + ";" + height + "}"; }

	public SetOriginSprSize(String tags) {
		String[] params = FrameTag.validateStringTags(this, tags, 2);
		int n = 0;
		try {
			width = Integer.parseInt(params[n++]);
			height = Integer.parseInt(params[n++]);
		}
		catch (Exception e)
			{ throw new RuntimeException(params[--n] + " - Invalid parameter"); }
	}

	@Override
	public SetOriginSprSize getNewInstanceOfThis()
		{ return new SetOriginSprSize(width, height); }
	
}