package frameset_tags;

import frameset.Sprite;
import util.Misc;

public class SetOutputSprPos extends FrameTag {

	public double x;
	public double y;

	public SetOutputSprPos(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public SetOutputSprPos(String tags) {
		sourceStringTags = tags;
		String[] params = validateStringTags(this, tags, 2);
		int n = 0;
		try {
			x = Double.parseDouble(params[n++]);
			y = Double.parseDouble(params[n++]);
		}
		catch (Exception e) {
    	Misc.addErrorOnLog(e, ".\\errors.log");
			throw new RuntimeException(params[--n] + " - Invalid parameter");
		}
	}

	@Override
	public SetOutputSprPos getNewInstanceOfThis() {
		return new SetOutputSprPos(x, y);
	}

	@Override
	public void process(Sprite sprite) {
		sprite.setX(x);
		sprite.setY(y);
	}

}
