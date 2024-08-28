package frameset_tags;

import entities.Sprite;

public class DecSprAlign extends FrameTag {
	
	public DecSprAlign() {}
	
	@Override
	public String toString()
		{ return "{" + FrameTag.getClassName(this) + "}"; }
	
	public DecSprAlign(String tags)
		{ FrameTag.validateStringTags(this, tags, 0); }

	@Override
	public DecSprAlign getNewInstanceOfThis()
		{ return new DecSprAlign(); }

	@Override
	public void process(Sprite sprite)
		{ sprite.setAlignment(sprite.getAlignment().getPreview()); }

}