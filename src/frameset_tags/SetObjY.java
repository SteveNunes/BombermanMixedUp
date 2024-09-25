package frameset_tags;

import frameset.FrameSet;
import frameset.Sprite;
import tools.Tools;

public class SetObjY extends FrameTag {
	
	private int value;
	
	public SetObjY(int value)
		{ this.value = value; }

	public int getValue()
		{ return value; }

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
		FrameSet frameSet = sprite.getMainFrameSet();
		frameSet.setY(getValue());
	}

}
