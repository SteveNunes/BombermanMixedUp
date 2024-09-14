package frameset_tags;

import entities.Entity;
import frameset.FrameSet;
import frameset.Sprite;
import tools.GameMisc;

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
			{ GameMisc.throwRuntimeException(params[0] + " - Invalid parameter"); }
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

}
