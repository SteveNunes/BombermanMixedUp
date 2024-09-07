package frameset_tags;

import entities.Sprite;
import enums.DirectionOrientation;
import objmoveutils.EliticMove;
import tools.GameMisc;

public class SetSprEliticMove extends FrameTag {
	
	private DirectionOrientation orientation;
	private double radiusWidth;
	private double radiusHeight;
	private double speed;
	
	public SetSprEliticMove(DirectionOrientation orientation, double radiusWidth, double radiusHeight, double speed) {
		this.orientation = orientation;
		this.radiusWidth = radiusWidth;
		this.radiusHeight = radiusHeight;
		this.speed = speed;
	}

	public DirectionOrientation getOrientation()
		{ return orientation; }

	public double getRadiusWidth()
		{ return radiusWidth; }

	public double getRadiusHeight()
		{ return radiusHeight; }

	public double getSpeed()
		{ return speed; }
	
	@Override
	public String toString()
		{ return "{" + FrameTag.getClassName(this) + ";" + orientation.name() + ";" + radiusWidth + ";" + radiusHeight + ";" + speed + "}"; }

	public SetSprEliticMove(String tags) {
		String[] params = FrameTag.validateStringTags(this, tags, 4);
		int n = 0;
		try {
			orientation = DirectionOrientation.valueOf(params[n++]);
			radiusWidth = Double.parseDouble(params[n++]);
			radiusHeight = Double.parseDouble(params[n++]);
			speed = Double.parseDouble(params[n++]);
		}
		catch (Exception e)
			{ GameMisc.throwRuntimeException(params[--n] + " - Invalid parameter"); }
	}

	@Override
	public SetSprEliticMove getNewInstanceOfThis()
		{ return new SetSprEliticMove(orientation, radiusWidth, radiusHeight, speed); }

	@Override
	public void process(Sprite sprite) {
		if (sprite.getEliticMove() == null)
			sprite.setEliticMove(new EliticMove(orientation, radiusWidth, radiusHeight, speed));
	}

}
