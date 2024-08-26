package frameset_tags;

import entities.Sprite;

public class IncSprFlip extends FrameTag {
	
	public IncSprFlip() {}

	@Override
	public String toString()
		{ return "{" + FrameTag.getClassName(this) + "}"; }
	
	public IncSprFlip(String tags)
		{ FrameTag.validateStringTags(this, tags, 0); }

	@Override
	public IncSprFlip getNewInstanceOfThis()
		{ return new IncSprFlip(); }
	
	@Override
	public void process(Sprite sprite)
		{ sprite.setFlip(sprite.getFlip().getNext()); }

	@Override
	public void reset() {
	}

}
