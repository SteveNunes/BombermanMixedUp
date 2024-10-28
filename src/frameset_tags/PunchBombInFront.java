package frameset_tags;

import entities.Bomb;
import entities.Entity;
import frameset.Sprite;
import objmoveutils.TileCoord;

public class PunchBombInFront extends FrameTag {
	
	public String punchSound;
	
	public PunchBombInFront(boolean b, String punchSound)
		{ this.punchSound = punchSound; }

	@Override
	public String toString()
		{ return "{" + getClassName(this) + "}"; }

	public PunchBombInFront(String tags) {
		String[] params = validateStringTags(this, tags);
		if (params.length > 1)
			throw new RuntimeException(tags + " - Too much parameters");
		punchSound = params.length == 1 ? params[0] : null;
	}
	
	@Override
	public PunchBombInFront getNewInstanceOfThis()
		{ return new PunchBombInFront(false, punchSound); }

	@Override
	public void process(Sprite sprite) {
		Entity entity = sprite.getSourceEntity();
		TileCoord coord = entity.getTileCoordFromCenter().getNewInstance().incCoordsByDirection(entity.getDirection());
		if (Bomb.haveBombAt(entity, coord))
			Bomb.getBombAt(coord).punch(entity.getDirection(), punchSound);
	}

}
