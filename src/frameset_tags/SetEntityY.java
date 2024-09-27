package frameset_tags;

import entities.Entity;
import frameset.FrameSet;
import frameset.Sprite;

public class SetEntityY extends FrameTag {
	
	public int value;
	
	public SetEntityY(int value)
		{ this.value = value; }

	@Override
	public String toString()
		{ return "{" + FrameTag.getClassName(this) + ";" + value + "}"; }

	public SetEntityY(String tags) {
		String[] params = FrameTag.validateStringTags(this, tags, 1);
		try
			{ value = Integer.parseInt(params[0]); }
		catch (Exception e)
			{ throw new RuntimeException(params[0] + " - Invalid parameter"); }
	}

	@Override
	public SetEntityY getNewInstanceOfThis()
		{ return new SetEntityY(value); }
	
	@Override
	public void process(Sprite sprite) {
		FrameSet frameSet = sprite.getMainFrameSet();
		Entity entity = frameSet.getEntity();
		entity.incY(value);
	}

}



