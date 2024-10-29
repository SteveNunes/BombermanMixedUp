package frameset_tags;

import entities.Entity;
import frameset.Sprite;

public class ReleaseEntity extends FrameTag {
	
	public ReleaseEntity() {}

	@Override
	public String toString()
		{ return "{" + getClassName(this) + "}"; }

	public ReleaseEntity(String tags)
		{ validateStringTags(this, tags, 0); }
	
	@Override
	public ReleaseEntity getNewInstanceOfThis()
		{ return new ReleaseEntity(); }

	@Override
	public void process(Sprite sprite) {
		Entity entity = sprite.getSourceEntity();
		if (entity.getHoldingEntity() != null)
			entity.unsetHoldingEntity();
	}

}
