package frameset_tags;

import enums.DirectionOrientation;
import frameset.Sprite;
import objmoveutils.EliticMove;
import util.Misc;

public class SetSprEliticMove extends FrameTag {

	public DirectionOrientation orientation;
	public double radiusWidth;
	public double radiusHeight;
	public double speed;

	public SetSprEliticMove(DirectionOrientation orientation, double radiusWidth, double radiusHeight, double speed) {
		this.orientation = orientation;
		this.radiusWidth = radiusWidth;
		this.radiusHeight = radiusHeight;
		this.speed = speed;
	}

	public SetSprEliticMove(String tags) {
		sourceStringTags = tags;
		String[] params = validateStringTags(this, tags, 4);
		int n = 0;
		try {
			orientation = DirectionOrientation.valueOf(params[n++]);
			radiusWidth = Double.parseDouble(params[n++]);
			radiusHeight = Double.parseDouble(params[n++]);
			speed = Double.parseDouble(params[n++]);
		}
		catch (Exception e) {
    	Misc.addErrorOnLog(e, ".\\errors.log");
			throw new RuntimeException(params[--n] + " - Invalid parameter");
		}
	}

	@Override
	public SetSprEliticMove getNewInstanceOfThis() {
		return new SetSprEliticMove(orientation, radiusWidth, radiusHeight, speed);
	}

	@Override
	public void process(Sprite sprite) {
		if (sprite.getEliticMove() == null)
			sprite.setEliticMove(new EliticMove(orientation, radiusWidth, radiusHeight, speed));
	}

}
