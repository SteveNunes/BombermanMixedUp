package frameset_tags;

import frameset.Sprite;

public class IncSprAlign extends FrameTag {
	
	public IncSprAlign() {}

	@Override
	public String toString()
		{ return "{" + getClassName(this) + "}"; }

	public IncSprAlign(String tags)
		{ validateStringTags(this, tags, 0); }

	@Override
	public IncSprAlign getNewInstanceOfThis()
		{ return new IncSprAlign(); }
	
	@Override
	public void process(Sprite sprite)
		{ sprite.setAlignment(sprite.getAlignment().getNext()); }

}
