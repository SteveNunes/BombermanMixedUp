package frameset_tags;

import entities.Entity;
import frameset.FrameSet;
import frameset.Sprite;

public class SetFrameSet extends FrameTag {
	
	private String value;
	
	public SetFrameSet()
		{ value = ""; }

	public String getValue()
		{ return value; }

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
		FrameSet frameSet = sprite.getMainFrameSet();
		Entity entity = frameSet.getEntity();
		entity.setFrameSet(value);
	}

}