package frameset_tags;

import entities.Entity;
import entities.FrameSet;
import entities.Sprite;

public class SetEntityX extends FrameTag {
	
	private int value;
	
	public SetEntityX(int value)
		{ this.value = value; }

	public int getValue()
		{ return value; }
	
	@Override
	public String toString()
		{ return "{" + FrameTag.getClassName(this) + ";" + value + "}"; }

	public SetEntityX(String tags) {
		String[] params = FrameTag.validateStringTags(this, tags, 1);
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
		FrameSet frameSet = sprite.getMainFrameSet();
		Entity entity = frameSet.getEntity();
		entity.incX(getValue());
	}

	@Override
	public void reset() {
	}

}
