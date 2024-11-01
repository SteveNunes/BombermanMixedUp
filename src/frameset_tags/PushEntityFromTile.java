package frameset_tags;

import entities.Entity;
import enums.Direction;
import frameset.Sprite;

public class PushEntityFromTile extends PushEntity {

	public PushEntityFromTile(Double startStrenght, Double decStrenght, Direction direction, TileCoord2 targetTile, String triggerSound, String soundWhenHits) {
		super(startStrenght, decStrenght, direction, targetTile, triggerSound, soundWhenHits);
	}

	public PushEntityFromTile(String tags) {
		super(tags);
	}

	@Override
	public PushEntityFromTile getNewInstanceOfThis() {
		return new PushEntityFromTile(startStrenght, decStrenght, direction, targetTile, triggerSound, soundWhenHits);
	}

	@Override
	public void process(Sprite sprite) {
		Entity entity = Entity.getFirstEntityFromCoord(sprite.getTileCoord());
		if (entity != null && entity.getPushEntity() == null)
			set(sprite, entity);
	}

}
