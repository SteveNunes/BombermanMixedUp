package frameset_tags;

import java.util.Set;

import entities.BomberMan;
import entities.Entity;
import frameset.Sprite;

public class PushPower extends FrameTag {
	
	String tags;
	
	public PushPower() {}

	public PushPower(String tags) {
		validateStringTags(this, tags);
		this.tags = tags;
	}

	@Override
	public PushPower getNewInstanceOfThis() {
		return new PushPower(tags);
	}

	@Override
	public void process(Sprite sprite) {
		if (Entity.haveAnyEntityAtCoord(sprite.getTileCoordFromCenter(), sprite.getSourceEntity())) {
			Set<Entity> entities = Entity.getEntityListFromCoord(sprite.getTileCoordFromCenter());
			if (entities != null)
				for (Entity entity : entities)
					if (entity instanceof BomberMan && ((BomberMan)entity).haveFrameSet("BeingPushed")) {
						BomberMan bomber = ((BomberMan)entity);
						bomber.unsetAllMovings();
						bomber.forceDirection(sprite.getSourceEntity().getDirection());
						bomber.setFrameSet("BeingPushed");
					}
		}
	}

}
