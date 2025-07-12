package frameset_tags;

import entities.Ride;
import frameset.Sprite;
import util.Misc;

public class IncEntityX extends FrameTag {

	public int increment;

	public IncEntityX(int increment) {
		this.increment = increment;
	}

	public IncEntityX(String tags) {
		sourceStringTags = tags;
		String[] params = validateStringTags(this, tags, 1);
		try {
			increment = Integer.parseInt(params[0]);
		}
		catch (Exception e) {
    	Misc.addErrorOnLog(e, ".\\errors.log");
			throw new RuntimeException(params[0] + " - Invalid parameter");
		}
	}

	@Override
	public IncEntityX getNewInstanceOfThis() {
		return new IncEntityX(increment);
	}

	@Override
	public void process(Sprite sprite) {
		if (sprite.getSourceEntity() instanceof Ride)
			((Ride)sprite.getSourceEntity()).getOwner().incX(increment);
		else
			sprite.getSourceEntity().incX(increment);
	}

}
