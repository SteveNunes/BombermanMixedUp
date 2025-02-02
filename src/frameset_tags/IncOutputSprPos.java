package frameset_tags;

import frameset.Sprite;

public class IncOutputSprPos extends FrameTag {

	public double incrementX;
	public double incrementY;

	public IncOutputSprPos(double incrementX, double incrementY) {
		this.incrementX = incrementX;
		this.incrementY = incrementY;
	}

	public IncOutputSprPos(String tags) {
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
	public IncOutputSprPos getNewInstanceOfThis() {
		return new IncOutputSprPos(incrementX, incrementY);
	}

	@Override
	public void process(Sprite sprite) {
		sprite.incX(incrementX);
		sprite.incY(incrementY);
	}

}
