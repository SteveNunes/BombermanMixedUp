package frameset_tags;

import frameset.Sprite;

public class IncOriginSprPos extends FrameTag {
	
	public int incrementX;
	public int incrementY;
	
	public IncOriginSprPos(int incrementX, int incrementY) {
		this.incrementX = incrementX;
		this.incrementY = incrementY;
	}

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

	@Override
	public IncOriginSprPos getNewInstanceOfThis()
		{ return new IncOriginSprPos(incrementX, incrementY); }
	
	@Override
	public void process(Sprite sprite) {
		sprite.incOriginSpriteX(incrementX);
		sprite.incOriginSpriteY(incrementY);
	}

}






