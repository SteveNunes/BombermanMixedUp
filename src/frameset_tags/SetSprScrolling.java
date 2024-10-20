package frameset_tags;

import frameset.Sprite;

public class SetSprScrolling extends FrameTag {
	
	public double incrementX;
	public double incrementY;
	
	public SetSprScrolling(double incrementX, double incrementY) {
		this.incrementX = incrementX;
		this.incrementY = incrementY;
		super.deleteMeAfterFirstRead = true;
	}

	@Override
	public String toString()
		{ return "{" + FrameTag.getClassName(this) + ";" + incrementX + ";" + incrementY + "}"; }

	public SetSprScrolling(String tags) {
		String[] params = FrameTag.validateStringTags(this, tags, 2);
		int n = 0;
		try {
			incrementX = Double.parseDouble(params[n++]);
			incrementY = Double.parseDouble(params[n++]);
		}
		catch (Exception e)
			{ throw new RuntimeException(params[--n] + " - Invalid parameter"); }
	}

	@Override
	public SetSprScrolling getNewInstanceOfThis()
		{ return new SetSprScrolling(incrementX, incrementY); }

	@Override
	public void process(Sprite sprite)
		{ sprite.setSpriteScroll(incrementX, incrementY); }

}
