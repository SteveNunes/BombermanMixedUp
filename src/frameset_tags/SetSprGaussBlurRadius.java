package frameset_tags;

import frameset.Sprite;

public class SetSprGaussBlurRadius extends FrameTag {
	
	public int value;
	
	public SetSprGaussBlurRadius(int value)
		{ this.value = value; }

	@Override
	public String toString()
		{ return "{" + FrameTag.getClassName(this) + ";" + value + "}"; }

	public SetSprGaussBlurRadius(String tags) {
		String[] params = FrameTag.validateStringTags(this, tags, 1);
		try
			{ value = Integer.parseInt(params[0]); }
		catch (Exception e)
			{ throw new RuntimeException(params[0] + " - Invalid parameter"); }
	}

	@Override
	public SetSprGaussBlurRadius getNewInstanceOfThis()
		{ return new SetSprGaussBlurRadius(value); }
	
	@Override
	public void process(Sprite sprite)
		{ sprite.getEffects().getGaussianBlur().setRadius(value); }

}



