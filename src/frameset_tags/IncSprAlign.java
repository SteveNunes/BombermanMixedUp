package frameset_tags;

import entities.Sprite;

public class IncSprAlign extends FrameTag {
	
	public IncSprAlign() {}

	@Override
	public String toString()
		{ return "{" + FrameTag.getClassName(this) + "}"; }

	public IncSprAlign(String tags)
		{ FrameTag.validateStringTags(this, tags, 0); }

	@Override
	public IncSprAlign getNewInstanceOfThis()
		{ return new IncSprAlign(); }
	
	@Override
	public void process(Sprite sprite)
		{ sprite.setAlignment(sprite.getAlignment().getNext()); }

	@Override
	public void reset() {
	}

}
