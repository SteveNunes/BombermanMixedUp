package frameset_tags;

import entities.Entity;
import entities.FrameSet;
import entities.Sprite;

public class SetEntityY extends FrameTag {
	
	private int value;
	
	public SetEntityY(int value)
		{ this.value = value; }

	public int getValue()
		{ return value; }

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
		entity.incY(getValue());
	}

	@Override
	public void reset() {
	}

}
