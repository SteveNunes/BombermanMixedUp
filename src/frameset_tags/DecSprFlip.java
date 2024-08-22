package frameset_tags;

public class DecSprFlip extends FrameTag {
	
	public DecSprFlip() {}

	@Override
	public String toString()
		{ return "{" + FrameTag.getClassName(this) + "}"; }

	public DecSprFlip(String tags)
		{ FrameTag.validateStringTags(this, tags, 0); }
	
	@Override
	public DecSprFlip getNewInstanceOfThis()
		{ return new DecSprFlip(); }

}
