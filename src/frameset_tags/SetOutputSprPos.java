package frameset_tags;

import frameset.Sprite;
import tools.Tools;

public class SetOutputSprPos extends FrameTag {
	
	private double x;
	private double y;
	
	public SetOutputSprPos(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public double getX()
		{ return x; }

	public double getY()
		{ return y; }	

	@Override
	public String toString()
		{ return "{" + FrameTag.getClassName(this) + ";" + x + ";" + y + "}"; }
	
	public SetOutputSprPos(String tags) {
		String[] params = FrameTag.validateStringTags(this, tags, 2);
		int n = 0;
		try {
			x = Double.parseDouble(params[n++]);
			y = Double.parseDouble(params[n++]);
		}
		catch (Exception e)
			{ throw new RuntimeException(params[--n] + " - Invalid parameter"); }
	}

	@Override
	public SetOutputSprPos getNewInstanceOfThis()
		{ return new SetOutputSprPos(x, y); }

	@Override
	public void process(Sprite sprite) {
		sprite.setX(getX());
		sprite.setY(getY());
	}

}