package frameset_tags;

public class IncSprAlign extends FrameTag {
	
	public IncSprAlign() {}

	@Override
	public String toString()
		{ return "{" + FrameTag.getClassName(this) + "}"; }

	public IncSprAlign(String tags)
		{ FrameTag.validateStringTags(this, tags, 0); }

}
