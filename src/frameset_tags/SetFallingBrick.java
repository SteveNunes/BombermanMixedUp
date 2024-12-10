package frameset_tags;

import java.util.ArrayList;
import java.util.List;

import enums.ItemType;
import frameset.Sprite;
import maps.Brick;
import maps.Item;

public class SetFallingBrick extends FrameTag {

	public List<TileCoord2> targetCoords;
	public ItemType itemType;
	
	public SetFallingBrick(ItemType itemType, List<TileCoord2> targetCoords) {
		this.itemType = itemType;
		this.targetCoords = targetCoords == null ? null : new ArrayList<>(targetCoords);
	}

	@Override
	public String toString() {
		return "{" + getClassName(this) + ";" + itemType.name() + ";" + tileCoord2ListToString(targetCoords) + "}";
	}

	public SetFallingBrick(String tags) {
		String[] params = validateStringTags(this, tags);
		if (params.length > 3)
			throw new RuntimeException(tags + " - Too much parameters");
		if (params.length < 2)
			throw new RuntimeException(tags + " - Too few parameters");
		int n = 0;
		try {
			itemType = ItemType.valueOf(params[n]);
			targetCoords = params.length == 1 ? null : stringToTileCoord2List(params[n = 1]);
		}
		catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(params[n] + " - Invalid parameter");
		}
	}

	@Override
	public SetFallingBrick getNewInstanceOfThis() {
		return new SetFallingBrick(itemType, targetCoords);
	}

	@Override
	public void process(Sprite sprite) {
		Item item = new Item(sprite.getSourceEntity().getTileCoordFromCenter().getNewInstance(), itemType);
		if (targetCoords == null)
			Brick.dropBrickFromSky(sprite.getTileCoordFromCenter(), item);
		else
			processTile(sprite.getTileCoord(), targetCoords, coord ->
				Brick.dropBrickFromSky(coord, item));
	}

}
