package frameset_tags;

import frameset.Sprite;
import util.Misc;

public class IncObjPos extends FrameTag {

	public double incrementX;
	public double incrementY;

	public IncObjPos(double incrementX, double incrementY) {
		this.incrementX = incrementX;
		this.incrementY = incrementY;
	}

	public IncObjPos(String tags) {
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
	public IncObjPos getNewInstanceOfThis() {
		return new IncObjPos(incrementX, incrementY);
	}

	@Override
	public void process(Sprite sprite) {
		sprite.getSourceFrameSet().incX(incrementX);
		sprite.getSourceFrameSet().incY(incrementY);
	}

}
