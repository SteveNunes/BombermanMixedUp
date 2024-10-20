package frameset_tags;

import frameset.Sprite;

public class DisableEntity extends FrameTag {
	
	public DisableEntity() {}

	@Override
	public String toString()
		{ return "{" + FrameTag.getClassName(this) + "}"; }

	public DisableEntity(String tags)
		{ FrameTag.validateStringTags(this, tags, 0); }
	
	@Override
	public DisableEntity getNewInstanceOfThis()
		{ return new DisableEntity(); }

	@Override
	public void process(Sprite sprite)
		{ sprite.getSourceEntity().setDisabled(); }

}
