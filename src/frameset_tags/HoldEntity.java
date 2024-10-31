package frameset_tags;

import entities.Bomb;
import entities.Entity;
import frameset.Sprite;
import maps.Brick;

public class HoldEntity extends FrameTag {
	
	public HoldEntity() {}

	@Override
	public String toString()
		{ return "{" + getClassName(this) + "}"; }

	public HoldEntity(String tags)
		{ validateStringTags(this, tags, 0); }
	
	@Override
	public HoldEntity getNewInstanceOfThis()
		{ return new HoldEntity(); }

	@Override
	public void process(Sprite sprite) {
		Entity entity = sprite.getSourceEntity();
		for (Entity en : Entity.getEntityListFromCoord(entity.getTileCoordFromCenter()))
			if (en != entity) {
				entity.setHoldingEntity(en);
				return;
			}
		if (Bomb.haveBombAt(entity.getTileCoordFromCenter())) {
			entity.setHoldingEntity(Bomb.getBombAt(entity.getTileCoordFromCenter()));
			return;
		}
		if (Brick.haveBrickAt(entity.getTileCoordFromCenter()))
			entity.setHoldingEntity(Brick.getBrickAt(entity.getTileCoordFromCenter()));
	}

}
