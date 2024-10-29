package frameset_tags;

import frameset.Sprite;

public class UnSetMultiSprIndexByDirection extends FrameTag {
	
	public UnSetMultiSprIndexByDirection() {}

	@Override
	public String toString()
		{ return "{" + getClassName(this) + "}"; }

	public UnSetMultiSprIndexByDirection(String tags)
		{ validateStringTags(this, tags, 0); }
	
	@Override
	public UnSetMultiSprIndexByDirection getNewInstanceOfThis()
		{ return new UnSetMultiSprIndexByDirection(); }

	@Override
	public void process(Sprite sprite)
		{ sprite.unsetMultiFrameIndexByDirection(); }

}
