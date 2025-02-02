package frameset_tags;

import java.util.ArrayList;
import java.util.List;

import frameset.Sprite;
import maps.MapSet;

public class ClearTileTags extends FrameTag {

	public List<TileCoord2> targetCoords;
	int targetLayer;

	public ClearTileTags(int layer, List<TileCoord2> targetCoords) {
		this.targetCoords = new ArrayList<>(targetCoords);
		targetLayer = layer;
	}

	public ClearTileTags(String tags) {
		sourceStringTags = tags;
		String[] params = validateStringTags(this, tags, 2);
		int n = 0;
		try {
			targetLayer = params[n = 0].equals("-") ? 26 : Integer.parseInt(params[n]);
			targetCoords = stringToTileCoord2List((n = 1) >= params.length ? null : params[n]);
		}
		catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(params[n] + " - Invalid parameter");
		}
	}

	@Override
	public ClearTileTags getNewInstanceOfThis() {
		return new ClearTileTags(targetLayer, targetCoords);
	}

	@Override
	public void process(Sprite sprite) {
		processTile(sprite.getTileCoord(), targetCoords, coord -> {
			if (MapSet.getLayer(targetLayer).tileHaveTags(coord))
				MapSet.getLayer(targetLayer).clearTileTags(coord);
		});
	}

}