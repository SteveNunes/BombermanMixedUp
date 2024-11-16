package frameset_tags;

import java.util.ArrayList;
import java.util.List;

import enums.ItemType;
import frameset.Sprite;
import maps.Item;

public class SetFallingItem extends FrameTag {

	public List<TileCoord2> targetCoords;
	public ItemType itemType;
	
	public SetFallingItem(ItemType itemType, List<TileCoord2> targetCoords) {
		this.itemType = itemType;
		this.targetCoords = targetCoords == null ? null : new ArrayList<>(targetCoords);
	}

	@Override
	public String toString() {
		return "{" + getClassName(this) + ";" + itemType.name() + ";" + tileCoord2ListToString(targetCoords) + "}";
	}

	public SetFallingItem(String tags) {
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
	public SetFallingItem getNewInstanceOfThis() {
		return new SetFallingItem(itemType, targetCoords);
	}

	@Override
	public void process(Sprite sprite) {
		if (targetCoords == null)
			Item.dropItemFromSky(sprite.getTileCoordFromCenter(), itemType);
		else
			processTile(sprite.getTileCoord(), targetCoords, coord ->
				Item.dropItemFromSky(coord, itemType));
	}

}
