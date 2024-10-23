package frameset_tags;

import frameset.Sprite;

public class SetEntityX extends FrameTag {
	
	public int value;
	
	public SetEntityX(int value)
		{ this.value = value; }

	@Override
	public String toString()
		{ return "{" + getClassName(this) + ";" + value + "}"; }

	public SetEntityX(String tags) {
		String[] params = validateStringTags(this, tags, 1);
		try
			{ value = Integer.parseInt(params[0]); }
		catch (Exception e)
			{ throw new RuntimeException(params[0] + " - Invalid parameter"); }
	}

	@Override
	public SetEntityX getNewInstanceOfThis()
		{ return new SetEntityX(value); }
	
	@Override
	public void process(Sprite sprite) {
		sprite.getSourceEntity().incX(value);
	}

}



