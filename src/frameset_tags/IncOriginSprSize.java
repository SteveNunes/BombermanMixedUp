package frameset_tags;

import frameset.Sprite;
import tools.GameMisc;

public class IncOriginSprSize extends FrameTag {
	
	private int incrementWidth;
	private int incrementHeight;
	
	public IncOriginSprSize(int incrementWidth, int incrementHeight) {
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
	
	public IncOriginSprSize(String tags) {
		String[] params = FrameTag.validateStringTags(this, tags, 2);
		int n = 0;
		try {
			incrementWidth = Integer.parseInt(params[n++]);
			incrementHeight = Integer.parseInt(params[n++]);
		}
		catch (Exception e)
			{ GameMisc.throwRuntimeException(params[--n] + " - Invalid parameter"); }
	}

	@Override
	public IncOriginSprSize getNewInstanceOfThis()
		{ return new IncOriginSprSize(incrementWidth, incrementHeight); }
	
	@Override
	public void process(Sprite sprite) {
		sprite.incOriginSpriteWidth(getIncrementWidth());
		sprite.incOriginSpriteHeight(getIncrementHeight());
	}

}
