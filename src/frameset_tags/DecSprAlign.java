package frameset_tags;

import frameset.Sprite;

public class DecSprAlign extends FrameTag {
	
	public DecSprAlign() {}
	
	@Override
	public String toString()
		{ return "{" + getClassName(this) + "}"; }
	
	public DecSprAlign(String tags)
		{ validateStringTags(this, tags, 0); }

	@Override
	public DecSprAlign getNewInstanceOfThis()
		{ return new DecSprAlign(); }

	@Override
	public void process(Sprite sprite)
		{ sprite.setAlignment(sprite.getAlignment().getPreview()); }

}