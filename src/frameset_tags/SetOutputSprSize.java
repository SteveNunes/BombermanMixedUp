package frameset_tags;

import frameset.Sprite;

public class SetOutputSprSize extends FrameTag {
	
	public int width;
	public int height;
	
	public SetOutputSprSize(int width, int height) {
		this.width = width;
		this.height = height;
	}

	@Override
	public String toString()
		{ return "{" + getClassName(this) + ";" + width + ";" + height + "}"; }

	public SetOutputSprSize(String tags) {
		String[] params = validateStringTags(this, tags, 2);
		int n = 0;
		try {
			width = Integer.parseInt(params[n++]);
			height = Integer.parseInt(params[n++]);
		}
		catch (Exception e)
			{ throw new RuntimeException(params[--n] + " - Invalid parameter"); }
	}

	@Override
	public SetOutputSprSize getNewInstanceOfThis()
		{ return new SetOutputSprSize(width, height); }
	
	@Override
	public void process(Sprite sprite) {
		sprite.setOutputWidth(width);
		sprite.setOutputHeight(height);
	}

}






