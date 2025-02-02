package frameset_tags;

import entities.Bomb;
import entities.Entity;
import frameset.Sprite;
import maps.Brick;

public class HoldEntity extends FrameTag {

	public HoldEntity(String tags) {
		sourceStringTags = tags;
		validateStringTags(this, tags, 0);
	}

	@Override
	public HoldEntity getNewInstanceOfThis() {
		return new HoldEntity(sourceStringTags);
	}

	@Override
	public void process(Sprite sprite) {
		Entity entity = sprite.getSourceEntity();
		if (Entity.haveAnyEntityAtCoord(entity.getTileCoordFromCenter(), sprite.getSourceEntity()))
			for (Entity en : Entity.getEntityListFromCoord(entity.getTileCoordFromCenter())) {
				if (en != entity && !en.isBlockedMovement()) {
					entity.setHoldingEntity(en);
					return;
				}
			}
		if (Bomb.haveBombAt(entity.getTileCoordFromCenter())) {
			entity.setHoldingEntity(Bomb.getBombAt(entity.getTileCoordFromCenter()));
			return;
		}
		if (Brick.haveBrickAt(entity.getTileCoordFromCenter()))
			entity.setHoldingEntity(Brick.getBrickAt(entity.getTileCoordFromCenter()));
	}

}
