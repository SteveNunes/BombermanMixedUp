package frameset_tags;

import frameset.Sprite;

public class SetOutputSprHeight extends FrameTag {
	
	public int value;
	
	public SetOutputSprHeight(int value)
		{ this.value = value; }

	@Override
	public String toString()
		{ return "{" + getClassName(this) + ";" + value + "}"; }

	public SetOutputSprHeight(String tags) {
		String[] params = validateStringTags(this, tags, 1);
		try
			{ value = Integer.parseInt(params[0]); }
		catch (Exception e)
			{ throw new RuntimeException(params[0] + " - Invalid parameter"); }
	}

	@Override
	public SetOutputSprHeight getNewInstanceOfThis()
		{ return new SetOutputSprHeight(value); }
	
	@Override
	public void process(Sprite sprite)
		{ sprite.setOutputHeight(value); }

}



