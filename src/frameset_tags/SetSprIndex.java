package frameset_tags;

import frameset.Sprite;

public class SetSprIndex extends FrameTag {
	
	public Integer value;
	
	public SetSprIndex(Integer value)
		{ this.value = value; }

	@Override
	public String toString()
		{ return "{" + getClassName(this) + ";" + (value == null ? "-" : value) + "}"; }

	public SetSprIndex(String tags) {
		String[] params = validateStringTags(this, tags, 1);
		try
			{ value = params[0].equals("-") ? null : Integer.parseInt(params[0]); }
		catch (Exception e)
			{ throw new RuntimeException(params[0] + " - Invalid parameter"); }
	}

	@Override
	public SetSprIndex getNewInstanceOfThis()
		{ return new SetSprIndex(value); }
	
	@Override
	public void process(Sprite sprite) {
		if (value == null)
			sprite.setVisible(false);
		else {
			sprite.setVisible(true);
			sprite.setSpriteIndex(value);
		}
	}

}



