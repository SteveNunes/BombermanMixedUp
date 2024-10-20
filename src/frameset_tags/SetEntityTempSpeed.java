package frameset_tags;

import frameset.Sprite;

public class SetEntityTempSpeed extends FrameTag {
	
	public double value;
	
	public SetEntityTempSpeed(double value)
		{ this.value = value; }

	@Override
	public String toString()
		{ return "{" + FrameTag.getClassName(this) + ";" + value + "}"; }

	public SetEntityTempSpeed(String tags) {
		String[] params = FrameTag.validateStringTags(this, tags, 1);
		try
			{ value = Integer.parseInt(params[0]); }
		catch (Exception e)
			{ throw new RuntimeException(params[0] + " - Invalid parameter"); }
	}

	@Override
	public SetEntityTempSpeed getNewInstanceOfThis()
		{ return new SetEntityTempSpeed(value); }
	
	@Override
	public void process(Sprite sprite)
		{ sprite.getSourceEntity().setTempSpeed(value); }

}



