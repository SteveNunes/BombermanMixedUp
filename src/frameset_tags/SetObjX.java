package frameset_tags;

import frameset.FrameSet;
import frameset.Sprite;

public class SetObjX extends FrameTag {
	
	public int value;
	
	public SetObjX(int value)
		{ this.value = value; }

	@Override
	public String toString()
		{ return "{" + FrameTag.getClassName(this) + ";" + value + "}"; }

	public SetObjX(String tags) {
		String[] params = FrameTag.validateStringTags(this, tags, 1);
		try
			{ value = Integer.parseInt(params[0]); }
		catch (Exception e)
			{ throw new RuntimeException(params[0] + " - Invalid parameter"); }
	}

	@Override
	public SetObjX getNewInstanceOfThis()
		{ return new SetObjX(value); }
	
	@Override
	public void process(Sprite sprite) {
		FrameSet frameSet = sprite.getSourceFrameSet();
		frameSet.setX(value);
	}

}



