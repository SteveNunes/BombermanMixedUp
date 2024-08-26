package frameset_tags;

import entities.Sprite;

public class IncOutputSprSize extends FrameTag {
	
	private int incrementWidth;
	private int incrementHeight;
	
	public IncOutputSprSize(int incrementWidth, int incrementHeight) {
		this.incrementWidth = incrementWidth;
		this.incrementHeight = incrementHeight;
	}

	public int getIncrementWidth()
		{ return incrementWidth; }

	public int getIncrementHeight()
		{ return incrementHeight; }

	@Override
	public String toString()
		{ return "{" + FrameTag.getClassName(this) + ";" + incrementWidth + ";" + incrementHeight + "}"; }
	
	public IncOutputSprSize(String tags) {
		String[] params = FrameTag.validateStringTags(this, tags, 2);
		int n = 0;
		try {
			incrementWidth = Integer.parseInt(params[n++]);
			incrementHeight = Integer.parseInt(params[n++]);
		}
		catch (Exception e)
			{ throw new RuntimeException(params[--n] + " - Invalid parameter"); }
	}

	@Override
	public IncOriginSprPos getNewInstanceOfThis()
		{ return new IncOriginSprPos(incrementWidth, incrementHeight); }
	
	@Override
	public void process(Sprite sprite) {
		sprite.incOutputWidth(getIncrementWidth());
		sprite.incOutputHeight(getIncrementHeight());
	}

	@Override
	public void reset() {
	}

}
