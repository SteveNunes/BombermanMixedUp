package frameset_tags;

import entities.FrameSet;
import entities.Sprite;

public class SetObjX extends FrameTag {
	
	private int value;
	
	public SetObjX(int value)
		{ this.value = value; }

	public int getValue()
		{ return value; }
	
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
		FrameSet frameSet = sprite.getMainFrameSet();
		frameSet.setX(getValue());
	}

}
