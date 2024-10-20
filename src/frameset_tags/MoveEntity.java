package frameset_tags;

import frameset.Sprite;

public class MoveEntity extends FrameTag {
	
	public double speed;
	
	public MoveEntity(double speed)
		{ this.speed = speed; }

	@Override
	public String toString()
		{ return "{" + FrameTag.getClassName(this) + ";" + speed + "}"; }

	public MoveEntity(String tags) {
		String[] params = FrameTag.validateStringTags(this, tags);
		if (params.length > 1)
			throw new RuntimeException(tags + " - Too much parameters");
		if (params.length == 1) {
			int n = 0;
			try {
				speed = Double.parseDouble(params[n]);
			}
			catch (Exception e)
				{ throw new RuntimeException(params[n] + " - Invalid parameter"); }
		}
		else
			speed = -1;
	}

	@Override
	public MoveEntity getNewInstanceOfThis()
		{ return new MoveEntity(speed); }

	@Override
	public void process(Sprite sprite) {
		if (speed >= 0)
			sprite.getSourceEntity().moveEntity(speed);
		else
			sprite.getSourceEntity().moveEntity();
	}

}
