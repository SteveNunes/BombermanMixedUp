package frameset_tags;

import entities.Entity;
import frameset.Sprite;
import maps.Brick;
import objmoveutils.TileCoord;

public class KickBrickInFront extends FrameTag {

	public String kickSound;
	public String slamSound;

	public KickBrickInFront(boolean b, String kickSound, String slamSound) {
		this.kickSound = kickSound;
		this.slamSound = slamSound;
	}

	public KickBrickInFront(String tags) {
		sourceStringTags = tags;
		String[] params = validateStringTags(this, tags);
		if (params.length > 2)
			throw new RuntimeException(tags + " - Too much parameters");
		kickSound = params.length == 1 ? params[0] : null;
		slamSound = params.length == 2 ? params[1] : null;
	}

	@Override
	public KickBrickInFront getNewInstanceOfThis() {
		return new KickBrickInFront(false, kickSound, slamSound);
	}

	@Override
	public void process(Sprite sprite) {
		Entity entity = sprite.getSourceEntity();
		TileCoord coord = entity.getTileCoordFromCenter().getNewInstance().incCoordsByDirection(entity.getDirection());
		if (Brick.haveBrickAt(coord))
			Brick.getBrickAt(coord).kick(entity.getDirection(), 4, kickSound, slamSound);
	}

}
