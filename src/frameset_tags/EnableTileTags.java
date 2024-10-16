package frameset_tags;

import java.util.ArrayList;
import java.util.List;

import frameset.Sprite;
import maps.MapSet;
import maps.Tile;

public class EnableTileTags extends FrameTag {
	
	public List<TileCoord2> targetCoords;
	int targetLayer;
	
	public EnableTileTags(int layer, List<TileCoord2> targetCoords) {
		this.targetCoords = new ArrayList<>(targetCoords);
		targetLayer = layer;
	}

	@Override
	public String toString()
		{ return "{" + FrameTag.getClassName(this) + ";" + targetLayer + ";" + FrameTag.tileCoord2ListToString(targetCoords) + "}"; }

	public EnableTileTags(String tags) {
		String[] params = FrameTag.validateStringTags(this, tags);
		if (params.length < 2)
			throw new RuntimeException(tags + " - Too few parameters");
		if (params.length > 5)
			throw new RuntimeException(tags + " - Too few parameters");
		int n = 0;
		try {
			targetLayer = Integer.parseInt(params[n = 0]);
			targetCoords = FrameTag.stringToTileCoord2List((n = 1) >= params.length ? null : params[n]);
		}
		catch (Exception e)
			{ e.printStackTrace(); throw new RuntimeException(params[n] + " - Invalid parameter"); }
	}

	@Override
	public EnableTileTags getNewInstanceOfThis()
		{ return new EnableTileTags(targetLayer, targetCoords); }

	@Override
	public void process(Sprite sprite) {
		FrameTag.processTile(sprite, targetCoords, coord -> {
			Tile tile = MapSet.getFirstBottomTileFromCoord(coord);
			if (tile != null)
				tile.enableTags();
		});
	}

}
