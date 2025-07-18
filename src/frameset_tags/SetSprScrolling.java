package frameset_tags;

import frameset.Sprite;
import util.Misc;

public class SetSprScrolling extends FrameTag {

	public double incrementX;
	public double incrementY;

	public SetSprScrolling(double incrementX, double incrementY) {
		this.incrementX = incrementX;
		this.incrementY = incrementY;
		super.deleteMeAfterFirstRead = true;
	}

	public SetSprScrolling(String tags) {
		sourceStringTags = tags;
		String[] params = validateStringTags(this, tags, 2);
		int n = 0;
		try {
			incrementX = Double.parseDouble(params[n++]);
			incrementY = Double.parseDouble(params[n++]);
		}
		catch (Exception e) {
    	Misc.addErrorOnLog(e, ".\\errors.log");
			throw new RuntimeException(params[--n] + " - Invalid parameter");
		}
	}

	@Override
	public SetSprScrolling getNewInstanceOfThis() {
		return new SetSprScrolling(incrementX, incrementY);
	}

	@Override
	public void process(Sprite sprite) {
		sprite.setSpriteScroll(incrementX, incrementY);
	}

}
