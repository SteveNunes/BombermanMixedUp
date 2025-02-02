package frameset_tags;

import entities.Ride;
import frameset.Sprite;

public class IncEntityPos extends FrameTag {

	public double incrementX;
	public double incrementY;

	public IncEntityPos(double incrementX, double incrementY) {
		this.incrementX = incrementX;
		this.incrementY = incrementY;
	}

	public IncEntityPos(String tags) {
		sourceStringTags = tags;
		String[] params = validateStringTags(this, tags, 2);
		int n = 0;
		try {
			incrementX = Double.parseDouble(params[n++]);
			incrementY = Double.parseDouble(params[n++]);
		}
		catch (Exception e) {
			throw new RuntimeException(params[--n] + " - Invalid parameter");
		}
	}

	@Override
	public IncEntityPos getNewInstanceOfThis() {
		return new IncEntityPos(incrementX, incrementY);
	}

	@Override
	public void process(Sprite sprite) {
		if (sprite.getSourceEntity() instanceof Ride) {
			((Ride)sprite.getSourceEntity()).getOwner().incX(incrementX);
			((Ride)sprite.getSourceEntity()).getOwner().incY(incrementY);
		}
		else {
			sprite.getSourceEntity().incX(incrementX);
			sprite.getSourceEntity().incY(incrementY);
		}
	}

}
