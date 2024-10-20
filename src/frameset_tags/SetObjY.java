package frameset_tags;

import frameset.FrameSet;
import frameset.Sprite;

public class SetObjY extends FrameTag {
	
	public int value;
	
	public SetObjY(int value)
		{ this.value = value; }

	@Override
	public String toString()
		{ return "{" + FrameTag.getClassName(this) + ";" + value + "}"; }

	public SetObjY(String tags) {
		String[] params = FrameTag.validateStringTags(this, tags, 1);
		try
			{ value = Integer.parseInt(params[0]); }
		catch (Exception e)
			{ throw new RuntimeException(params[0] + " - Invalid parameter"); }
	}

	@Override
	public SetObjY getNewInstanceOfThis()
		{ return new SetObjY(value); }
	
	@Override
	public void process(Sprite sprite) {
		FrameSet frameSet = sprite.getSourceFrameSet();
		frameSet.setY(value);
	}

}



