package frameset_tags;

import enums.DirectionOrientation;
import frameset.Sprite;
import objmoveutils.EliticMove;

public class SetEliticMove extends FrameTag {

	public DirectionOrientation orientation;
	public double radiusW;
	public double radiusH;
	public double speed;
	public double initialAngle;
	
	
	public SetEliticMove(DirectionOrientation orientation, double radiusW, double radiusH, double initialAngle, double speed) {
		this.orientation = orientation;
		this.radiusW = radiusW;
		this.radiusH = radiusH;
		this.initialAngle = initialAngle;
		this.speed = speed;
	}

	public SetEliticMove(String tags) {
		sourceStringTags = tags;
		String[] params = validateStringTags(this, tags);
		if (params.length > 5)
			throw new RuntimeException(tags + " - Too much parameters");
		if (params.length < 4)
			throw new RuntimeException(tags + " - Too few parameters");
		int n = 0;
		try {
			orientation = DirectionOrientation.valueOf(params[n++]);
			radiusW = Double.parseDouble(params[n++]);
			radiusH = Double.parseDouble(params[n++]);
			initialAngle = params.length == 4 ? 0 : Double.parseDouble(params[n++]);
			speed = Double.parseDouble(params[n++]);
		}
		catch (Exception e) {
			throw new RuntimeException(params[--n] + " - Invalid parameter");
		}
	}

	@Override
	public SetEliticMove getNewInstanceOfThis() {
		return new SetEliticMove(orientation, radiusW, radiusH, initialAngle, speed);
	}

	@Override
	public void process(Sprite sprite) {
		sprite.setEliticMove(new EliticMove(orientation, radiusW, radiusH, initialAngle, speed));
	}

}
