package frameset_tags;

import entities.Entity;
import frameset.FrameSet;
import frameset.Sprite;

public class SetEntityNoMove extends FrameTag {
	
	public boolean value;
	
	public SetEntityNoMove(boolean value)
		{ this.value = value; }

	@Override
	public String toString()
		{ return "{" + FrameTag.getClassName(this) + ";" + value + "}"; }

	public SetEntityNoMove(String tags) {
		String[] params = FrameTag.validateStringTags(this, tags, 1);
		try
			{ value = Boolean.parseBoolean(params[0]); }
		catch (Exception e)
			{ throw new RuntimeException(params[0] + " - Invalid parameter"); }
	}

	@Override
	public SetEntityNoMove getNewInstanceOfThis()
		{ return new SetEntityNoMove(value); }
	
	@Override
	public void process(Sprite sprite) {
		FrameSet frameSet = sprite.getMainFrameSet();
		Entity entity = frameSet.getEntity();
		entity.setNoMove(value);
	}

}



