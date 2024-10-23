package frameset_tags;

import entities.Bomb;
import enums.Direction;
import frameset.Sprite;

public class PushBombFromTile extends PushEntity {
	

	public PushBombFromTile(Double startStrenght, Double decStrenght, Direction direction, TileCoord2 targetTile, String triggerSound, String soundWhenHits)
		{ super(startStrenght, decStrenght, direction, targetTile, triggerSound, soundWhenHits); }

	public PushBombFromTile(String tags)
		{ super(tags); }
	
	@Override
	public PushBombFromTile getNewInstanceOfThis()
		{ return new PushBombFromTile(startStrenght, decStrenght, direction, targetTile, triggerSound, soundWhenHits); }

	@Override
	public void process(Sprite sprite) {
	  Bomb bomb = Bomb.getBombAt(sprite.getTileCoord());
		if (bomb != null && bomb.getPushEntity() == null) 
			set(sprite, bomb);
	}

}
