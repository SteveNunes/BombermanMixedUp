package frameset_tags;

import entities.Entity;
import frameset.Sprite;
import maps.Brick;
import objmoveutils.TileCoord;

public class PunchBrickInFront extends FrameTag {

	public String punchSound;

	public PunchBrickInFront(boolean b, String punchSound) {
		this.punchSound = punchSound;
	}

	@Override
	public String toString() {
		return "{" + getClassName(this) + "}";
	}

	public PunchBrickInFront(String tags) {
		String[] params = validateStringTags(this, tags);
		if (params.length > 1)
			throw new RuntimeException(tags + " - Too much parameters");
		punchSound = params.length == 1 ? params[0] : null;
	}

	@Override
	public PunchBrickInFront getNewInstanceOfThis() {
		return new PunchBrickInFront(false, punchSound);
	}

	@Override
	public void process(Sprite sprite) {
		Entity entity = sprite.getSourceEntity();
		TileCoord coord = entity.getTileCoordFromCenter().getNewInstance().incCoordsByDirection(entity.getDirection());
		if (Brick.haveBrickAt(coord))
			Brick.getBrickAt(coord).punch(entity.getDirection(), punchSound);
	}

}
