package frameset_tags;

import entities.Entity;
import frameset.Sprite;

public class ReleaseEntity extends FrameTag {

	public ReleaseEntity(String tags) {
		sourceStringTags = tags;
		validateStringTags(this, tags, 0);
	}

	@Override
	public ReleaseEntity getNewInstanceOfThis() {
		return new ReleaseEntity(sourceStringTags);
	}

	@Override
	public void process(Sprite sprite) {
		Entity entity = sprite.getSourceEntity();
		if (entity.isHoldingEntity())
			entity.unsetHoldingEntity();
	}

}
