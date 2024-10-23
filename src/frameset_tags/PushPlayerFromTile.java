package frameset_tags;

import java.util.Set;

import entities.BomberMan;
import entities.Entity;
import enums.Direction;
import frameset.Sprite;

public class PushPlayerFromTile extends PushEntity {
	
	public PushPlayerFromTile(Double startStrenght, Double decStrenght, Direction direction, TileCoord2 targetTile, String triggerSound, String soundWhenHits)
		{ super(startStrenght, decStrenght, direction, targetTile, triggerSound, soundWhenHits); }
	
	public PushPlayerFromTile(String tags)
		{ super(tags); }

	@Override
	public PushPlayerFromTile getNewInstanceOfThis()
		{ return new PushPlayerFromTile(startStrenght, decStrenght, direction, targetTile, triggerSound, soundWhenHits); }

	@Override
	public void process(Sprite sprite) {
		Set<Entity> list = Entity.getEntityListFromCoord(sprite.getTileCoord());
		for (Entity entity : list)
			if (entity != null && entity instanceof BomberMan && entity.getPushEntity() == null)
				set(sprite, entity);
	}

}
