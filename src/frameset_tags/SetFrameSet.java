package frameset_tags;

import frameset.Sprite;

public class SetFrameSet extends FrameTag {
	
	public String value;
	
	public SetFrameSet()
		{ value = ""; }

	@Override
	public String toString()
		{ return "{" + FrameTag.getClassName(this) + ";" + value + "}"; }

	public SetFrameSet(String tags) {
		String[] params = FrameTag.validateStringTags(this, tags, 1);
		value = params[0];
	}

	@Override
	public SetFrameSet getNewInstanceOfThis() {
		SetFrameSet s = new SetFrameSet();
		s.value = value;
		return s;
	}
	
	@Override
	public void process(Sprite sprite) {
		sprite.getSourceEntity().setFrameSet(value);
	}

}



