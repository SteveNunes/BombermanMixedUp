package frameset_tags;

public class IncSprFlip extends FrameTag {
	
	public IncSprFlip() {}

	@Override
	public String toString()
		{ return "{" + FrameTag.getClassName(this) + "}"; }
	
	public IncSprFlip(String tags)
		{ FrameTag.validateStringTags(this, tags, 0); }

}
